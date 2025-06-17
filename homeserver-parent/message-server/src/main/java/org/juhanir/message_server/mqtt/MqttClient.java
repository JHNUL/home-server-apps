package org.juhanir.message_server.mqtt;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.MqttMessageMetadata;
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

    private String getDeviceIdFromTopic(Message<String> msg) {
        String topic = msg
                .getMetadata(MqttMessageMetadata.class)
                .map(MqttMessageMetadata::getTopic)
                .orElse("unknown sender");
        return topic.split("/")[0];
    }

    @Incoming("shelly-events")
    public Uni<Void> process(Message<String> msg) {
        String deviceId = getDeviceIdFromTopic(msg);
        LOG.info("Got incoming MQTT message from %s: %s".formatted(deviceId, msg.getPayload()));
        bus.send("message", msg.getPayload());
        Temperature temp = new Temperature();
        temp.setTimestamp(null);
        temp.setUnit(TemperatureUnit.CELSIUS);
        temp.setValue(new BigDecimal("-18.3"));
        return Panache.withTransaction(() -> repository.persist(temp))
                .onFailure().invoke(t -> LOG.error("Could not persist temperature %s".formatted(temp), t))
                .onFailure().recoverWithNull().replaceWithVoid();
    }
}
