package org.juhanir.message_server.rest.resource;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.rest.api.Role;
import org.juhanir.message_server.utils.QuarkusTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.juhanir.message_server.utils.TestConstants.*;

@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class DeviceResourceTest extends QuarkusTestUtils {

    @BeforeEach
    void setUp() {
        deleteAllDevices();
    }

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
                .body("size()", equalTo(10));
    }

    @Test
    void deletingDeviceRemovesDeviceAndMeasurements() {
        String identifier = createDeviceToDatabase();
        authenticateUsingRole(Role.ADMIN)
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(200);

        createMeasurementsForDevice(identifier);

        authenticateUsingRole(Role.ADMIN)
                .get(HUMIDITY_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(1));

        authenticateUsingRole(Role.ADMIN)
                .get(TEMPERATURE_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(200)
                .and()
                .body("size()", equalTo(1));

        authenticateUsingRole(Role.ADMIN)
                .delete("devices/%s".formatted(identifier))
                .then()
                .statusCode(204);

        // Gone is the device...
        authenticateUsingRole(Role.ADMIN)
                .get("devices/%s".formatted(identifier))
                .then()
                .statusCode(404);

        // ...and its humidity measurement...
        authenticateUsingRole(Role.ADMIN)
                .get(HUMIDITY_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(404);

        // ...as well as temperature measurement.
        authenticateUsingRole(Role.ADMIN)
                .get(TEMPERATURE_URL_TPL.formatted(identifier, ""))
                .then()
                .statusCode(404);

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
        var hm = new HumidityStatus()
                .setDevice(device)
                .setComponentId(0)
                .setMeasurementTime(Instant.now())
                .setValue(24.0);
        var tm = new TemperatureStatus()
                .setDevice(device)
                .setComponentId(0)
                .setMeasurementTime(Instant.now())
                .setValueCelsius(10.0)
                .setValueFahrenheit(50.0);
        createHumidityMeasurements(List.of(hm));
        createTemperatureMeasurements(List.of(tm));
    }

}
