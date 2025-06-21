package org.juhanir.message_server.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.MqttMessage;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;
import org.juhanir.message_server.repository.TemperatureRepository;

@ApplicationScoped
public class MqttClient {

    private static final Logger LOG = Logger.getLogger(MqttClient.class);
    private final EventBus bus;
    private final TemperatureRepository repository;
    private final ObjectMapper mapper;

    @Inject
    public MqttClient(EventBus bus, TemperatureRepository repository, ObjectMapper mapper) {
        this.bus = bus;
        this.repository = repository;
        this.mapper = mapper;
    }

    private String getDeviceIdentifierFromTopic(String topic) {
        return topic.split("/")[0];
    }

    private String getStatusTypeFromTopic(String topic) {
        String[] pcs = topic.split("/");
        String last = pcs[pcs.length - 1];
        return last.split(":")[0];
    }

    @Incoming("shelly-status")
    public Uni<Void> process(MqttMessage<String> msg) {
        String topic = msg.getTopic();
        String deviceIdentifier = getDeviceIdentifierFromTopic(topic);
        String statusType = getStatusTypeFromTopic(topic);

        return switch (statusType) {
            case "humidity" -> processHumidity(msg);
            case "temperature" -> processTemperature(msg);
            default -> {
                LOG.warnf("Unsupported status topic %s", statusType);
                yield Uni.createFrom().voidItem();
            }
        };

//        LOG.info("Got incoming MQTT message from %s: %s".formatted(deviceIdentifier, msg.getPayload()));
//        bus.send("message", msg.getPayload());
//        return Panache.withTransaction(() -> repository.persist(null))
//                .onFailure().invoke(t -> LOG.error("Could not persist temperature %s".formatted(temp), t))
//                .onFailure().recoverWithNull().replaceWithVoid();
    }

    private Uni<Void> processTemperature(MqttMessage<String> msg) {
        LOG.infof("Got temperature message %s", msg.getPayload());
        return Uni.createFrom().voidItem();
    }

    private Uni<Void> processHumidity(MqttMessage<String> msg) {
        LOG.infof("Got humidity message %s", msg.getPayload());
        return Uni.createFrom().voidItem();
    }
}
