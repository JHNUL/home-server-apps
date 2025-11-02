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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityStatusTest extends QuarkusTestUtils {

    private static MqttClient client;

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
    void sentHumidityStatusMessageCanBeFetchedViaRestApi() {
        String device = UUID.randomUUID().toString();
        String message = """
                {
                  "id": 1,
                  "rh": 99
                }""";
        client.publishAndAwait("%s/status/humidity:0".formatted(device), Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            authenticateUsingRole("user")
                    .get("/signaldata?device=%s".formatted(device))
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(device))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN))
                    .body("[0].temperatureCelsius", nullValue())
                    .body("[0].temperatureFahrenheit", nullValue())
                    .body("[0].relativeHumidity", equalTo(99.0f));
        });
    }

    @Test
    void invalidHumidityMessageIsNotPersisted() {
        String device = UUID.randomUUID().toString();
        String invalidMessage = """
                {
                  "testing": true
                }""";
        String validMessage = """
                {
                  "id": 1,
                  "rh": 57.6
                }""";
        client.publishAndAwait("%s/status/humidity:0".formatted(device), Buffer.buffer(invalidMessage), MqttQoS.EXACTLY_ONCE, false, false);
        client.publishAndAwait("%s/status/humidity:0".formatted(device), Buffer.buffer(validMessage), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            authenticateUsingRole("user")
                    .get("/signaldata?device=%s".formatted(device))
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(1))
                    .body("[0].deviceIdentifier", equalTo(device))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN))
                    .body("[0].temperatureCelsius", nullValue())
                    .body("[0].temperatureFahrenheit", nullValue())
                    .body("[0].relativeHumidity", equalTo(57.6f));
        });
    }

    @Test
    void humidityStatusMessagesForNonExistingDeviceReturnsEmptyList() {
        AwaitUtils.awaitAssertion(() -> {
            authenticateUsingRole("user")
                    .get("/signaldata?device=0xcafebabe")
                    .then()
                    .statusCode(200)
                    .body("size()", equalTo(0));
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
