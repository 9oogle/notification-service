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
public class Reference {

  @Enumerated(EnumType.STRING)
  @Column(name = "reference_type", nullable = false, length = 30, updatable = false)
  private ReferenceType referenceType;

  @Column(name = "reference_id", nullable = false, updatable = false)
  private UUID referenceId;

  private Reference(ReferenceType referenceType, UUID referenceId) {
    validate(referenceType, referenceId);
    this.referenceType = referenceType;
    this.referenceId = referenceId;
  }

  private static void validate(ReferenceType referenceType, UUID referenceId) {
    if (referenceType == null) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_REFERENCE_TYPE);
    }
    if (referenceId == null) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_REFERENCE_ID);
    }
  }
}
