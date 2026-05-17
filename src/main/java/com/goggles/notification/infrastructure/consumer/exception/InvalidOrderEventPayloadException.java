package com.goggles.notification.infrastructure.consumer.exception;

import com.goggles.common.exception.BadRequestException;

public class InvalidOrderEventPayloadException extends BadRequestException {

  public InvalidOrderEventPayloadException() {
    super("주문 이벤트 payload가 올바르지 않습니다.");
  }
}
