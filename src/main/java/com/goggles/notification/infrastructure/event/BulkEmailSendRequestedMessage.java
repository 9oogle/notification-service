package com.goggles.notification.infrastructure.event;

import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BulkEmailSendRequestedMessage(
    List<UUID> notificationIds, List<BulkEmailTarget> targets) {
  public record BulkEmailTarget(
      UUID notificationId,
      String receiverEmail,
      String receiverName,
      String title,
      Map<String, Object> templateVariables) {
    public static BulkEmailTarget from(BulkEmailSendRequestedEvent.BulkEmailTarget target) {
      return new BulkEmailTarget(
          target.notificationId(),
          target.receiverEmail(),
          target.receiverName(),
          target.title(),
          target.templateVariables());
    }
  }

  public static BulkEmailSendRequestedMessage from(BulkEmailSendRequestedEvent event) {
    return new BulkEmailSendRequestedMessage(
        event.notificationIds(), event.targets().stream().map(BulkEmailTarget::from).toList());
  }
}
