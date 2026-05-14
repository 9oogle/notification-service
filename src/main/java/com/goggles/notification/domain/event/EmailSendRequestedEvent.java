package com.goggles.notification.domain.event;

import java.util.Map;
import java.util.UUID;

public record EmailSendRequestedEvent(
    UUID notificationId,
    String receiverEmail,
    String receiverName,
    String title,
    String content,
    Map<String, Object> templateVariables) {}
