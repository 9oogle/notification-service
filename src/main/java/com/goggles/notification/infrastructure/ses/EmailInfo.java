package com.goggles.notification.infrastructure.ses;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailInfo {

  private String from;
  private List<String> to;
  private String subject;
  private String content;

  @Builder
  public EmailInfo(String from, List<String> to, String subject, String content) {
    this.from = from;
    this.to = to;
    this.subject = subject;
    this.content = content;
  }

  public SendEmailRequest toSendEmailRequest() {
    Destination destination = Destination.builder().toAddresses(this.to).build();

    Message message =
        Message.builder()
            .subject(createContent(this.subject))
            .body(Body.builder().html(createContent(this.content)).build())
            .build();

    EmailContent emailContent = EmailContent.builder().simple(message).build();

    return SendEmailRequest.builder()
        .fromEmailAddress(this.from)
        .destination(destination)
        .content(emailContent)
        .build();
  }

  private Content createContent(String text) {
    return Content.builder().charset("UTF-8").data(text).build();
  }
}
