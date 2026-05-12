package com.goggles.notification.domain.model;

import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Receiver {

  @Column(name = "receiver_id", nullable = false, updatable = false)
  private UUID receiverId;

  @Enumerated(EnumType.STRING)
  @Column(name = "receiver_type", nullable = false, length = 20, updatable = false)
  private ReceiverType receiverType;

  private Receiver(UUID receiverId, ReceiverType receiverType) {
    validate(receiverId, receiverType);
    this.receiverId = receiverId;
    this.receiverType = receiverType;
  }

  private static void validate(UUID receiverId, ReceiverType receiverType) {
    if (receiverId == null) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_RECEIVER_ID);
    }
    if (receiverType == null) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_RECEIVER_TYPE);
    }
  }
}