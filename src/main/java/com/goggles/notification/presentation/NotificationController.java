package com.goggles.notification.presentation;

import com.goggles.notification.application.NotificationProcessor;
import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.presentation.dto.CreateNotificationRequest;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {
  private final NotificationProcessor notificationProcessor;

  @PostMapping("/{notificationType}/bulk")
  public void sendBulkReminder(
      @RequestBody List<CreateNotificationRequest> requests,
      @PathVariable String notificationType,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole) {

    List<SendNotificationCommand> commands = requests.stream()
        .map(request -> request.toCommand(notificationType))
        .toList();

    notificationProcessor.bulkProcess(commands, notificationType);
  }
}
