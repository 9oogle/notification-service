package com.goggles.notification.infrastructure.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.goggles.notification.application.NotificationProcessor;
import com.goggles.notification.application.dto.SendNotificationCommand;
import com.goggles.notification.domain.model.NotificationChannel;
import com.goggles.notification.domain.model.NotificationType;
import com.goggles.notification.domain.model.ReceiverType;
import com.goggles.notification.domain.model.ReferenceType;
import com.goggles.notification.infrastructure.consumer.exception.InvalidOrderEventPayloadException;
import com.goggles.notification.infrastructure.event.OrderCompleteEvent;
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
public class OrderCompleteConsumer {
  public static final String TOPIC = "order.completed.v1";
  public static final String GROUP_NAME = "order-service.complete";

  private final NotificationProcessor notificationProcessor;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = TOPIC, groupId = GROUP_NAME)
  //  @IdempotentConsumer(GROUP_NAME)
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
      OrderCompleteEvent event = objectMapper.readValue(value, OrderCompleteEvent.class);
      return new SendNotificationCommand(
          event.customerId(),
          ReceiverType.USER,
          event.customerEmail(),
          event.customerName(),
          ReferenceType.ORDER,
          event.orderId(),
          NotificationType.ORDER_COMPLETED,
          NotificationChannel.EMAIL,
          NotificationType.ORDER_COMPLETED.getTitle(),
          NotificationType.ORDER_COMPLETED.formatContent(
              event.orderId(), event.amount(), event.approvedAt()),
          Map.of(
              "subtitle", "주문 내역을 확인해 주세요.",
              "orderId", event.orderId(),
              "orderName", event.orderName(),
              "amount", event.amount(),
              "eventAt", event.approvedAt(),
              "supportEmail", "AnnieHa"));
    } catch (JsonProcessingException e) {
      throw new InvalidOrderEventPayloadException();
    }
  }
}
