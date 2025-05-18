package org.juhanir.message_server.repository;

import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.juhanir.domain.sensordata.Temperature;

@ApplicationScoped
public class TemperatureRepository implements PanacheRepository<Temperature> {
}
