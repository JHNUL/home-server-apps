package org.juhanir.message_server.rest.resource;

import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.NoCache;
import org.juhanir.domain.sensordata.dto.outgoing.UserResponse;
import org.juhanir.message_server.rest.api.UserApi;

public class UserResource implements UserApi {

    private final SecurityIdentity identity;

    @Inject
    public UserResource(SecurityIdentity identity) {
        this.identity = identity;
    }

    @Override
    @NoCache
    public Uni<Response> me() {
        return Uni
                .createFrom()
                .item(Response.ok(new UserResponse(identity)).build());
    }
}
