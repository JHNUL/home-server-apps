package org.juhanir.message_server.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.reactive.messaging.mqtt.MqttMessageMetadata;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.juhanir.domain.sensordata.dto.incoming.HumidityStatusMqttPayload;
import org.juhanir.domain.sensordata.dto.incoming.TemperatureStatusMqttPayload;
import org.juhanir.domain.sensordata.entity.Device;
import org.juhanir.domain.sensordata.entity.DeviceTypeName;
import org.juhanir.domain.sensordata.entity.HumidityStatus;
import org.juhanir.domain.sensordata.entity.TemperatureStatus;
import org.juhanir.message_server.repository.DeviceRepository;
import org.juhanir.message_server.repository.DeviceTypeRepository;
import org.juhanir.message_server.repository.HumidityRepository;
import org.juhanir.message_server.repository.TemperatureRepository;

@ApplicationScoped
public class StatusClient {

    private static final Logger LOG = Logger.getLogger(StatusClient.class);
    private final EventBus bus;
    private final TemperatureRepository repository;
    private final DeviceRepository deviceRepository;
    private final DeviceTypeRepository deviceTypeRepository;
    private final HumidityRepository humidityRepository;
    private final ObjectMapper mapper;

    @Inject
    public StatusClient(
            EventBus bus,
            TemperatureRepository repository,
            DeviceRepository deviceRepository,
            DeviceTypeRepository deviceTypeRepository,
            HumidityRepository humidityRepository,
            ObjectMapper mapper) {
        this.bus = bus;
        this.repository = repository;
        this.deviceRepository = deviceRepository;
        this.deviceTypeRepository = deviceTypeRepository;
        this.humidityRepository = humidityRepository;
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

    private String getTopicFromMessage(Message<String> msg) {
        return msg
                .getMetadata(MqttMessageMetadata.class)
                .map(MqttMessageMetadata::getTopic)
                .orElse("unknown_topic");
    }

    @Incoming("shelly-status")
    @WithTransaction
    public Uni<Void> process(Message<String> msg) {
        String topic = getTopicFromMessage(msg);
        String deviceIdentifier = getDeviceIdentifierFromTopic(topic);
        String statusType = getStatusTypeFromTopic(topic);
        String messagePayload = msg.getPayload();

        LOG.infof("Message from %s, to topic %s, payload %s", deviceIdentifier, topic, messagePayload);

        return findOrCreateDevice(deviceIdentifier)
                .onItem()
                .transformToUni(device -> switch (statusType) {
                    case "humidity" -> processHumidity(messagePayload, device);
                    case "temperature" -> processTemperature(messagePayload, device);
                    default -> {
                        LOG.warnf("Unsupported status topic %s", statusType);
                        yield Uni.createFrom().voidItem();
                    }
                })
                .onFailure().invoke(throwable -> LOG.errorf("Failed to process message from %s", deviceIdentifier))
                .replaceWithVoid();
    }

    private Uni<Device> findOrCreateDevice(String deviceIdentifier) {
        return deviceRepository.findByIdentifier(deviceIdentifier)
                .onItem().ifNotNull().transform(device -> device)
                .onItem().ifNull().switchTo(() -> deviceTypeRepository.findByName(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR)
                        .onItem().ifNull().failWith(() -> new NotFoundException("Device type " + DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR + " not found."))
                        .onItem().transformToUni(deviceType -> {
                            Device device = new Device()
                                    .setDeviceType(deviceType)
                                    .setIdentifier(deviceIdentifier);
                            return deviceRepository.persist(device);
                        }));
    }

    private Uni<Void> processTemperature(String payload, Device device) {
        return Uni.createFrom().item(Unchecked.supplier(() ->
                        TemperatureStatus.fromMqttPayload(mapper.readValue(payload, TemperatureStatusMqttPayload.class))
                ))
                .onItem().invoke(ts -> ts.setDevice(device))
                .onItem().transformToUni(repository::persistTemperature)
                .onItem().transform(ts -> bus.send("message", String.valueOf(ts.getId())))
                .replaceWithVoid();
    }

    private Uni<Void> processHumidity(String payload, Device device) {
        return Uni.createFrom().item(Unchecked.supplier(() ->
                        HumidityStatus.fromMqttPayload(mapper.readValue(payload, HumidityStatusMqttPayload.class))
                ))
                .onItem().invoke(hs -> hs.setDevice(device))
                .onItem().transformToUni(humidityRepository::persistHumidity)
                .onItem().transform(ts -> bus.send("message", String.valueOf(ts.getId())))
                .replaceWithVoid();
    }
}
