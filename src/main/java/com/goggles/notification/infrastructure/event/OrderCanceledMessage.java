package com.goggles.notification.infrastructure.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCanceledMessage(
    UUID orderId,
    UUID customerId,
    String customerEmail,
    String customerName,
    String orderName,
    Long amount,
    Instant cancelledAt,
    String cancelReason) {}
