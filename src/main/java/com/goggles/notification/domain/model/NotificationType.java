package com.goggles.notification.domain.model;

import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
  // 사용자
  ORDER_CREATED("주문 생성"),
  ORDER_CANCELLED("주문 취소"),
  PAYMENT_COMPLETED("결제 완료"),
  PAYMENT_FAILED("결제 실패"),
  COUPON_ISSUED("쿠폰 발급"),
  // 운영팀
  COMPENSATION_FAILED("보상 트랜잭션 실패"),
  SYSTEM_ERROR("시스템 오류");

  private final String displayName;

  public static NotificationType from(String value) {
    if (value == null || value.isBlank()) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_NOTIFICATION_TYPE);
    }
    try {
      return NotificationType.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_NOTIFICATION_TYPE, value);
    }
  }
}