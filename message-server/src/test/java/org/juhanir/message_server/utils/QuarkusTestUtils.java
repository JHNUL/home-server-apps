package org.juhanir.message_server.utils;

import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.reactive.mutiny.Mutiny;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.SignalData;
import org.juhanir.message_server.rest.api.Role;
import org.juhanir.message_server.rest.utils.AccessTokenResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public abstract class QuarkusTestUtils {

    public static final String EMPTY_MEASUREMENTS = "TRUNCATE TABLE sensor.signal_data;";
    /**
     * Test users are created by default by importing a dev/test realm via docker compose.
     */
    public static final Map<String, String> TEST_USERS = Map.of(
            Role.USER, "testuser",
            Role.ADMIN, "testadmin"
    );

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keycloakUrl;

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String oidcClientId;

    @Inject
    protected Mutiny.SessionFactory sessionFactory;

    /**
     * Authenticate as a user with the given role to test RBAC-protected
     * endpoints.
     *
     * @return {@link  io.restassured.specification.RequestSpecification} for chaining
     */
    public RequestSpecification authenticateUsingRole(String role) {
        return given()
                .auth()
                .oauth2(getToken(TEST_USERS.get(role)));
    }

    /**
     * Creates a device to database with type TEMPERATURE_HUMIDITY_SENSOR
     * and a UUID identifier.
     *
     * @return UUID identifier as string
     */
    public String createDeviceToDatabase() {
        String identifier = UUID.randomUUID().toString();
        String nativeQuery = """
                INSERT INTO sensor.device(identifier, device_type) VALUES('%s', 1);
                """.formatted(identifier);
        executeTransactionalNativeQuery(nativeQuery);
        return identifier;
    }

    /**
     * Get a device from database by identifier.
     */
    public Device getDevice(String identifier) {
        String nativeQuery = """
                SELECT * FROM sensor.device WHERE identifier = :identifier;
                """;
        return sessionFactory
                .withSession((session) -> session
                        .createNativeQuery(nativeQuery, Device.class)
                        .setParameter("identifier", identifier)
                        .getSingleResult())
                .await()
                .indefinitely();
    }

    /**
     * Deletes all devices from sensor.device table.
     */
    public void deleteAllDevices() {
        String nativeQuery = "DELETE FROM sensor.device;";
        executeTransactionalNativeQuery(nativeQuery);
    }

    /**
     * Delete all measurements from sensor.humidity table.
     */
    public void deleteAllHumidityMeasurements() {
        executeTransactionalNativeQuery(EMPTY_MEASUREMENTS);
    }

    /**
     * Delete all measurements from sensor.temperature table.
     */
    public void deleteAllTemperatureMeasurements() {
        executeTransactionalNativeQuery(EMPTY_MEASUREMENTS);
    }

    /**
     * Create temperature measurements to database.
     */
    public void createTemperatureMeasurements(Iterable<SignalData> temps) {
        if (!temps.iterator().hasNext()) return;

        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sb.append("""
                INSERT INTO sensor.signal_data (identifier, measurement_time, temperature_celsius, temperature_fahrenheit) VALUES
                """);

        int index = 0;
        for (SignalData temp : temps) {
            if (index > 0) sb.append(", ");
            sb.append("(?, ?, ?, ?)");

            params.add(temp.getDevice().getIdentifier());
            params.add(temp.getMeasurementTime());
            params.add(temp.getTemperatureCelsius());
            params.add(temp.getTemperatureFahrenheit());

            index++;
        }

        String sql = sb.toString();

        sessionFactory.withTransaction((session, tx) -> {
            var query = session.createNativeQuery(sql);
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }
            return query.executeUpdate();
        }).await().indefinitely();
    }

    /**
     * Create humidity measurements to database.
     */
    public void createHumidityMeasurements(Iterable<SignalData> humids) {
        if (!humids.iterator().hasNext()) return;

        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sb.append("""
                INSERT INTO sensor.signal_data (identifier, measurement_time, relative_humidity) VALUES
                """);

        int index = 0;
        for (SignalData hum : humids) {
            if (index > 0) sb.append(", ");
            sb.append("(?, ?, ?)");

            params.add(hum.getDevice().getIdentifier());
            params.add(hum.getMeasurementTime());
            params.add(hum.getRelativeHumidity());

            index++;
        }

        String sql = sb.toString();

        sessionFactory.withTransaction((session, tx) -> {
            var query = session.createNativeQuery(sql);
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i + 1, params.get(i));
            }
            return query.executeUpdate();
        }).await().indefinitely();
    }

    /**
     * Create rows of humidity data with random values
     *
     * @param identifier identifier for the measurements
     * @param startDate  timestamp for start data, e.g. '2025-01-01'
     * @param endDate    timestamp for start data, e.g. '2025-01-10'
     * @param interval   time interval, e.g. '1 hour'
     */
    public void seedRandomHumidityData(String identifier, String startDate, String endDate, String interval) {
        String nativeQuery = """
                SELECT '%s' as identifier,
                    measurement_time,
                    random()*50 as relative_humidity
                FROM generate_series('%s', '%s', interval '%s') as measurement_time;
                """.formatted(identifier, startDate, endDate, interval);
        executeTransactionalNativeQuery(nativeQuery);
    }

    private void executeTransactionalNativeQuery(String query) {
        sessionFactory
                .withTransaction((session, tx) -> session.createNativeQuery(query).executeUpdate())
                .await()
                .indefinitely();
    }

    private String getToken(String user) {
        return given()
                .relaxedHTTPSValidation()
                .param("grant_type", "password")
                .param("username", user)
                .param("password", "%s123".formatted(user))
                .param("client_id", oidcClientId)
                .when()
                .post("%s/protocol/openid-connect/token".formatted(keycloakUrl))
                .as(AccessTokenResponse.class)
                .accessToken();
    }

}
