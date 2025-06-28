package org.juhanir.message_server.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.mqtt.MqttClient;
import jakarta.inject.Inject;
import org.hamcrest.Matchers;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.repository.HumidityRepository;
import org.juhanir.message_server.utils.AwaitUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class TemperatureStatusTest {

    private static MqttClient client;
    private static final String TOPIC = "shellyid-123123/status/temperature:0";
    private static final String TEMPERATURE_URL_TPL = "/devices/%s/temperature";
    private static final AtomicInteger idProducer = new AtomicInteger();

    @Inject
    private HumidityRepository humidityRepository;

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
                  "id": %d,
                  "tC": 29.6,
                  "tF": 63.3
                }""".formatted(idProducer.incrementAndGet());
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
    }

    @Test
    void invalidTemperatureMessageIsNotPersisted() {
        int testId = idProducer.incrementAndGet();
        String invalidMessage = """
                {
                  "testing": true
                }""";
        String validMessage = """
                {
                  "id": %d,
                  "tC": 19.6,
                  "tF": 67.3
                }""".formatted(testId);
        client.publishAndAwait(TOPIC, Buffer.buffer(invalidMessage), MqttQoS.EXACTLY_ONCE, false, false);
        client.publishAndAwait(TOPIC, Buffer.buffer(validMessage), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get(TEMPERATURE_URL_TPL.formatted("shellyid-123123"))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", greaterThanOrEqualTo(1))
                    .body("", hasItem(
                            Matchers.<Map<String, Object>>allOf(
                                    hasEntry("valueCelsius", 19.6f),
                                    hasEntry("valueFahrenheit", 67.3f),
                                    hasEntry("componentId", testId)
                            )
                    ));
        });
    }

    @Test
    void sentTemperatureStatusMessageCanBeFetchedViaRestApi() {
        int testId = idProducer.incrementAndGet();
        String message = """
                {
                  "id": %d,
                  "tC": 29.6,
                  "tF": 71.3
                }""".formatted(testId);
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);

        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get(TEMPERATURE_URL_TPL.formatted("shellyid-123123"))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", greaterThanOrEqualTo(1))
                    .body("", hasItem(
                            Matchers.<Map<String, Object>>allOf(
                                    hasEntry("valueCelsius", 29.6f),
                                    hasEntry("valueFahrenheit", 71.3f),
                                    hasEntry("componentId", testId)
                            )
                    ));
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
