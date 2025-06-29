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
public class StatusMessageProcessorFactory {

    private final Map<StatusMessageType, StatusMessageProcessor> processorMap;

    @Inject
    public StatusMessageProcessorFactory(Instance<StatusMessageProcessor> processors) {
        processorMap = processors
                .stream()
                .collect(Collectors.toMap(StatusMessageProcessor::getType, Function.identity()));
    }

    public StatusMessageProcessor get(String type) {
        StatusMessageType statusType = StatusMessageType.fromString(type)
                .orElseThrow(() -> new NotSupportedException("Invalid status type: %s".formatted(type)));

        return Optional
                .ofNullable(processorMap.get(statusType))
                .orElseThrow(() -> new NotSupportedException("No processor for status type: %s".formatted(statusType)));
    }

}
