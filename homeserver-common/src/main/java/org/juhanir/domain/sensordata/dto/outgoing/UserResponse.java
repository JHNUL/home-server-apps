package org.juhanir.domain.sensordata.dto.outgoing;

import io.quarkus.security.identity.SecurityIdentity;

public class UserResponse {

    private final String username;

    public UserResponse(SecurityIdentity identity) {
        this.username = identity.getPrincipal().getName();
    }

    public String getUsername() {
        return username;
    }
}
