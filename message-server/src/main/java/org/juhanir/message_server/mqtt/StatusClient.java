package org.juhanir.message_server.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.reactive.messaging.mqtt.MqttMessageMetadata;
import io.vertx.mutiny.core.eventbus.EventBus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
                .onItem()
                .transformToUni(none -> Uni.createFrom().completionStage(msg.ack()))
                .onFailure()
                .recoverWithUni(t -> Uni.createFrom().completionStage(msg.nack(t)));

    }

    private Uni<Device> findOrCreateDevice(String deviceIdentifier) {
        return deviceRepository.findByIdentifier(deviceIdentifier)
                .onItem().ifNotNull().transform(device -> device)
                .onItem().ifNull().switchTo(() -> deviceTypeRepository.findByName(DeviceTypeName.TEMPERATURE_HUMIDITY_SENSOR)
                        .onFailure().invoke(t -> LOG.errorf("Failed to fetch device type %s", t))
                        .onItem()
                        .transformToUni(deviceType -> {
                            Device device = new Device()
                                    .setDeviceType(deviceType)
                                    .setIdentifier(deviceIdentifier);
                            return deviceRepository.persist(device);
                        }));
    }

    private Uni<Void> processTemperature(String payload, Device device) {
        LOG.infof("Got temperature message from %s, payload %s", device.getIdentifier(), payload);
        return Uni.createFrom()
                .item(Unchecked.supplier(() -> {
                    TemperatureStatus ts = TemperatureStatus.fromMqttPayload(mapper.readValue(payload, TemperatureStatusMqttPayload.class));
                    ts.setDevice(device);
                    return ts;
                }))
                .onFailure().invoke(t -> LOG.errorf("Failed to serialize %s", payload))
                .onItem()
                .transformToUni(tempStatus -> repository.persist(tempStatus))
                .invoke(() -> LOG.infof("Persisted tempStatus successfully"))
                .onFailure()
                .invoke(t -> LOG.errorf("Could not persist temperature %s", t))
                .onItem().invoke(tempStatus -> bus.send("message", String.valueOf(tempStatus.getId())))
                .onFailure().invoke(t -> LOG.errorf("Failure %s", t))
                .onFailure()
                .recoverWithNull()
                .replaceWithVoid();
    }

    private Uni<Void> processHumidity(String payload, Device device) {
        LOG.infof("Got humidity message from %s, payload %s", device.getIdentifier(), payload);
        return Uni.createFrom()
                .item(Unchecked.supplier(() -> {
                    HumidityStatus hs = HumidityStatus.fromMqttPayload(mapper.readValue(payload, HumidityStatusMqttPayload.class));
                    hs.setDevice(device);
                    return hs;
                }))
                .onFailure().invoke(t -> LOG.errorf("Failed to serialize %s", payload))
                .onItem()
                .transformToUni(humidityStatus -> humidityRepository.persist(humidityStatus))
                .onFailure()
                .invoke(t -> LOG.errorf("Could not persist temperature %s", t))
                .onItem().invoke(humidityStatus -> bus.send("message", String.valueOf(humidityStatus.getId())))
                .onFailure().invoke(t -> LOG.errorf("Failure %s", t))
                .onFailure()
                .recoverWithNull()
                .replaceWithVoid();
    }
}
