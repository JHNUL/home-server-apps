package org.juhanir.message_server.rest.resource;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.juhanir.domain.sensordata.dto.outgoing.HumidityStatusResponse;
import org.juhanir.domain.sensordata.dto.outgoing.TemperatureStatusResponse;
import org.juhanir.message_server.repository.DeviceRepository;
import org.juhanir.message_server.repository.HumidityRepository;
import org.juhanir.message_server.repository.TemperatureRepository;
import org.juhanir.message_server.rest.api.DeviceApi;

import java.util.List;

public class DeviceResource implements DeviceApi {

    private static final Logger LOG = Logger.getLogger(DeviceResource.class);

    private final TemperatureRepository temperatureStorage;
    private final HumidityRepository humidityStorage;
    private final DeviceRepository deviceStorage;

    @Inject
    public DeviceResource(
            TemperatureRepository temperatureStorage,
            HumidityRepository humidityStorage,
            DeviceRepository deviceStorage) {
        this.temperatureStorage = temperatureStorage;
        this.humidityStorage = humidityStorage;
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

    @Override
    @WithSession
    public Uni<Response> getHumidity(String deviceIdentifier) {
        return deviceStorage.findByIdentifier(deviceIdentifier)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException("No device found with identifier %s".formatted(deviceIdentifier)))
                .onItem()
                .transformToUni(device -> humidityStorage
                        .findByDeviceId(device.getId())
                        .map(humids -> {
                            List<HumidityStatusResponse> body = humids
                                    .stream()
                                    .map(HumidityStatusResponse::fromHumidityStatus)
                                    .toList();
                            return Response.ok(body).build();
                        }));
    }
}
