package com.goggles.notification.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.notification.application.handler.EmailNotificationEventHandler;
import com.goggles.notification.domain.event.BulkEmailSendRequestedEvent;
import com.goggles.notification.infrastructure.consumer.exception.InvalidEmailEventPayloadException;
import com.goggles.notification.infrastructure.consumer.exception.InvalidOrderEventPayloadException;
import com.goggles.notification.infrastructure.event.BulkEmailSendRequestedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkEmailSendRequestConsumer {
  public static final String TOPIC = "notification.bulk-email-send.v1";
  public static final String GROUP_NAME = "notification-service.bulk-email-send";

  private final EmailNotificationEventHandler handler;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = TOPIC, groupId = GROUP_NAME)
  @IdempotentConsumer(GROUP_NAME)
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    log.info("[Kafka] Received {} | partition={}, offset={}", TOPIC, record.partition(), record.offset());

    try {
      handler.bulkHandle(toEvent(record.value()));
      ack.acknowledge();
    } catch (InvalidOrderEventPayloadException e) {
      log.error("페이로드 파싱 실패, 스킵 처리 topic={}, partition={}, offset={}", TOPIC, record.partition(), record.offset(), e);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("처리 실패, 재처리 예정 topic={}, partition={}, offset={}", TOPIC, record.partition(), record.offset(), e);
      throw new RuntimeException("email-send 처리 실패", e);
    }
  }

  private BulkEmailSendRequestedEvent toEvent(String value) {
    try {
      BulkEmailSendRequestedMessage message =
          objectMapper.readValue(value, BulkEmailSendRequestedMessage.class);
      return new BulkEmailSendRequestedEvent(
          message.notificationIds(),
          message.targets().stream()
              .map(t -> new BulkEmailSendRequestedEvent.BulkEmailTarget(
                  t.notificationId(),
                  t.receiverEmail(),
                  t.receiverName(),
                  t.title(),
                  t.templateVariables()
              ))
              .toList()
      );
    } catch (JsonProcessingException e) {
      throw new InvalidEmailEventPayloadException();
    }
  }
}
