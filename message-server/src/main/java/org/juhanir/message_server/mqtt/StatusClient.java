package org.juhanir.message_server.mqtt;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.mqtt.MqttMessageMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.juhanir.message_server.service.DeviceService;
import org.juhanir.message_server.service.HumidityService;
import org.juhanir.message_server.service.TemperatureService;

@ApplicationScoped
public class StatusClient {

    private static final Logger LOG = Logger.getLogger(StatusClient.class);
    private static final String STATUS_TOPIC_HUMIDITY = "humidity";
    private static final String STATUS_TOPIC_TEMPERATURE = "temperature";

    private final TemperatureService temperatureService;
    private final HumidityService humidityService;
    private final DeviceService deviceService;


    @Inject
    public StatusClient(TemperatureService temperatureService, HumidityService humidityService, DeviceService deviceService) {
        this.temperatureService = temperatureService;
        this.humidityService = humidityService;
        this.deviceService = deviceService;
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

        return deviceService.findOrCreateDevice(deviceIdentifier)
                .onItem()
                .transformToUni(device -> switch (statusType) {
                    case STATUS_TOPIC_HUMIDITY -> humidityService.processHumidity(messagePayload, device);
                    case STATUS_TOPIC_TEMPERATURE -> temperatureService.processTemperature(messagePayload, device);
                    default -> {
                        LOG.warnf("Unsupported status topic %s", statusType);
                        yield Uni.createFrom().voidItem();
                    }
                })
                .onFailure().invoke(throwable -> {
                    String errorMsg = "Failed to process message from %s: %s".formatted(deviceIdentifier, throwable.getMessage());
                    LOG.error(errorMsg, throwable);
                })
                .onFailure().recoverWithUni(err -> Uni.createFrom().voidItem());
    }

}
