package org.juhanir.message_server.rest.api;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.juhanir.domain.sensordata.dto.outgoing.DeviceResponse;
import org.juhanir.domain.sensordata.dto.outgoing.HumidityStatusResponse;
import org.juhanir.domain.sensordata.dto.outgoing.TemperatureStatusResponse;

@Path("devices")
public interface DeviceApi {

    @GET
    @Path("/{deviceIdentifier}")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Fetch one device.")
    @APIResponse(
            responseCode = "200",
            description = "Device fetched successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DeviceResponse.class)
            )
    )
    Uni<Response> getDevice(@PathParam("deviceIdentifier") String deviceIdentifier);

    @GET
    @Path("/{deviceIdentifier}/temperature")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get a list of temperatures for a given device.")
    @APIResponse(
            responseCode = "200",
            description = "Temperatures fetched succesfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TemperatureStatusResponse.class)
            )
    )
    Uni<Response> getTemperatures(@PathParam("deviceIdentifier") String deviceIdentifier);

    @GET
    @Path("/{deviceIdentifier}/humidity")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get a list of humidity readings for a given device.")
    @APIResponse(
            responseCode = "200",
            description = "Humidity readings fetched succesfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = HumidityStatusResponse.class)
            )
    )
    Uni<Response> getHumidity(@PathParam("deviceIdentifier") String deviceIdentifier);

}
