package org.juhanir.message_server.rest.resource;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.juhanir.domain.sensordata.dto.outgoing.TemperatureStatusResponse;
import org.juhanir.message_server.mqtt.MqttClient;
import org.juhanir.message_server.repository.DeviceRepository;
import org.juhanir.message_server.repository.TemperatureRepository;
import org.juhanir.message_server.rest.api.TemperatureApi;

import java.util.List;

public class TemperatureResource implements TemperatureApi {

    private static final Logger LOG = Logger.getLogger(MqttClient.class);

    private final TemperatureRepository temperatureStorage;
    private final DeviceRepository deviceStorage;

    @Inject
    public TemperatureResource(TemperatureRepository temperatureStorage, DeviceRepository deviceStorage) {
        this.temperatureStorage = temperatureStorage;
        this.deviceStorage = deviceStorage;
    }

    @Override
    @WithSession
    public Uni<Response> getTemperatures(String deviceIdentifier) {
        return deviceStorage.findByIdentifier(deviceIdentifier)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("No device found with identifier %s".formatted(deviceIdentifier)))
                .onItem()
                .transformToUni(device -> temperatureStorage
                        .findByDeviceId(device.getId())
                        .map(temps -> {
                            List<TemperatureStatusResponse> body = temps
                                    .stream()
                                    .map(TemperatureStatusResponse::fromTemperatureStatus)
                                    .toList();
                            return Response.ok(body).build();
                        }));
    }
}
