package org.juhanir.message_server.mqtt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotSupportedException;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class StatusMessageMapperFactory {

    private final Map<StatusMessageType, StatusMessageMapper> mappers;

    @Inject
    public StatusMessageMapperFactory(Instance<StatusMessageMapper> allMappers) {
        this.mappers = allMappers
                .stream()
                .collect(Collectors.toMap(StatusMessageMapper::getType, Function.identity()));
    }

    public StatusMessageMapper get(String type) {
        StatusMessageType statusType = StatusMessageType.fromString(type)
                .orElseThrow(() -> new NotSupportedException("Invalid status type: %s".formatted(type)));

        return Optional
                .ofNullable(mappers.get(statusType))
                .orElseThrow(() -> new NotSupportedException("No mapper for status type: %s".formatted(statusType)));
    }

}
