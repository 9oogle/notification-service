package com.goggles.notification.application;

import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.application.service.NotificationService;
import com.goggles.notification.domain.model.NotificationChannel;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationProcessor {

  private final Map<NotificationChannel, NotificationService> notificationServiceMap;

  public void process(SendNotificationCommand command) {
    notificationServiceMap.get(command.channel()).sendNotification(command);
  }

  public void bulkProcess(List<SendNotificationCommand> commands, String channel) {
    notificationServiceMap.get(NotificationChannel.from(channel)).sendBulkNotification(commands);
  }
}
