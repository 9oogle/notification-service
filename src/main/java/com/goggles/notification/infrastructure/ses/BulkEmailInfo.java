package com.goggles.notification.infrastructure.ses;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.sesv2.model.BulkEmailEntry;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.ReplacementEmailContent;
import software.amazon.awssdk.services.sesv2.model.ReplacementTemplate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BulkEmailInfo {

  private String to;
  private String templateData;

  @Builder
  public BulkEmailInfo(String to, String templateData) {
    this.to = to;
    this.templateData = templateData;
  }

  public BulkEmailEntry toSesEntry() {
    Destination destination = Destination.builder().toAddresses(this.to).build();

    ReplacementTemplate replacementTemplate =
        ReplacementTemplate.builder().replacementTemplateData(this.templateData).build();

    ReplacementEmailContent replacementEmailContent =
        ReplacementEmailContent.builder().replacementTemplate(replacementTemplate).build();

    return BulkEmailEntry.builder()
        .destination(destination)
        .replacementEmailContent(replacementEmailContent)
        .build();
  }
}
