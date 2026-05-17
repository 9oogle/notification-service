package com.goggles.notification.application.service;

import com.goggles.notification.application.dto.SendNotificationCommand;
import java.util.List;

public interface NotificationService {
  void sendNotification(SendNotificationCommand command);

  void sendBulkNotification(List<SendNotificationCommand> commands);
}
