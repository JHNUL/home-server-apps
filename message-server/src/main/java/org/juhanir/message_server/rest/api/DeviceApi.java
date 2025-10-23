package org.juhanir.message_server.rest.api;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.juhanir.domain.sensordata.dto.outgoing.DeviceResponse;
import org.juhanir.domain.sensordata.dto.outgoing.HumidityStatusResponse;
import org.juhanir.domain.sensordata.dto.outgoing.TemperatureStatusResponse;

@Path("devices")
@RolesAllowed({Role.USER, Role.ADMIN})
public interface DeviceApi {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Fetch all devices.")
    @APIResponse(
            responseCode = "200",
            description = "Devices fetched successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DeviceResponse.class, type = SchemaType.ARRAY)
            )
    )
    Uni<Response> getDevices();

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
            description = "Temperatures fetched successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = TemperatureStatusResponse.class, type = SchemaType.ARRAY)
            )
    )
    Uni<Response> getTemperatures(
            @PathParam("deviceIdentifier") String deviceIdentifier,
            @BeanParam @Valid TimeSeriesQueryParams queryParams
    );

    @GET
    @Path("/{deviceIdentifier}/humidity")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Get a list of humidity readings for a given device.")
    @APIResponse(
            responseCode = "200",
            description = "Humidity readings fetched successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = HumidityStatusResponse.class, type = SchemaType.ARRAY)
            )
    )
    Uni<Response> getHumidity(
            @PathParam("deviceIdentifier") String deviceIdentifier,
            @BeanParam @Valid TimeSeriesQueryParams queryParams
    );

    @DELETE
    @Path("/{deviceIdentifier}")
    @Operation(summary = "Delete a device (and all its measurements)")
    @APIResponse(
            responseCode = "204",
            description = "Device deleted successfully."
    )
    @RolesAllowed({Role.ADMIN})
    Uni<Response> deleteDevice(@PathParam("deviceIdentifier") String deviceIdentifier);

}
