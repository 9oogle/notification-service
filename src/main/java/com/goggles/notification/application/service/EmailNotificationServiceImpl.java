package com.goggles.notification.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.model.Receiver;
import com.goggles.notification.domain.model.Reference;
import com.goggles.notification.domain.repository.NotificationRepository;
import com.goggles.notification.infrastructure.ses.BulkEmailInfo;
import com.goggles.notification.infrastructure.ses.EmailInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.BulkEmailContent;
import software.amazon.awssdk.services.sesv2.model.BulkEmailEntry;
import software.amazon.awssdk.services.sesv2.model.SendBulkEmailRequest;
import software.amazon.awssdk.services.sesv2.model.Template;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationServiceImpl implements NotificationService {

  @Value("${aws.ses.send-mail-from}")
  private String sender;

  private final SesV2Client sesV2Client;
  private final TemplateEngine templateEngine;
  private final ObjectMapper objectMapper;
  private final NotificationRepository notificationRepository;

  @Override
  public void sendNotification(SendNotificationCommand command) {
    Context context = new Context();
    context.setVariable("title", command.title());
    context.setVariable("receiverName", command.receiverName());
    context.setVariable("supportEmail", "AnnieHa@goggles.com");
    command.templateVariables().forEach(context::setVariable);

    String rendered = templateEngine.process("notification-email", context);

    EmailInfo emailInfo =
        EmailInfo.builder()
            .from(sender)
            .to(List.of(command.receiverEmail()))
            .subject(command.title())
            .content(rendered)
            .build();

    Notification notification = toNotification(command);
    notificationRepository.createNotification(notification);

    sesV2Client.sendEmail(emailInfo.toSendEmailRequest());
  }

  @Override
  public void sendBulkNotification(List<SendNotificationCommand> commands) {
    List<List<SendNotificationCommand>> partitions = partition(commands, 50);
    List<Notification> notifications = commands.stream().map(this::toNotification).toList();
    notificationRepository.createNotifications(notifications);
    partitions.forEach(this::sendBulkPartition);
  }

  private List<List<SendNotificationCommand>> partition(
      List<SendNotificationCommand> list, int size) {
    return IntStream.range(0, (list.size() + size - 1) / size)
        .mapToObj(i -> list.subList(i * size, Math.min(i * size + size, list.size())))
        .toList();
  }

  private void sendBulkPartition(List<SendNotificationCommand> commands) {
    List<BulkEmailEntry> entries =
        commands.stream()
            .map(
                command -> {
                  return new BulkEmailInfo(command.receiverEmail(), toTemplateData(command))
                      .toSesEntry();
                })
            .toList();

    Template template =
        Template.builder()
            .templateName("notification-email-template")
            .templateData("{\"receiverName\":\"고객\"}")
            .build();

    BulkEmailContent bulkEmailContent = BulkEmailContent.builder().template(template).build();

    SendBulkEmailRequest request =
        SendBulkEmailRequest.builder()
            .fromEmailAddress(sender)
            .bulkEmailEntries(entries)
            .defaultContent(bulkEmailContent)
            .build();

    try {
      sesV2Client.sendBulkEmail(request);
    } catch (Exception e) {
      log.error("[Bulk Email] 파티션 발송 실패. cause: {}", e.getMessage(), e);
      throw e;
    }
  }

  private String toTemplateData(SendNotificationCommand command) {
    Map<String, Object> data = new HashMap<>(command.templateVariables());
    data.put("receiverName", command.receiverName());
    data.put("title", command.title());
    data.put("supportEmail", "AnnieHa@goggles.com");

    try {
      return objectMapper.writeValueAsString(data);
    } catch (JsonProcessingException e) {
      log.error("[Bulk Email] 템플릿 데이터 직렬화 실패. cause: {}", e.getMessage(), e);
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_TEMPLATE_DATA);
    }
  }

  public Notification toNotification(SendNotificationCommand command) {
    return Notification.create(
        new Receiver(command.receiverId(), command.receiverType()),
        new Reference(command.referenceType(), command.referenceId()),
        command.type(),
        command.channel(),
        command.title(),
        command.content());
  }
}
