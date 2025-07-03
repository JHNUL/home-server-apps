package org.juhanir.message_server.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.mqtt.MqttClient;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.AwaitUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.*;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class TemperatureStatusTest {

    private static MqttClient client;
    private static final String SENDER = "shellyid-123123";
    private static final String TOPIC = "%s/status/temperature:0".formatted(SENDER);

    @Inject
    private Mutiny.SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        client = MqttClient.create(Vertx.vertx());
        client.connectAndAwait(1883, "localhost");
        sessionFactory
                .withTransaction((session, tx) ->
                        session.createNativeQuery(EMPTY_TEMPERATURE_MEASUREMENTS).executeUpdate()
                )
                .await()
                .indefinitely();
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
            given()
                    .get(TEMPERATURE_URL_TPL.formatted(SENDER))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", equalTo(1))
                    .body("[0].deviceId", instanceOf(Number.class))
                    .body("[0].componentId", equalTo(2))
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
            given()
                    .get(TEMPERATURE_URL_TPL.formatted(SENDER))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", equalTo(1))
                    .body("[0].deviceId", instanceOf(Number.class))
                    .body("[0].componentId", equalTo(1))
                    .body("[0].valueCelsius", equalTo(29.6f))
                    .body("[0].valueFahrenheit", equalTo(71.3f))
                    .body("[0].measurementTime", matchesPattern(DATETIME_PATTERN));
        });
    }

    @Test
    void temperatureStatusMessagesForNonExistingDeviceReturnsNotFound() {
        AwaitUtils.awaitAssertionMaintained(() -> {
            given()
                    .get(TEMPERATURE_URL_TPL.formatted("qwerty123456-foobar"))
                    .then()
                    .statusCode(404);
        });
    }
}
