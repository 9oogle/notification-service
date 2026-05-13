package com.goggles.notification.application;

import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.application.service.NotificationService;
import com.goggles.notification.domain.model.NotificationChannel;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationProcessor {

  private final Map<NotificationChannel, NotificationService> notificationServiceMap;

  public void process(SendNotificationCommand command) {
    getService(command.channel()).sendNotification(command);
  }

  public void bulkProcess(List<SendNotificationCommand> commands) {
    commands.stream()
        .collect(Collectors.groupingBy(SendNotificationCommand::channel))
        .forEach((channel, grouped) ->
            getService(channel).sendBulkNotification(grouped)
        );
  }

  private NotificationService getService(NotificationChannel channel) {
    NotificationService service = notificationServiceMap.get(channel);
    if (service == null) {
      throw new IllegalArgumentException("지원하지 않는 알림 채널: " + channel);
    }
    return service;
  }
}
