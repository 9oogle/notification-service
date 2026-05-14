package com.goggles.notification.application.service;

import java.util.List;
import java.util.UUID;

public interface NotificationStatusService {
  void markSent(UUID notificationId);

  void markFailed(UUID notificationId, String reason);

  void markAllSent(List<UUID> notificationIds);

  void markAllFailed(List<UUID> notificationIds, String reason);
}
