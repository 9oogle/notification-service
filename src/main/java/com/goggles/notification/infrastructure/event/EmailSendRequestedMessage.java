package com.goggles.notification.infrastructure.event;

import java.util.Map;
import java.util.UUID;

public record EmailSendRequestedMessage(
    UUID notificationId,
    String receiverEmail,
    String receiverName,
    String title,
    String content,
    Map<String, Object> templateVariables
) {}