package com.goggles.notification.infrastructure.ses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.notification.application.port.EmailSender;
import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import com.goggles.notification.domain.event.EmailSendRequestedEvent;
import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import com.goggles.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.BulkEmailContent;
import software.amazon.awssdk.services.sesv2.model.BulkEmailEntry;
import software.amazon.awssdk.services.sesv2.model.SendBulkEmailRequest;
import software.amazon.awssdk.services.sesv2.model.Template;

@Component
@RequiredArgsConstructor
@Slf4j
public class SesEmailSender implements EmailSender {
  @Value("${aws.ses.send-mail-from}")
  private String sender;

  @Value("${support.email}")
  private String supportEmail;

  private final SesV2Client sesV2Client;
  private final TemplateEngine templateEngine;
  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;

  @Override
  public void send(EmailSendRequestedEvent event) {
    Context context = new Context();
    context.setVariable("title", event.title());
    context.setVariable("receiverName", event.receiverName());
    context.setVariable("supportEmail", supportEmail);
    event.templateVariables().forEach(context::setVariable);

    String rendered = templateEngine.process("notification-email", context);

    EmailInfo emailInfo =
        EmailInfo.builder()
            .from(sender)
            .to(List.of(event.receiverEmail()))
            .subject(event.title())
            .content(rendered)
            .build();

    sesV2Client.sendEmail(emailInfo.toSendEmailRequest());
  }

  @Override
  public void sendBulk(BulkEmailSendRequestedEvent event) {
    List<BulkEmailSendRequestedEvent.BulkEmailTarget> targets = event.targets();
    List<List<BulkEmailSendRequestedEvent.BulkEmailTarget>> partitions = partition(targets, 50);

    try {
      partitions.forEach(this::sendBulkPartition);
      notificationRepository.updateNotificationsSent(event.notificationIds(), LocalDateTime.now());
    } catch (Exception e) {
      log.error("[Bulk Email] 발송 실패. cause: {}", e.getMessage(), e);
      notificationRepository.updateNotificationsFailed(event.notificationIds(), "서버 에러로 인한 발송 실패");
      throw e;
    }
  }

  private void sendBulkPartition(List<BulkEmailSendRequestedEvent.BulkEmailTarget> targets) {
    List<BulkEmailEntry> entries =
        targets.stream()
            .map(
                target ->
                    new BulkEmailInfo(target.receiverEmail(), toTemplateData(target)).toSesEntry())
            .toList();

    Template defaultTemplate =
        Template.builder()
            .templateName("notification-email-template")
            .templateData("{\"receiverName\":\"고객\"}")
            .build();

    SendBulkEmailRequest request =
        SendBulkEmailRequest.builder()
            .fromEmailAddress(sender)
            .bulkEmailEntries(entries)
            .defaultContent(BulkEmailContent.builder().template(defaultTemplate).build())
            .build();

    try {
      sesV2Client.sendBulkEmail(request);
    } catch (Exception e) {
      log.error("[Bulk Email] 파티션 발송 실패. cause: {}", e.getMessage(), e);
      throw e;
    }
  }

  private String toTemplateData(BulkEmailSendRequestedEvent.BulkEmailTarget target) {
    Map<String, Object> data = new HashMap<>(target.templateVariables());
    data.put("receiverName", target.receiverName());
    data.put("title", target.title());
    data.put("supportEmail", supportEmail);

    try {
      return objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      log.error("[Bulk Email] 템플릿 데이터 직렬화 실패. notificationId: {}", e.getMessage(), e);
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_TEMPLATE_DATA);
    }
  }

  private <T> List<List<T>> partition(List<T> list, int size) {
    return IntStream.range(0, (list.size() + size - 1) / size)
        .mapToObj(i -> list.subList(i * size, Math.min(i * size + size, list.size())))
        .toList();
  }
}
