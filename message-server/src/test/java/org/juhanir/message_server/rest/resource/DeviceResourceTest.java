package org.juhanir.message_server.rest.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.DatabaseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class DeviceResourceTest extends DatabaseUtils {

    @BeforeEach
    void setUp() {
        deleteAllDevices();
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

}
