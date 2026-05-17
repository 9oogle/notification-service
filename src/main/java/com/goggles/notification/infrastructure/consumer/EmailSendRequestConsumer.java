package com.goggles.notification.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.notification.application.handler.EmailNotificationEventHandler;
import com.goggles.notification.domain.event.EmailSendRequestedEvent;
import com.goggles.notification.infrastructure.consumer.exception.InvalidEmailEventPayloadException;
import com.goggles.notification.infrastructure.consumer.exception.InvalidOrderEventPayloadException;
import com.goggles.notification.infrastructure.event.EmailSendRequestedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSendRequestConsumer {
  public static final String TOPIC = "notification.email-send.v1";
  public static final String GROUP_NAME = "notification-service.email-send";

  private final EmailNotificationEventHandler handler;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = TOPIC, groupId = GROUP_NAME)
  @IdempotentConsumer(GROUP_NAME)
  public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
    log.info(
        "[Kafka] Received {} | partition={}, offset={}",
        TOPIC,
        record.partition(),
        record.offset());

    try {
      handler.handle(toEvent(record.value()));
      ack.acknowledge();
    } catch (InvalidOrderEventPayloadException e) {
      log.error(
          "페이로드 파싱 실패, 스킵 처리 topic={}, partition={}, offset={}",
          TOPIC,
          record.partition(),
          record.offset(),
          e);
      ack.acknowledge();
    } catch (Exception e) {
      log.error(
          "처리 실패, 재처리 예정 topic={}, partition={}, offset={}",
          TOPIC,
          record.partition(),
          record.offset(),
          e);
      throw new RuntimeException("bulk-email-send 처리 실패", e);
    }
  }

  private EmailSendRequestedEvent toEvent(String value) {
    try {
      EmailSendRequestedMessage message =
          objectMapper.readValue(value, EmailSendRequestedMessage.class);
      return new EmailSendRequestedEvent(
          message.notificationId(),
          message.receiverEmail(),
          message.receiverName(),
          message.title(),
          message.content(),
          message.templateVariables());
    } catch (JsonProcessingException e) {
      throw new InvalidEmailEventPayloadException();
    }
  }
}
