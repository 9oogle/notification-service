package com.goggles.notification.domain.repository;

import com.goggles.notification.domain.model.Notification;
import java.util.List;

public interface NotificationRepository {
  Notification createNotification(Notification notification);

  void createNotifications(List<Notification> notifications);
}
