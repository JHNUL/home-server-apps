package org.juhanir.message_server.rest.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.rest.api.Role;
import org.juhanir.message_server.utils.QuarkusTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.DATETIME_PATTERN;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class DeviceResourceTest extends QuarkusTestUtils {

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void fetchingNonExistingDeviceResultIsNotFound(String role) {
        authenticateUsingRole(role)
                .get("devices/fooo-baar-bazz-123456")
                .then()
                .statusCode(404);
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void createdDeviceCanBeFetched(String role) {
        String identifier = createDeviceToDatabase();
        authenticateUsingRole(role)
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(200)
                .body("id", instanceOf(Number.class))
                .body("identifier", equalTo(identifier))
                .body("deviceType", equalTo(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR.toString()))
                .body("createdAt", matchesPattern(DATETIME_PATTERN))
                .body("latestCommunication", matchesPattern(DATETIME_PATTERN));
    }

    @ParameterizedTest()
    @ValueSource(strings = {Role.USER, Role.ADMIN})
    void canFetchAllDevices(String role) {
        for (int i = 0; i < 10; i++) {
            createDeviceToDatabase();
        }
        authenticateUsingRole(role)
                .get("devices")
                .then()
                .statusCode(200)
                .and()
                .body("size()", greaterThanOrEqualTo(10));
    }

    @Test
    void deletingDevicePossibleOnlyIfNoData() {
        String identifier = createDeviceToDatabase();
        authenticateUsingRole(Role.ADMIN)
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(200);

        // OK to delete since no data has been saved for device
        authenticateUsingRole(Role.ADMIN)
                .delete("devices/%s".formatted(identifier))
                .then()
                .statusCode(204);

        // Gone is the device
        authenticateUsingRole(Role.ADMIN)
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(404);

        String identifier2 = createDeviceToDatabase();
        authenticateUsingRole(Role.ADMIN)
                .get("devices/%s".formatted(identifier2))
                .then()
                .statusCode(200);

        // Adding data
        createMeasurementsForDevice(identifier2);

        // Try to delete
        authenticateUsingRole(Role.ADMIN)
                .delete("devices/%s".formatted(identifier))
                .then()
                .statusCode(204);

        // Still there
        authenticateUsingRole(Role.ADMIN)
                .get("devices/%s".formatted(identifier2))
                .then()
                .statusCode(200);
    }

    @Test
    void deletingNonExistingDevice() {
        authenticateUsingRole(Role.ADMIN)
                .delete("devices/___notadevice!!!!")
                .then()
                .statusCode(204);
    }

    @ParameterizedTest
    @MethodSource("allRoles")
    void onlyAdminCanDeleteDevice(String role) {
        String identifier = createDeviceToDatabase();
        authenticateUsingRole(role)
                .delete("devices/%s".formatted(identifier))
                .then()
                .statusCode(role.equals(Role.ADMIN) ? 204 : 403);
    }

    private static Stream<Arguments> allRoles() {
        return Role.getAllRoles().stream().map(Arguments::of);
    }

    private void createMeasurementsForDevice(String identifier) {
        Device device = getDevice(identifier);
        var hm = new SignalData()
                .setDevice(device)
                .setMeasurementTime(Instant.now())
                .setRelativeHumidity(24.0);
        var tm = new SignalData()
                .setDevice(device)
                .setMeasurementTime(Instant.now())
                .setTemperatureCelsius(10.0)
                .setTemperatureFahrenheit(50.0);
        createHumidityMeasurements(List.of(hm));
        createTemperatureMeasurements(List.of(tm));
    }

}
