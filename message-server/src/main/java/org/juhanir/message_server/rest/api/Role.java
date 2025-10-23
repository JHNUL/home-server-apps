package org.juhanir.message_server.rest.api;

import java.util.Collection;
import java.util.Set;

public abstract class Role {

    public static final String USER = "user";
    public static final String ADMIN = "admin";

    public static Collection<String> getAllRoles() {
        return Set.of(USER, ADMIN);
    }

}
