package com.goggles.notification.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.common.event.annotation.IdempotentConsumer;
import com.goggles.notification.application.NotificationProcessor;
import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.domain.model.NotificationChannel;
import com.goggles.notification.domain.model.NotificationType;
import com.goggles.notification.domain.model.ReceiverType;
import com.goggles.notification.domain.model.ReferenceType;
import com.goggles.notification.infrastructure.consumer.exception.InvalidOrderEventPayloadException;
import com.goggles.notification.infrastructure.event.OrderCanceledEvent;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelConsumer {
  public static final String TOPIC = "order.canceled.v1";
  public static final String GROUP_NAME = "order-service.canceled";

  private final NotificationProcessor notificationProcessor;
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
      notificationProcessor.process(toCommand(record.value()));
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
      throw new RuntimeException("order.complete 처리 실패", e);
    }
  }

  private SendNotificationCommand toCommand(String value) {
    try {
      OrderCanceledEvent event = objectMapper.readValue(value, OrderCanceledEvent.class);
      return new SendNotificationCommand(
          event.customerId(),
          ReceiverType.USER,
          event.customerEmail(),
          event.customerName(),
          ReferenceType.ORDER,
          event.orderId(),
          NotificationType.ORDER_CANCELLED,
          NotificationChannel.EMAIL,
          NotificationType.ORDER_CANCELLED.getTitle(),
          NotificationType.ORDER_CANCELLED.formatContent(
              event.orderId(), event.amount(), event.cancelledAt()),
          Map.of(
              "subtitle", "주문 취소 내역을 확인해 주세요.",
              "orderId", event.orderId(),
              "orderName", event.orderName(),
              "amount", event.amount(),
              "eventAt", event.cancelledAt(),
              "cancelReason", event.cancelReason(),
              "supportEmail", "AnnieHa"));
    } catch (JsonProcessingException e) {
      throw new InvalidOrderEventPayloadException();
    }
  }
}
