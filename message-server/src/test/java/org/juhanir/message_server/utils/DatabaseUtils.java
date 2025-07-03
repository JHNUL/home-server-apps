package org.juhanir.message_server.utils;

import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DatabaseUtils {

    public static final String EMPTY_HUMIDITY_MEASUREMENTS = "TRUNCATE TABLE sensor.humidity_status;";
    public static final String EMPTY_TEMPERATURE_MEASUREMENTS = "TRUNCATE TABLE sensor.temperature_status;";

    @Inject
    private Mutiny.SessionFactory sessionFactory;

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
        sessionFactory
                .withTransaction((session, tx) -> session.createNativeQuery(nativeQuery).executeUpdate())
                .await()
                .indefinitely();
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
        sessionFactory
                .withTransaction((session, tx) -> session.createNativeQuery(nativeQuery).executeUpdate())
                .await()
                .indefinitely();
    }

    /**
     * Delete all measurements from sensor.humidity table.
     */
    public void deleteAllHumidityMeasurements() {
        sessionFactory
                .withTransaction((session, tx) ->
                        session.createNativeQuery(EMPTY_HUMIDITY_MEASUREMENTS).executeUpdate()
                )
                .await()
                .indefinitely();
    }

    /**
     * Delete all measurements from sensor.temperature table.
     */
    public void deleteAllTemperatureMeasurements() {
        sessionFactory
                .withTransaction((session, tx) ->
                        session.createNativeQuery(EMPTY_TEMPERATURE_MEASUREMENTS).executeUpdate()
                )
                .await()
                .indefinitely();
    }

    /**
     * Create temperature measurements to database.
     */
    public void createTemperatureMeasurements(Iterable<TemperatureStatus> temps) {
        if (!temps.iterator().hasNext()) return;

        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sb.append("""
                INSERT INTO sensor.temperature_status (device_id, measurement_time, component_id, value_celsius, value_fahrenheit) VALUES
                """);

        int index = 0;
        for (TemperatureStatus temp : temps) {
            if (index > 0) sb.append(", ");
            sb.append("(?, ?, ?, ?, ?)");

            params.add(temp.getDevice().getId());
            params.add(temp.getMeasurementTime());
            params.add(temp.getComponentId());
            params.add(temp.getValueCelsius());
            params.add(temp.getValueFahrenheit());

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

}
