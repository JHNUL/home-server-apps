package org.juhanir.message_server.rest.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.DatabaseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.*;

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

    @Test
    void deletingDeviceRemovesDeviceAndMeasurements() {
        String identifier = createDeviceToDatabase();
        given()
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(200);

        createMeasurementsForDevice(identifier);

        given()
                .get(HUMIDITY_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(1));

        given()
                .get(TEMPERATURE_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(1));

        given()
                .delete("devices/%s".formatted(identifier))
                .then()
                .statusCode(204);

        // Gone is the device...
        given()
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(404);

        // ...and its humidity measurement...
        given()
                .get(HUMIDITY_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(404);

        // ...as well as temperature measurement.
        given()
                .get(TEMPERATURE_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(404);

    }

    private void createMeasurementsForDevice(String identifier) {
        Device device = getDevice(identifier);
        var hm = new HumidityStatus()
                .setDevice(device)
                .setComponentId(0)
                .setMeasurementTime(Instant.now())
                .setValue(new BigDecimal(24));
        var tm = new TemperatureStatus()
                .setDevice(device)
                .setComponentId(0)
                .setMeasurementTime(Instant.now())
                .setValueCelsius(new BigDecimal(10))
                .setValueFahrenheit(new BigDecimal(50));
        createHumidityMeasurements(List.of(hm));
        createTemperatureMeasurements(List.of(tm));
    }

}
