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

import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;
import static org.juhanir.message_server.utils.TestConstants.TEMPERATURE_URL_TPL;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class TemperatureStatusTest extends QuarkusTestUtils {

    private static MqttClient client;
    private static final String SENDER = "shellyid-123123";
    private static final String TOPIC = "%s/status/temperature:0".formatted(SENDER);

    @BeforeEach
    void setUp() {
        client = MqttClient.create(Vertx.vertx());
        client.connectAndAwait(1883, "localhost");
        deleteAllTemperatureMeasurements();
    }

    @AfterAll
    static void tearDown() {
        if (null != client && client.isConnected()) {
            client.disconnectAndAwait();
        }
    }

    @Test
    void invalidTemperatureMessageIsNotPersisted() {
        String invalidMessage = """
                {
                  "testing": true
                }""";
        String validMessage = """
                {
                  "id": 2,
                  "tC": 19.6,
                  "tF": 67.3
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(invalidMessage), MqttQoS.EXACTLY_ONCE, false, false);
        client.publishAndAwait(TOPIC, Buffer.buffer(validMessage), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            authenticateUsingRole("user")
                    .get(TEMPERATURE_URL_TPL.formatted(SENDER, ""))
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(SENDER))
                    .body("[0].valueCelsius", equalTo(19.6f))
                    .body("[0].valueFahrenheit", equalTo(67.3f))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN));
        });
    }

    @Test
    void sentTemperatureStatusMessageCanBeFetchedViaRestApi() {
        String message = """
                {
                  "id": 1,
                  "tC": 29.6,
                  "tF": 71.3
                }""";
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            authenticateUsingRole("user")
                    .get(TEMPERATURE_URL_TPL.formatted(SENDER, ""))
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(SENDER))
                    .body("[0].valueCelsius", equalTo(29.6f))
                    .body("[0].valueFahrenheit", equalTo(71.3f))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN));
        });
    }

    @Test
    void temperatureStatusMessagesForNonExistingDeviceReturnsNotFound() {
        AwaitUtils.awaitAssertionMaintained(() -> {
            authenticateUsingRole("user")
                    .get(TEMPERATURE_URL_TPL.formatted("qwerty123456-foobar", ""))
                    .then()
                    .statusCode(404);
        });
    }

    @Test
    void deviceLatestCommunicationTimeIsUpdated() {
        String identifier = createDeviceToDatabase();
        String message = """
                {
                  "id": 2,
                  "tC": 19.6,
                  "tF": 67.3
                }""";
        Instant latestCommTime = authenticateUsingRole("user")
                .get("/devices/%s".formatted(identifier))
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(DeviceResponse.class)
                .latestCommunication();
        client.publishAndAwait(identifier + "/status/temperature:0", Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
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
