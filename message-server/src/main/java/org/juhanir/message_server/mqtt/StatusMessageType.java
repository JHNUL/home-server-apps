package org.juhanir.message_server.mqtt;

import java.util.Optional;

public enum StatusMessageType {
    HUMIDITY,
    TEMPERATURE;


    public static Optional<StatusMessageType> fromString(String input) {
        try {
            return Optional.of(StatusMessageType.valueOf(input.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
