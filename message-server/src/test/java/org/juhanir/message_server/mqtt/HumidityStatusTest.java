package org.juhanir.message_server.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.mqtt.MqttClient;
import org.juhanir.domain.sensordata.dto.outgoing.DeviceResponse;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.AwaitUtils;
import org.juhanir.message_server.utils.QuarkusTestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;
import static org.juhanir.message_server.utils.TestConstants.HUMIDITY_URL_TPL;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityStatusTest extends QuarkusTestUtils {

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
            authenticateUsingRole("user")
                    .get(HUMIDITY_URL_TPL.formatted(SENDER, ""))
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(SENDER))
                    .body("[0].value", equalTo(99.0f))
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
            authenticateUsingRole("user")
                    .get(HUMIDITY_URL_TPL.formatted(SENDER, ""))
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(SENDER))
                    .body("[0].value", equalTo(57.6f))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN));
        });
    }

    @Test
    void humidityStatusMessagesForNonExistingDeviceReturnsNotFound() {
        AwaitUtils.awaitAssertionMaintained(() -> {
            authenticateUsingRole("user")
                    .get(HUMIDITY_URL_TPL.formatted("qwerty123456-foobar", ""))
                    .then()
                    .statusCode(404);
        });
    }

    @Test
    void deviceLatestCommunicationTimeIsUpdated() {
        String identifier = createDeviceToDatabase();
        String message = """
                {
                  "id": 1,
                  "rh": 99
                }""";
        Instant latestCommTime = authenticateUsingRole("user")
                .get("/devices/%s".formatted(identifier))
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(DeviceResponse.class)
                .latestCommunication();
        client.publishAndAwait(identifier + "/status/humidity:0", Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            Instant newTime = authenticateUsingRole("user")
                    .get("/devices/%s".formatted(identifier))
                    .then()
                    .statusCode(200)
                    .extract()
                    .body()
                    .as(DeviceResponse.class)
                    .latestCommunication();
            Assertions.assertTrue(newTime.isAfter(latestCommTime));
        });
    }

}
