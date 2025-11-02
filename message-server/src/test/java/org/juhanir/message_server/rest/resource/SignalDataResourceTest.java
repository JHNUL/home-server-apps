package org.juhanir.message_server.rest.resource;


import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.dto.outgoing.SignalDataResponse;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.rest.api.Role;
import org.juhanir.message_server.utils.QuarkusTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class SignalDataResourceTest extends QuarkusTestUtils {

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void canFetchMeasurementsFilteredByTime(String role) {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                10
        );

        // All results = 10
        authenticateUsingRole(role)
                .get("/signaldata?device=%s".formatted(deviceIdentifier))
                .then()
                .statusCode(200)
                .body("size()", equalTo(10));

        // At minutes 15,16,17,18
        authenticateUsingRole(role)
                .get("/signaldata?device=%s&from=%s&to=%s".formatted(deviceIdentifier, "2025-06-30T18:15:18Z", "2025-06-30T18:18:18Z"))
                .then()
                .statusCode(200)
                .body("size()", equalTo(4));

        // At minutes 15,16
        authenticateUsingRole(role)
                .get("/signaldata?device=%s&from=%s&to=%s".formatted(deviceIdentifier, "2025-06-30T18:15:18Z", "2025-06-30T18:16:18Z"))
                .then()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void testInvalidDatetimeQueryParam(String role) {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                10
        );

        authenticateUsingRole(role)
                .get("/signaldata?device=%s&from=%s".formatted(deviceIdentifier, "thebeginningoftime"))
                .then()
                .statusCode(400);
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void canLimitMaximumNumberOfResults(String role) {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                160
        );

        // Default max at 100
        authenticateUsingRole(role)
                .get("/signaldata?device=%s".formatted(deviceIdentifier))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(100));

        // Without device identifier, also max 100
        authenticateUsingRole(role)
                .get("/signaldata")
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(100));

        // Limit to 10
        authenticateUsingRole(role)
                .get("/signaldata?device=%s&limit=%s".formatted(deviceIdentifier, "10"))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(10));

        // Fetch all
        authenticateUsingRole(role)
                .get("/signaldata?device=%s&limit=%s".formatted(deviceIdentifier, "1000"))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(160));
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void canFetchDataFromMultipleDevices(String role) {
        var startingTime = Instant.parse("2025-06-30T18:18:18Z");
        String device1 = createDeviceToDatabase();
        String device2 = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(device1, startingTime, 20);
        createMeasurementsCountingDownFromBaseTime(device2, startingTime, 30);

        // All from device1 = 20
        authenticateUsingRole(role)
                .get("/signaldata?device=%s".formatted(device1))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(20));

        // All from device2 = 30
        authenticateUsingRole(role)
                .get("/signaldata?device=%s".formatted(device2))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(30));

        // device1 and 2 = 50
        authenticateUsingRole(role)
                .get("/signaldata?device=%s&device=%s".formatted(device1, device2))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(50));
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void canSortResultsDescendingByMeasurementTime(String role) {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                5
        );

        // Default descending
        var resultsDesc = authenticateUsingRole(role)
                .get("/signaldata?device=%s".formatted(deviceIdentifier))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(5))
                .extract()
                .body()
                .jsonPath()
                .getList(".", SignalDataResponse.class);
        for (int i = 1; i < resultsDesc.size(); i++) {
            var prev = resultsDesc.get(i - 1);
            var curr = resultsDesc.get(i);
            assertTrue(curr.measurementTime().isBefore(prev.measurementTime()));
        }

        // Query ascending
        var resultsAsc = authenticateUsingRole(role)
                .get("/signaldata?device=%s&sort=%s".formatted(deviceIdentifier, "asc"))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(5))
                .extract()
                .body()
                .jsonPath()
                .getList(".", SignalDataResponse.class);
        for (int i = 1; i < resultsAsc.size(); i++) {
            var prev = resultsAsc.get(i - 1);
            var curr = resultsAsc.get(i);
            assertTrue(curr.measurementTime().isAfter(prev.measurementTime()));
        }
    }

    private void createMeasurementsCountingDownFromBaseTime(String deviceIdentifier, Instant baseTime, int numberOfMinutes) {
        Device device = getDevice(deviceIdentifier);
        List<SignalData> data = IntStream
                .range(0, numberOfMinutes)
                .mapToObj(i -> new SignalData()
                        .setDevice(device)
                        .setMeasurementTime(baseTime.minusSeconds(i * 60L))
                        .setRelativeHumidity(24.0)
                        .setTemperatureFahrenheit(24.0)
                        .setTemperatureFahrenheit(56.0)
                )
                .toList();
        createMeasurements(data);
    }

}
