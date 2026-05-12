package com.goggles.notification.domain.model;

import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationChannel {
  EMAIL("이메일"),
  SLACK("슬랙"),
  PUSH("푸시");

  private final String displayName;

  public static NotificationChannel from(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_NOTIFICATION_CHANNEL);
    }
    try {
      return NotificationChannel.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidNotificationException(
          NotificationErrorCode.INVALID_NOTIFICATION_CHANNEL, value);
    }
  }
}
