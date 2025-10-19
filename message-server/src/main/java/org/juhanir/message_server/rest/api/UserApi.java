package org.juhanir.message_server.rest.api;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.juhanir.domain.sensordata.dto.outgoing.UserResponse;

@Path("users")
public interface UserApi {

    @GET
    @Path("/me")
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Fetch user information.")
    @APIResponse(
            responseCode = "200",
            description = "User information fetched successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = UserResponse.class)
            )
    )
    public Uni<Response> me();

}
