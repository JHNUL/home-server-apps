package org.juhanir.message_server.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.mqtt.MqttClient;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.AwaitUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


@QuarkusTest
@QuarkusTestResource(
        value = MessageServerTestResource.class,
        restrictToAnnotatedClass = true
)
public class TemperatureStatusTest {

    private static MqttClient client;
    private static final String TOPIC = "shellyid-123123/status/temperature:0";

    @BeforeEach
    void setUp() {
        client = MqttClient.create(Vertx.vertx());
        client.connectAndAwait(1883, "localhost");
    }

    @AfterAll
    static void tearDown() {
        if (null != client && client.isConnected()) {
            client.disconnectAndAwait();
        }
    }

    @Test
    void canSendTemperatureStatusMessageToTopic() {
        String message = """
                {
                  "id": 0,
                  "tC": 19.6,
                  "tF": 67.3
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
    }

    @Test
    void sentTemperatureStatusMessageCanBeFetchedViaRestApi() {
        String message = """
                {
                  "id": 0,
                  "tC": 19.6,
                  "tF": 67.3
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);

        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get("/temperatures/shellyid-123123")
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", equalTo(1))
                    .body("[0].valueCelsius", equalTo(19.6f))
                    .body("[0].valueFahrenheit", equalTo(67.3f))
                    .body("[0].componentId", equalTo(0));
        });
    }

    @Test
    void temperatureStatusMessagesForNonExistingDeviceReturnsNotFound() {
        AwaitUtils.awaitAssertionMaintained(() -> {
            given()
                    .get("/temperatures/qwerty-asdfg-12345")
                    .then()
                    .statusCode(404);
        });
    }
}
