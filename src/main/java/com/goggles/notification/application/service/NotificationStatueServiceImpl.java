package com.goggles.notification.application.service;

import com.goggles.notification.domain.exception.NotFoundNotificationException;
import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.model.NotificationStatus;
import com.goggles.notification.domain.repository.NotificationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationStatueServiceImpl implements NotificationStatusService {
  private final NotificationRepository notificationRepository;

  @Transactional
  public void markSent(UUID notificationId) {
    notificationRepository
        .findById(notificationId)
        .orElseThrow(NotFoundNotificationException::new)
        .sentNotification();
  }

  @Transactional
  public void markFailed(UUID notificationId, String reason) {
    notificationRepository
        .findById(notificationId)
        .orElseThrow(NotFoundNotificationException::new)
        .failedNotification(reason);
  }

  @Transactional
  public void markAllSent(List<UUID> notificationIds) {
    List<Notification> notifications = notificationRepository.findByIds(notificationIds);
    notifications.forEach(n -> n.transitionTo(NotificationStatus.SENT));
    notificationRepository.updateNotificationsSent(notificationIds, LocalDateTime.now());
  }

  @Transactional
  public void markAllFailed(List<UUID> notificationIds, String reason) {
    List<Notification> notifications = notificationRepository.findByIds(notificationIds);
    notifications.forEach(n -> n.transitionTo(NotificationStatus.FAILED));
    notificationRepository.updateNotificationsFailed(notificationIds, reason);
  }
}
