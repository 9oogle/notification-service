package simulation;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class SingleNotificationSimulation extends Simulation {

  private static final HttpProtocolBuilder httpProtocol = http
      .baseUrl("http://localhost:9012")
      .acceptHeader("application/json")
      .contentTypeHeader("application/json")
      .header("X-User-Id", "550e8400-e29b-41d4-a716-446655440000")
      .header("X-User-Role", "ADMIN");

  private static final FeederBuilder<Object> feeder = jsonFile("recipients.json").circular();

  private static final ScenarioBuilder scenario = scenario("단건 알림 600건")
      .feed(feeder)
      .exec(http("단건 알림 발송")
          .post("/api/v1/notifications/ORDER_CANCELLED/EMAIL")
          .body(StringBody(session -> {
            String receiverId = session.getString("receiverId");
            String receiverType = session.getString("receiverType");
            String receiverEmail = session.getString("receiverEmail");
            String receiverName = session.getString("receiverName");
            String referenceType = session.getString("referenceType");
            String referenceId = session.getString("referenceId");

            return """
                {
                    "receiverId": "%s",
                    "receiverType": "%s",
                    "receiverEmail": "%s",
                    "receiverName": "%s",
                    "referenceType": "%s",
                    "referenceId": "%s",
                    "title": "주문이 취소되었습니다.",
                    "content": "주문 취소 안내 메일입니다.",
                    "templateVariables": {
                        "receiverName": "%s",
                        "badge": "주문 취소",
                        "title": "주문이 정상적으로 취소되었습니다.",
                        "subtitle": "취소 내역을 아래에서 확인해 주세요.",
                        "orderId": "ORD-20260515-0001",
                        "orderName": "Apple iPhone 16 Pro",
                        "eventAt": "2026-05-15 14:30",
                        "amount": "1590000",
                        "cancelReason": "고객 요청에 의한 취소",
                        "supportEmail": "support@goggles.com"
                    }
                }
                """.formatted(receiverId, receiverType, receiverEmail, receiverName, referenceType, referenceId, receiverName);
          })).asJson()
          .check(status().is(200)));

  {
    setUp(
        scenario.injectOpen(atOnceUsers(600))
    )
        .assertions(
            global().responseTime().max().lt(5000),
            global().failedRequests().count().lt(1L)
        )
        .protocols(httpProtocol);
  }
}