package org.juhanir.message_server.rest.api;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.juhanir.domain.sensordata.dto.outgoing.DeviceResponse;

@Path("signaldata")
@RolesAllowed({Role.USER, Role.ADMIN})
public interface SignalDataApi {

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Fetch signal data.")
    @APIResponse(
            responseCode = "200",
            description = "Devices fetched successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = DeviceResponse.class, type = SchemaType.ARRAY)
            )
    )
    Uni<Response> getDevices();

}
