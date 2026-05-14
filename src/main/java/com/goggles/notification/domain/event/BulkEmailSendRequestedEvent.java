package com.goggles.notification.domain.event;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BulkEmailSendRequestedEvent(
    List<UUID> notificationIds,
    List<BulkEmailTarget> targets
) {
  public record BulkEmailTarget(
      UUID notificationId,
      String receiverEmail,
      String receiverName,
      String title,
      Map<String, Object> templateVariables
  ) {}
}