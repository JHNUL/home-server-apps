package org.juhanir.message_server.mqtt;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.juhanir.domain.sensordata.Temperature;
import org.juhanir.domain.sensordata.TemperatureUnit;
import org.juhanir.message_server.repository.TemperatureRepository;

import java.math.BigDecimal;

@ApplicationScoped
public class MqttClient {

    private static final Logger LOG = Logger.getLogger(MqttClient.class);
    private final EventBus bus;
    private final TemperatureRepository repository;

    @Inject
    public MqttClient(EventBus bus, TemperatureRepository repository) {
        this.bus = bus;
        this.repository = repository;
    }

    @Incoming("message")
    public Uni<Void> process(Message<String> msg) {
        LOG.info("Got incoming MQTT message: %s".formatted(msg.getPayload()));
        bus.send("message", msg.getPayload());
        Temperature temp = new Temperature();
        temp.setTimestamp(null);
        temp.setUnit(TemperatureUnit.CELSIUS);
        temp.setValue(new BigDecimal("-18.3"));
        return Panache.withTransaction(() -> repository
                .persist(temp)
                .onFailure()
                .invoke(throwable -> {
                    LOG.error("ERRRRROR", throwable);
                })
                .replaceWithVoid());
    }
}
