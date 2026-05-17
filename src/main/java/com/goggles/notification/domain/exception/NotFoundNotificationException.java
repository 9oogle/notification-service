package com.goggles.notification.domain.exception;

import com.goggles.common.exception.NotFoundException;

public class NotFoundNotificationException extends NotFoundException {

  public NotFoundNotificationException() {
    super("알림을 찾을 수 없습니다.");
  }
}
