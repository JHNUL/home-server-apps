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
public class HumidityStatusTest {

    private static MqttClient client;
    private static final String TOPIC = "shellyid-123123/status/humidity:0";
    private static final String HUMIDITY_URL_TPL = "/devices/%s/humidity";
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
    void canSendHumidityStatusMessageToTopic() {
        String message = """
                {
                  "id": %d,
                  "rh": 57
                }""".formatted(idProducer.incrementAndGet());
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
    }

    @Test
    void sentHumidityStatusMessageCanBeFetchedViaRestApi() {
        int testId = idProducer.incrementAndGet();
        String message = """
                {
                  "id": %d,
                  "rh": 99
                }""".formatted(testId);
        client.publishAndAwait(TOPIC, Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);

        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get(HUMIDITY_URL_TPL.formatted("shellyid-123123"))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", greaterThanOrEqualTo(1))
                    .body("", hasItem(
                            Matchers.<Map<String, Object>>allOf(
                                    hasEntry("value", 99.0f),
                                    hasEntry("componentId", testId))
                    ));
        });
    }

    @Test
    void invalidHumidityMessageIsNotPersisted() {
        int testId = idProducer.incrementAndGet();
        String invalidMessage = """
                {
                  "testing": true
                }""";
        String validMessage = """
                {
                  "id": %d,
                  "rh": 57.6
                }""".formatted(testId);
        client.publishAndAwait(TOPIC, Buffer.buffer(invalidMessage), MqttQoS.EXACTLY_ONCE, false, false);
        client.publishAndAwait(TOPIC, Buffer.buffer(validMessage), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get(HUMIDITY_URL_TPL.formatted("shellyid-123123"))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("size()", greaterThanOrEqualTo(1))
                    .body("", hasItem(
                            Matchers.<Map<String, Object>>allOf(
                                    hasEntry("value", 57.6f),
                                    hasEntry("componentId", testId))
                    ));
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
