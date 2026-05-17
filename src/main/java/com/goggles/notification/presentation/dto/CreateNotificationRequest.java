package com.goggles.notification.presentation.dto;

import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.domain.model.NotificationChannel;
import com.goggles.notification.domain.model.NotificationType;
import com.goggles.notification.domain.model.ReceiverType;
import com.goggles.notification.domain.model.ReferenceType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.UUID;

public record CreateNotificationRequest(
    @NotNull(message = "수신자 ID는 필수입니다") UUID receiverId,
    @NotBlank(message = "수신자 타입은 필수입니다") String receiverType,
    @NotBlank(message = "수신자 이메일은 필수입니다") @Email(message = "올바른 이메일 형식이 아닙니다") String receiverEmail,
    @NotBlank(message = "수신자 이름은 필수입니다") String receiverName,
    @NotBlank(message = "참조 타입은 필수입니다") String referenceType,
    @NotNull(message = "참조 ID는 필수입니다") UUID referenceId,
    @NotBlank(message = "제목은 필수입니다") @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
        String title,
    @NotBlank(message = "내용은 필수입니다") String content,
    Map<String, Object> templateVariables) {
  public SendNotificationCommand toCommand(String notificationType, String channel) {
    return new SendNotificationCommand(
        receiverId,
        ReceiverType.from(receiverType),
        receiverEmail,
        receiverName,
        ReferenceType.from(referenceType),
        referenceId,
        NotificationType.from(notificationType),
        NotificationChannel.from(channel),
        title,
        content,
        templateVariables != null ? templateVariables : Map.of());
  }
}
