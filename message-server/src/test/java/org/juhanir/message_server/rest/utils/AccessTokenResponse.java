package org.juhanir.message_server.rest.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccessTokenResponse(String accessToken, String scope) {

    @JsonCreator
    public AccessTokenResponse(
            @JsonProperty(value = "access_token", required = true) String accessToken,
            @JsonProperty(value = "scope", required = true) String scope
    ) {
        this.accessToken = accessToken;
        this.scope = scope;
    }

}
