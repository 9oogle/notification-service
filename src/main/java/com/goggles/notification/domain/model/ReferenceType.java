package com.goggles.notification.domain.model;

import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;

public enum ReferenceType {
  ORDER,
  PAYMENT,
  COUPON;

  public static ReferenceType from(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_REFERENCE_TYPE);
    }
    try {
      return ReferenceType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_RECEIVER_TYPE, value);
    }
  }
}
