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
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityStatusTest {

    private static MqttClient client;
    private static final String TOPIC = "shellyid-123123/status/humidity:0";
    private static final String HUMIDITY_URL_TPL = "/devices/%s/temperature";

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
    void canSendHumidityStatusMessageToTopic() {
        String message = """
                {
                  "id": 0,
                  "rh": 57
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
    }

    @Test
    void sentHumidityStatusMessageCanBeFetchedViaRestApi() {
        String message = """
                {
                  "id": 0,
                  "rh": 57
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);

        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get("devices/shellyid-123123/humidity")
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", equalTo(1))
                    .body("[0].value", equalTo(57.0f))
                    .body("[0].componentId", equalTo(0));
        });
    }

    @Test
    void humidityStatusMessagesForNonExistingDeviceReturnsNotFound() {
        AwaitUtils.awaitAssertionMaintained(() -> {
            given()
                    .get(HUMIDITY_URL_TPL.formatted("qwerty123456-foobar"))
                    .then()
                    .statusCode(404);
        });
    }

}
