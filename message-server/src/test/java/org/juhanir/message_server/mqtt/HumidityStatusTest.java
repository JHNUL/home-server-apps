package org.juhanir.message_server.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.mqtt.MqttClient;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.AwaitUtils;
import org.juhanir.message_server.utils.DatabaseUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;
import static org.juhanir.message_server.utils.TestConstants.HUMIDITY_URL_TPL;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityStatusTest extends DatabaseUtils {

    private static MqttClient client;
    private static final String SENDER = "shellyid-123123";
    private static final String TOPIC = "%s/status/humidity:0".formatted(SENDER);

    @BeforeEach
    void setUp() {
        client = MqttClient.create(Vertx.vertx());
        client.connectAndAwait(1883, "localhost");
        deleteAllHumidityMeasurements();
    }

    @AfterAll
    static void tearDown() {
        if (null != client && client.isConnected()) {
            client.disconnectAndAwait();
        }
    }

    @Test
    void sentHumidityStatusMessageCanBeFetchedViaRestApi() {
        String message = """
                {
                  "id": 1,
                  "rh": 99
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get(HUMIDITY_URL_TPL.formatted(SENDER, ""))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(SENDER))
                    .body("[0].value", equalTo(99.0f))
                    .body("[0].componentId", equalTo(1))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN));
        });
    }

    @Test
    void invalidHumidityMessageIsNotPersisted() {
        String invalidMessage = """
                {
                  "testing": true
                }""";
        String validMessage = """
                {
                  "id": 1,
                  "rh": 57.6
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(invalidMessage), MqttQoS.EXACTLY_ONCE, false, false);
        client.publishAndAwait(TOPIC, Buffer.buffer(validMessage), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get(HUMIDITY_URL_TPL.formatted(SENDER, ""))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(SENDER))
                    .body("[0].value", equalTo(57.6f))
                    .body("[0].componentId", equalTo(1))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN));
        });
    }

    @Test
    void humidityStatusMessagesForNonExistingDeviceReturnsNotFound() {
        AwaitUtils.awaitAssertionMaintained(() -> {
            given()
                    .get(HUMIDITY_URL_TPL.formatted("qwerty123456-foobar", ""))
                    .then()
                    .statusCode(404);
        });
    }

}
