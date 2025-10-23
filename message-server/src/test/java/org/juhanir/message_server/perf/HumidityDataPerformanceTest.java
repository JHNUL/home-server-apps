package org.juhanir.message_server.perf;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.QuarkusTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static org.juhanir.message_server.utils.TestConstants.HUMIDITY_URL_TPL;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("performance")
@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityDataPerformanceTest extends QuarkusTestUtils {

    private static final int QUERY_NFR_TIME_LIMIT_MS = 150;
    private static String identifier;

    @BeforeEach
    void setUp() {
        identifier = createDeviceToDatabase();
        long deviceId = getDevice(identifier).getId();
        // 365*24*60=525600 measurements
        seedRandomHumidityData(deviceId, "2024-01-01", "2024-12-31", "1 minute");
        given().get("devices/%s".formatted(identifier)); // Triggers classloading, etc.
    }

    /**
     * OK this is silly AF, but for now keep just one @Test-annotated method and run all the perf tests inside it.
     * Why? Until a better way to seed data is implemented, must do it in @BeforeEach, because that's where
     * Vert.x considers it OK. @BeforeAll is no good.
     */
    @Test
    void performanceTest() {
        perfTestAssert("Query from the beginning of dataset", QUERY_NFR_TIME_LIMIT_MS, () ->
                given()
                        .get(HUMIDITY_URL_TPL.formatted(identifier, "?from=2024-01-01T06:06:06Z&to=2024-01-01T06:26:06Z"))
                        .then()
                        .statusCode(200)
        );

        perfTestAssert("Query from the end of dataset", QUERY_NFR_TIME_LIMIT_MS, () ->
                given()
                        .get(HUMIDITY_URL_TPL.formatted(identifier, "?from=2024-12-31T06:06:06Z&to=2024-12-31T06:26:06Z"))
                        .then()
                        .statusCode(200)
        );
    }

    private void perfTestAssert(String perfTestCase, int ms, Supplier<?> restCall) {
        long start = System.nanoTime();
        restCall.get();
        double durationMs = (System.nanoTime() - start) * 1.0 / 1_000_000;
        assertTrue(durationMs < ms, "%s: Required time %s, but took %s".formatted(perfTestCase, ms, durationMs));
    }
}
