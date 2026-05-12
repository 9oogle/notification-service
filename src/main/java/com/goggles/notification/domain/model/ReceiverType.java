package com.goggles.notification.domain.model;

import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReceiverType {
  USER("사용자"),
  ADMIN("관리자");

  private final String displayName;

  public static ReceiverType from(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_RECEIVER_TYPE);
    }
    try {
      return ReceiverType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_RECEIVER_TYPE, value);
    }
  }
}