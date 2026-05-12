package com.goggles.notification.domain.model;

import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
  // 사용자
  ORDER_COMPLETED("주문 완료", "주문이 완료되었습니다.", "주문이 완료되었습니다.\n주문번호: %s\n결제금액: %s원\n결제일시: %s"),
  ORDER_CANCELLED("주문 취소", "주문이 취소되었습니다.", "주문이 취소되었습니다.\n주문번호: %s"),
  PAYMENT_COMPLETED("결제 완료", "결제가 완료되었습니다.", "결제가 완료되었습니다.\n주문번호: %s\n결제금액: %s원\n결제일시: %s"),
  PAYMENT_FAILED("결제 실패", "결제에 실패했습니다.", "결제에 실패했습니다.\n주문번호: %s"),
  COUPON_ISSUED("쿠폰 발급", "쿠폰이 발급되었습니다.", "쿠폰이 발급되었습니다."),

  // 운영팀
  COMPENSATION_FAILED("보상 실패", "보상 트랜잭션이 실패했습니다.", "[SYSTEM] 보상 트랜잭션 실패\n주문번호: %s\n원인: %s"),
  SYSTEM_ERROR("시스템 오류", "시스템 오류가 발생했습니다.", "[SYSTEM] 시스템 오류 발생\n주문번호: %s\n원인: %s");

  private final String displayName;
  private final String title;
  private final String contentTemplate;

  public String formatContent(Object... args) {
    return String.format(contentTemplate, args);
  }

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