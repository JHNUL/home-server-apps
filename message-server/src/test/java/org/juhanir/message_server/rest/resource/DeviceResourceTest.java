package org.juhanir.message_server.rest.resource;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.mqtt.MqttClient;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.AwaitUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class DeviceResourceTest {

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
    void fetchingNonExistingDeviceResultIsNotFound() {
        given()
                .get("devices/fooo-baar-bazz-123456")
                .then()
                .statusCode(404);
    }

    @Test
    void createdDeviceCanBeFetched() {
        String identifier = "sally-123";
        String message = """
                {
                  "id": 11,
                  "rh": 99
                }""";
        client.publishAndAwait("%s/status/humidity:0".formatted(identifier), Buffer.buffer(message), MqttQoS.EXACTLY_ONCE, false, false);
        AwaitUtils.awaitAssertion(() -> {
            given()
                    .get("devices/%s".formatted(identifier))
                    .then()
                    .statusCode(200)
                    .and()
                    .log().body()
                    .body("id", instanceOf(Number.class))
                    .body("identifier", equalTo(identifier))
                    .body("deviceType", equalTo(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR.toString()));
        });
    }
}
