package org.juhanir.message_server.perf;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.juhanir.message_server.MessageServerTestResource;
import org.juhanir.message_server.utils.PerfTestSeeder;
import org.juhanir.message_server.utils.TestConfiguration;
import org.junit.jupiter.api.*;

import java.util.function.Supplier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.juhanir.message_server.utils.TestConstants.HUMIDITY_URL_TPL;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("performance")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@QuarkusTest
@QuarkusTestResource(value = MessageServerTestResource.class)
public class HumidityDataPerformanceTest {

    private static final int QUERY_NFR_TIME_LIMIT_MS = 150;

    @Inject
    TestConfiguration config;

    @BeforeAll
    void setUp() {
        String dbUrl = config.getDbUrl().startsWith("jdbc")
                ? config.getDbUrl()
                : "jdbc:" + config.getDbUrl();
        PerfTestSeeder seeder = new PerfTestSeeder(
                dbUrl,
                config.getDbUsername(),
                config.getDbPassword()
        );
        seeder.seedData();

    }

    @BeforeEach
    void warmUp() {
        // Not sure if needed before every test
        // but as the actual need is to exclude all
        // bootstrapping from the measured time...
        given().get(HUMIDITY_URL_TPL.formatted("shelly-123123", "?limit=1")); // Triggers classloading, etc.
    }

    /**
     * Query from the beginning of dataset
     */
    @Test
    void performanceTest() {
        perfTestAssert(QUERY_NFR_TIME_LIMIT_MS, () ->
                given()
                        .get(HUMIDITY_URL_TPL.formatted("shelly-123123", "?from=2025-06-06T06:06:06Z&to=2025-06-06T06:26:06Z"))
                        .then()
                        .statusCode(200)
        );
    }

    private void perfTestAssert(int ms, Supplier<?> restCall) {
        long start = System.nanoTime();
        restCall.get();
        double durationMs = (System.nanoTime() - start) * 1.0 / 1_000_000;
        assertTrue(durationMs < ms, "Required time %s, but took %s".formatted(ms, durationMs));
    }
}
