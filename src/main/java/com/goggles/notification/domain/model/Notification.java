package com.goggles.notification.domain.model;

import com.goggles.common.domain.BaseTime;
import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification_logs")
public class Notification extends BaseTime {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "notification_log_id", updatable = false, nullable = false)
  private UUID notificationLogId;

  @Embedded
  private Receiver receiver;

  @Embedded
  private Reference reference;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 30, updatable = false)
  private NotificationType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "channel", nullable = false, length = 20, updatable = false)
  private NotificationChannel channel;

  @Column(name = "title", nullable = false, length = 100, updatable = false)
  private String title;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT", updatable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private NotificationStatus status;

  @Column(name = "sent_at")
  private LocalDateTime sentAt;

  @Column(name = "failure_reason", columnDefinition = "TEXT")
  private String failureReason;

  private Notification(
      Receiver receiver,
      Reference reference,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content) {
    this.receiver = receiver;
    this.reference = reference;
    this.type = type;
    this.channel = channel;
    this.title = title;
    this.content = content;
    this.status = NotificationStatus.SENT;
    this.sentAt = LocalDateTime.now();
  }

  public static Notification create(
      Receiver receiver,
      Reference reference,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content) {
    validate(type, channel, title, content);
    return new Notification(receiver, reference, type, channel, title, content);
  }

  private static void validate(
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content) {
    if (type == null) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_NOTIFICATION_TYPE);
    }
    if (channel == null) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_NOTIFICATION_CHANNEL);
    }
    if (title == null || title.isBlank()) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_TITLE);
    }
    if (title.length() > 100) {
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_TITLE_LENGTH);
    }
    if (content == null || content.isBlank()) {
      throw new InvalidNotificationException(NotificationErrorCode.MISSING_CONTENT);
    }
    if (content.length() > 1000) {
      throw new InvalidNotificationException(NotificationErrorCode.INVALID_CONTENT_LENGTH);
    }
  }

  public void markFailed(String failureReason) {
    this.status = NotificationStatus.FAILED;
    this.failureReason = failureReason;
  }
}
