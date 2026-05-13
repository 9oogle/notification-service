package com.goggles.notification.presentation.dto;

import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.domain.model.NotificationChannel;
import com.goggles.notification.domain.model.NotificationType;
import com.goggles.notification.domain.model.ReceiverType;
import com.goggles.notification.domain.model.ReferenceType;
import java.util.Map;
import java.util.UUID;

public record CreateNotificationRequest(
    UUID receiverId,
    String receiverType,
    String receiverEmail,
    String receiverName,
    String referenceType,
    UUID referenceId,
    String title,
    String content,
    Map<String, Object> templateVariables) {
  public SendNotificationCommand toCommand(String notificationType, String channel) {
    return new SendNotificationCommand(
        receiverId,
        ReceiverType.from(receiverType),
        receiverEmail,
        receiverName,
        ReferenceType.from(referenceType),
        referenceId,
        NotificationType.from(notificationType),
        NotificationChannel.from(channel),
        title,
        content,
        templateVariables != null ? templateVariables : Map.of());
  }
}
