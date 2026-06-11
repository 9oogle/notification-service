package com.goggles.notification.domain.repository;

import com.goggles.notification.domain.model.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
  Notification createNotification(Notification notification);

  List<Notification> createNotifications(List<Notification> notifications);

  void updateNotificationsSent(List<UUID> ids, LocalDateTime sentAt);

  void updateNotificationsFailed(List<UUID> ids, String failureReason);

  Optional<Notification> findById(UUID id);
}
