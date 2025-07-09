package org.juhanir.message_server.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@ApplicationScoped
public class TestConfiguration {

    private final Config config = ConfigProvider.getConfig();

    public String getDbUrl() {
        return config.getValue("quarkus.datasource.reactive.url", String.class);
    }

    public String getDbUsername() {
        return config.getValue("quarkus.datasource.username", String.class);
    }

    public String getDbPassword() {
        return config.getValue("quarkus.datasource.password", String.class);
    }

}
