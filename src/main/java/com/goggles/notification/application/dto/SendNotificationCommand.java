package com.goggles.notification.application.dto;

import com.goggles.notification.domain.model.NotificationChannel;
import com.goggles.notification.domain.model.NotificationType;
import com.goggles.notification.domain.model.ReceiverType;
import com.goggles.notification.domain.model.ReferenceType;
import java.util.Map;
import java.util.UUID;

public record SendNotificationCommand(
    UUID receiverId,
    ReceiverType receiverType,
    String receiverEmail,
    String receiverName,
    ReferenceType referenceType,
    UUID referenceId,
    NotificationType type,
    NotificationChannel channel,
    String title,
    String content,
    Map<String, Object> templateVariables) {}
