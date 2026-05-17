package com.goggles.notification.infrastructure.consumer.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidEmailEventPayloadException extends BadRequestException {

  public InvalidEmailEventPayloadException() {
    super("이메일 이벤트 payload가 올바르지 않습니다.");
  }
}
