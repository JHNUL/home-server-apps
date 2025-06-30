package org.juhanir.message_server.utils;

public final class TestConstants {

    public static final String DT_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3,6}Z$";
    public static final String EMPTY_HUMIDITY_MEASUREMENTS = "TRUNCATE TABLE sensor.humidity_status;";
    public static final String EMPTY_TEMPERATURE_MEASUREMENTS = "TRUNCATE TABLE sensor.temperature_status;";
    public static final String TEMPERATURE_URL_TPL = "/devices/%s/temperature";
    public static final String HUMIDITY_URL_TPL = "/devices/%s/humidity";

    private TestConstants() {
    }

}
