package com.goggles.notification.infrastructure.ses;

import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.AlreadyExistsException;
import software.amazon.awssdk.services.sesv2.model.CreateEmailTemplateRequest;
import software.amazon.awssdk.services.sesv2.model.EmailTemplateContent;
import software.amazon.awssdk.services.sesv2.model.UpdateEmailTemplateRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class SesTemplateInitializer implements ApplicationRunner {

  private final SesV2Client sesV2Client;

  @Override
  public void run(ApplicationArguments args) {
    try {
      ClassPathResource resource =
          new ClassPathResource("templates/notification-email-bulk.html");
      String htmlContent =
          new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

      EmailTemplateContent templateContent =
          EmailTemplateContent.builder().subject("{{title}}").html(htmlContent).build();

      try {
        sesV2Client.createEmailTemplate(
            CreateEmailTemplateRequest.builder()
                .templateName("notification-email-template")
                .templateContent(templateContent)
                .build());
        log.info("[SES] 템플릿 등록 완료.");
      } catch (AlreadyExistsException e) {
        sesV2Client.updateEmailTemplate(
            UpdateEmailTemplateRequest.builder()
                .templateName("notification-email-template")
                .templateContent(templateContent)
                .build());
        log.info("[SES] 템플릿 업데이트 완료.");
      }
    } catch (SdkException e) {
      log.warn("[SES] 템플릿 초기화 실패 — 서비스는 계속 기동됩니다. 원인: {}", e.getMessage());
    } catch (Exception e) {
      log.warn("[SES] 템플릿 초기화 중 예외 발생 — 서비스는 계속 기동됩니다. 원인: {}", e.getMessage());
    }
  }
}
