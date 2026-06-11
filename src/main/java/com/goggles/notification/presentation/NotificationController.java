package com.goggles.notification.presentation;

import com.goggles.notification.application.NotificationProcessor;
import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.presentation.dto.CreateNotificationRequest;
import jakarta.validation.Valid;
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

  @PostMapping("/{notificationType}/{channel}")
  public void sendNotification(
      @RequestBody @Valid CreateNotificationRequest request,
      @PathVariable String notificationType,
      @PathVariable String channel,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole) {

    notificationProcessor.process(request.toCommand(notificationType, channel));
  }

  @PostMapping("/{notificationType}/{channel}/bulk")
  public void sendBulkNotification(
      @RequestBody @Valid List<@Valid CreateNotificationRequest> requests,
      @PathVariable String notificationType,
      @PathVariable String channel,
      @RequestHeader("X-User-Id") UUID userId,
      @RequestHeader("X-User-Role") String userRole) {

    if (requests.isEmpty()) {
      throw new IllegalArgumentException("요청 목록이 비어있습니다");
    }

    List<SendNotificationCommand> commands =
        requests.stream().map(request -> request.toCommand(notificationType, channel)).toList();

    notificationProcessor.bulkProcess(commands);
  }
}
