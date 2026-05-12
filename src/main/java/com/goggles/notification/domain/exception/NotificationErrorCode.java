package com.goggles.notification.domain.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode {
  // NotificationChannel
  MISSING_NOTIFICATION_CHANNEL("알림 채널은 필수입니다."),
  INVALID_NOTIFICATION_CHANNEL("유효하지 않은 알림 채널입니다: %s"),

  // NotificationType
  MISSING_NOTIFICATION_TYPE("알림 타입은 필수입니다."),
  INVALID_NOTIFICATION_TYPE("유효하지 않은 알림 타입입니다: %s"),

  // ReceiverType
  MISSING_RECEIVER_TYPE("수신자 타입은 필수입니다."),
  INVALID_RECEIVER_TYPE("유효하지 않은 수신자 타입입니다: %s"),

  // Receiver
  MISSING_RECEIVER_ID("수신자 ID는 필수입니다."),

  // Reference
  MISSING_REFERENCE_TYPE("참조 타입은 필수입니다."),
  MISSING_REFERENCE_ID("참조 ID는 필수입니다."),

  // Notification
  MISSING_TITLE("알림 제목은 필수입니다."),
  INVALID_TITLE_LENGTH("알림 제목은 100자를 초과할 수 없습니다."),
  MISSING_CONTENT("알림 내용은 필수입니다."),
  INVALID_TEMPLATE_DATA("템플릿 데이터 직렬화에 실패했습니다."),
  INVALID_CONTENT_LENGTH("알림 내용은 1000자를 초과할 수 없습니다.");

  private final String message;

  public String getMessage(Object... args) {
    return String.format(message, args);
  }
}
