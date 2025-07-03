package org.juhanir.message_server.utils;

public final class TestConstants {

    public static final String DATETIME_PATTERN = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3,6}Z$";
    public static final String TEMPERATURE_URL_TPL = "/devices/%s/temperature%s";
    public static final String HUMIDITY_URL_TPL = "/devices/%s/humidity%s";

    private TestConstants() {
    }

}
