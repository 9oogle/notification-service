package com.goggles.notification.domain.repository;

import com.goggles.notification.domain.model.Notification;
import com.goggles.notification.domain.model.NotificationStatus;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository {
  Notification createNotification(Notification notification);

  List<Notification> createNotifications(List<Notification> notifications);

  void updateNotificationsSent(List<UUID> ids);
  void updateNotificationsFailed(List<UUID> ids, String failureReason);
}
