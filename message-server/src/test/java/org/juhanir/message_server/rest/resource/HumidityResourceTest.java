package org.juhanir.message_server.rest.resource;


import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.dto.outgoing.HumidityStatusResponse;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.rest.api.Role;
import org.juhanir.message_server.utils.QuarkusTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.equalTo;
import static org.juhanir.message_server.utils.TestConstants.HUMIDITY_URL_TPL;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityResourceTest extends QuarkusTestUtils {

    @BeforeEach
    void setUp() {
        deleteAllHumidityMeasurements();
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void canFetchHumidityMeasurementsFilteredByTime(String role) {
        String deviceIdentifier = createDeviceToDatabase();
        createMeasurementsCountingDownFromBaseTime(
                deviceIdentifier,
                Instant.parse("2025-06-30T18:18:18Z"),
                10
        );

        // All results = 10
        authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, ""))
                .then()
                .statusCode(200)
                .body("size()", equalTo(10));

        // At minutes 15,16,17,18
        authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, "?from=2025-06-30T18:15:18Z&to=2025-06-30T18:18:18Z"))
                .then()
                .statusCode(200)
                .body("size()", equalTo(4));

        // At minutes 15,16
        authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, "?from=2025-06-30T18:15:18Z&to=2025-06-30T18:16:18Z"))
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
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, "?from=thebeginningoftime"))
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
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, ""))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(100));

        // Limit to 10
        authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, "?limit=10"))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(10));

        // Fetch all
        authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, "?limit=1000"))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(160));
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

        // Default ascending
        var resultsAsc = authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, ""))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(5))
                .extract()
                .body()
                .jsonPath()
                .getList(".", HumidityStatusResponse.class);
        for (int i = 1; i < resultsAsc.size(); i++) {
            var prev = resultsAsc.get(i - 1);
            var curr = resultsAsc.get(i);
            assertTrue(prev.measurementTime().isBefore(curr.measurementTime()));
        }

        // Query descending
        var resultsDesc = authenticateUsingRole(role)
                .get(HUMIDITY_URL_TPL.formatted(deviceIdentifier, "?sort=desc"))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(5))
                .extract()
                .body()
                .jsonPath()
                .getList(".", HumidityStatusResponse.class);
        for (int i = 1; i < resultsDesc.size(); i++) {
            var prev = resultsDesc.get(i - 1);
            var curr = resultsDesc.get(i);
            assertTrue(prev.measurementTime().isAfter(curr.measurementTime()));
        }
    }

    private void createMeasurementsCountingDownFromBaseTime(String deviceIdentifier, Instant baseTime, int numberOfMinutes) {
        Device device = getDevice(deviceIdentifier);
        List<SignalData> hums = IntStream
                .range(0, numberOfMinutes)
                .mapToObj(i -> new SignalData()
                        .setDevice(device)
                        .setMeasurementTime(baseTime.minusSeconds(i * 60L))
                        .setRelativeHumidity(24.0))
                .toList();
        createHumidityMeasurements(hums);
    }

}
