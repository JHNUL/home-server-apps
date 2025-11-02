package org.juhanir.message_server.rest.resource;

import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.juhanir.domain.sensordata.dto.outgoing.DeviceResponse;
import org.juhanir.message_server.repository.DeviceRepository;
import org.juhanir.message_server.rest.api.DeviceApi;

import java.util.List;

public class DeviceResource implements DeviceApi {

    private final DeviceRepository deviceStorage;

    @Inject
    public DeviceResource(DeviceRepository deviceStorage) {
        this.deviceStorage = deviceStorage;
    }

    @Override
    @WithSession
    public Uni<Response> getDevices() {
        return deviceStorage.findAllWithType(Sort.ascending("createdAt"))
                .map(devices -> {
                    List<DeviceResponse> body = devices
                            .stream()
                            .map(DeviceResponse::fromDevice)
                            .toList();
                    return Response.ok(body).build();
                });
    }

    @Override
    @WithSession
    public Uni<Response> getDevice(String deviceIdentifier) {
        return deviceStorage.findByIdentifierWithType(deviceIdentifier)
                .onItem().ifNull()
                .failWith(new NotFoundException("No device found with identifier %s".formatted(deviceIdentifier)))
                .onItem()
                .transform(device -> Response.ok(DeviceResponse.fromDevice(device)).build());
    }

    @Override
    @WithTransaction
    public Uni<Response> deleteDevice(String identifier) {
        return deviceStorage.delete("identifier", identifier)
                .onItem()
                .transform((id) -> Response.noContent().build());
    }

}
