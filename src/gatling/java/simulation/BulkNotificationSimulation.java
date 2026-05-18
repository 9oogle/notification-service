package simulation;

import static io.gatling.javaapi.core.CoreDsl.ElFileBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class BulkNotificationSimulation extends Simulation {

  private static final HttpProtocolBuilder httpProtocol = http
      .baseUrl("http://localhost:9012")
      .acceptHeader("application/json")
      .contentTypeHeader("application/json")
      .header("X-User-Id", "550e8400-e29b-41d4-a716-446655440000")
      .header("X-User-Role", "ADMIN");

  private static final ScenarioBuilder scenario = scenario("Bulk 알림 600건")
      .exec(http("Bulk 알림 발송")
          .post("/api/v1/notifications/ORDER_COMPLETED/EMAIL/bulk")
          .body(ElFileBody("bulk_600_recipients.json")).asJson()
          .check(status().is(200)));

  {
    setUp(
        scenario.injectOpen(atOnceUsers(1))
    )
        .assertions(
            global().responseTime().max().lt(5000),
            global().failedRequests().count().lt(1L)
        )
        .protocols(httpProtocol);
  }
}