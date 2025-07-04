package org.juhanir.message_server.rest.utils;

public class ParseUtil {

    private ParseUtil() {
    }

    public static int tryParseInt(String value, int defaultValue) {
        try {
            // Avoid unnecessary throwing with null inputs as exception
            // handling is more expensive than regular control flow.
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
