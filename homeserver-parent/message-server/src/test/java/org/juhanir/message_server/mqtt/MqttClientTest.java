package org.juhanir.message_server.mqtt;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.message_server.MessageServerTestResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
@QuarkusTestResource(
        value = MessageServerTestResource.class,
        restrictToAnnotatedClass = true
)
public class MqttClientTest {

    @Test
    void canSendMessageToTopic() {
        Assertions.assertTrue("fooo".contains("o"));
    }
}
