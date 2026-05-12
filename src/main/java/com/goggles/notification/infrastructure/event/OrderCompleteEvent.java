package com.goggles.notification.infrastructure.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCompleteEvent(
    UUID orderId,
    UUID customerId,
    String customerName,
    String customerEmail,
    String orderName,
    Long amount,
    Instant approvedAt) {}
