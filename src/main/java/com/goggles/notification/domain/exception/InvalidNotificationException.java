package com.goggles.notification.domain.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidNotificationException extends BadRequestException {

  private final NotificationErrorCode errorCode;

  public InvalidNotificationException(NotificationErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public InvalidNotificationException(NotificationErrorCode errorCode, Object... args) {
    super(errorCode.getMessage(args));
    this.errorCode = errorCode;
  }
}