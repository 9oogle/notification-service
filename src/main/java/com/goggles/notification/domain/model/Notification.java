package com.goggles.notification.domain.model;

import com.goggles.common.domain.BaseTime;
import com.goggles.notification.domain.exception.InvalidNotificationException;
import com.goggles.notification.domain.exception.NotificationErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

  @Id private UUID id;

  @Embedded private Receiver receiver;

  @Embedded private Reference reference;

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

  public static Notification create(
      Receiver receiver,
      Reference reference,
      NotificationType type,
      NotificationChannel channel,
      String title,
      String content) {
    validate(type, channel, title, content);
    Notification notification = new Notification();
    notification.id = UUID.randomUUID();
    notification.receiver = receiver;
    notification.reference = reference;
    notification.type = type;
    notification.channel = channel;
    notification.title = title;
    notification.content = content;
    notification.status = NotificationStatus.PENDING;

    return notification;
  }

  public void sentNotification() {
    this.sentAt = LocalDateTime.now();
    transitionTo(NotificationStatus.SENT);
  }

  public void failedNotification(String failureReason) {
    this.failureReason = failureReason;
    transitionTo(NotificationStatus.FAILED);
  }

  private void transitionTo(NotificationStatus next) {
    if (!this.status.canTransitionTo(next)) {
      throw new InvalidNotificationException(
          NotificationErrorCode.INVALID_NOTIFICATION_STATUS,
          this.status.getDisplayName(),
          next.getDisplayName());
    }
    this.status = next;
  }

  private static void validate(
      NotificationType type, NotificationChannel channel, String title, String content) {
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
