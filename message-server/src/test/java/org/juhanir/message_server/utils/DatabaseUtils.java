package org.juhanir.message_server.utils;

import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class DatabaseUtils {

    public static final String EMPTY_HUMIDITY_MEASUREMENTS = "TRUNCATE TABLE sensor.humidity_status;";
    public static final String EMPTY_TEMPERATURE_MEASUREMENTS = "TRUNCATE TABLE sensor.temperature_status;";

    @Inject
    protected Mutiny.SessionFactory sessionFactory;

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
        executeTransactionalNativeQuery(EMPTY_HUMIDITY_MEASUREMENTS);
    }

    /**
     * Delete all measurements from sensor.temperature table.
     */
    public void deleteAllTemperatureMeasurements() {
        executeTransactionalNativeQuery(EMPTY_TEMPERATURE_MEASUREMENTS);
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

    /**
     * Create humidity measurements to database.
     */
    public void createHumidityMeasurements(Iterable<HumidityStatus> humids) {
        if (!humids.iterator().hasNext()) return;

        StringBuilder sb = new StringBuilder();
        List<Object> params = new ArrayList<>();

        sb.append("""
                INSERT INTO sensor.humidity_status (device_id, measurement_time, component_id, value) VALUES
                """);

        int index = 0;
        for (HumidityStatus hum : humids) {
            if (index > 0) sb.append(", ");
            sb.append("(?, ?, ?, ?)");

            params.add(hum.getDevice().getId());
            params.add(hum.getMeasurementTime());
            params.add(hum.getComponentId());
            params.add(hum.getValue());

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
     * @param deviceId device id for the measurements
     * @param startDate timestamp for start data, e.g. '2025-01-01'
     * @param endDate timestamp for start data, e.g. '2025-01-10'
     * @param interval time interval, e.g. '1 hour'
     */
    public void seedRandomHumidityData(long deviceId, String startDate, String endDate, String interval) {
        String nativeQuery = """
                SELECT %s as device_id,
                    measurement_time,
                    0 as component_id,
                    random()*50 as value
                FROM generate_series('%s', '%s', interval '%s') as measurement_time;
                """.formatted(deviceId, startDate, endDate, interval);
        executeTransactionalNativeQuery(nativeQuery);
    }

    private void executeTransactionalNativeQuery(String query) {
        sessionFactory
                .withTransaction((session, tx) -> session.createNativeQuery(query).executeUpdate())
                .await()
                .indefinitely();
    }

}
