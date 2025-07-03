package org.juhanir.message_server.rest.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.message_server.MessageServerTestResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class DeviceResourceTest {

    @Inject
    private Mutiny.SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        String nativeQuery = "DELETE FROM sensor.device;";
        sessionFactory
                .withTransaction((session, tx) -> session.createNativeQuery(nativeQuery).executeUpdate())
                .await()
                .indefinitely();
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
        String identifier = createDeviceToDatabase();
        given()
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(200)
                .and()
                .log().body()
                .body("id", instanceOf(Number.class))
                .body("identifier", equalTo(identifier))
                .body("deviceType", equalTo(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR.toString()))
                .body("createdAt", matchesPattern(DATETIME_PATTERN))
                .body("latestCommunication", matchesPattern(DATETIME_PATTERN));
    }

    @Test
    void canFetchAllDevices() {
        for (int i = 0; i < 10; i++) {
            createDeviceToDatabase();
        }
        given()
                .get("devices")
                .then()
                .statusCode(200)
                .and()
                .log().body()
                .body("size()", equalTo(10));
    }

    private String createDeviceToDatabase() {
        String identifier = UUID.randomUUID().toString();
        String nativeQuery = """
                INSERT INTO sensor.device(identifier, device_type) VALUES('%s', 1);
                """.formatted(identifier);
        sessionFactory.withTransaction((session, tx) -> session.createNativeQuery(nativeQuery).executeUpdate()).await().indefinitely();
        return identifier;
    }
}
