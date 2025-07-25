package org.juhanir.message_server.mqtt;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.smallrye.reactive.messaging.mqtt.MqttMessageMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.jboss.logging.Logger;
import org.juhanir.message_server.message.mapper.StatusMessageMapperFactory;
import org.juhanir.message_server.message.processor.StatusMessageProcessorFactory;

@ApplicationScoped
public class StatusClient {

    private static final Logger LOG = Logger.getLogger(StatusClient.class);

    private final StatusMessageProcessorFactory processorFactory;
    private final StatusMessageMapperFactory mapperFactory;

    @Inject
    public StatusClient(StatusMessageProcessorFactory processorFactory, StatusMessageMapperFactory mapperFactory) {
        this.processorFactory = processorFactory;
        this.mapperFactory = mapperFactory;
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
        return Uni.createFrom()
                .item(Unchecked.supplier(() -> mapperFactory.get(statusType).mapFromMqtt(messagePayload)))
                .onItem()
                .transformToUni(measurement -> processorFactory.get(statusType).process(measurement, deviceIdentifier))
                .onFailure().invoke(throwable -> {
                    String errorMsg = "Failed to process message from %s: %s".formatted(deviceIdentifier, throwable.getMessage());
                    LOG.error(errorMsg, throwable);
                })
                .onFailure().recoverWithUni(err -> Uni.createFrom().voidItem());
    }

}
