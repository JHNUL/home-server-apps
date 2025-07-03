package org.juhanir.message_server.rest.resource;


import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.DatabaseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.juhanir.message_server.utils.TestConstants.TEMPERATURE_URL_TPL;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class TemperatureResourceTest extends DatabaseUtils {

    @BeforeEach
    void setUp() {
        deleteAllTemperatureMeasurements();
    }

    @Test
    void canFetchTemperatureMeasurementsFilteredByTime() {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                10
        );

        // All results = 10
        given()
                .get(TEMPERATURE_URL_TPL.formatted(deviceIdentifier, ""))
                .then()
                .statusCode(200)
                .and()
                .log().body()
                .body("size()", equalTo(10));

        // At minutes 15,16,17,18 (=4)
        given()
                .get(TEMPERATURE_URL_TPL.formatted(deviceIdentifier, "?from=2025-06-30T18:15:18Z&to2025-06-30T18:18:18Z"))
                .then()
                .statusCode(200)
                .and()
                .log().body()
                .body("size()", equalTo(4));
    }

    @Test
    void testInvalidDatetimeQueryParam() {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                10
        );

        given()
                .get(TEMPERATURE_URL_TPL.formatted(deviceIdentifier, "?from=thebeginningoftime"))
                .then()
                .statusCode(400);
    }

    private void createMeasurementsCountingDownFromBaseTime(String deviceIdentifier, Instant baseTime, int numberOfMinutes) {
        Device device = getDevice(deviceIdentifier);
        List<TemperatureStatus> temps = IntStream
                .range(0, numberOfMinutes)
                .mapToObj(i -> new TemperatureStatus()
                        .setDevice(device)
                        .setComponentId(0)
                        .setMeasurementTime(baseTime.minusSeconds(i * 60L))
                        .setValueCelsius(new BigDecimal(10))
                        .setValueFahrenheit(new BigDecimal(50)))
                .toList();
        createTemperatureMeasurements(temps);
    }

}
