package org.juhanir.message_server.rest.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SensorDataQueryBuilderTest {

    private static final String IDENTIFIER = "abcdefg123456";

    @Test
    void doesNotAppendAdjacentOperators() {
        var q = SensorDataQueryBuilder
                .create()
                .fromDevice(IDENTIFIER)
                .and()
                .and()
                .or()
                .beforeTime("2020-10-10T10:10:10Z", true)
                .build();
        assertEquals("device.identifier = :identifier AND measurementTime <= :to", q.query());
        assertEquals(Instant.parse("2020-10-10T10:10:10Z"), q.params().get("to"));
        assertEquals(IDENTIFIER, q.params().get("identifier"));
    }

    @Test
    void complexValidQueryBuildsCorrectly() {
        var q = SensorDataQueryBuilder
                .create()
                .fromDevice(IDENTIFIER)
                .and()
                .afterTime("2023-01-01T00:00:00Z", false)
                .or()
                .beforeTime("2024-01-01T00:00:00Z", true)
                .build();

        assertEquals(
                "device.identifier = :identifier AND measurementTime > :from OR measurementTime <= :to",
                q.query()
        );
        assertEquals(3, q.params().size());
        assertEquals(IDENTIFIER, q.params().get("identifier"));
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), q.params().get("from"));
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), q.params().get("to"));
    }


    @Test
    void invalidQueryThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            SensorDataQueryBuilder
                    .create()
                    .fromDevice(IDENTIFIER)
                    .beforeTime("2020-10-10T10:10:10Z", true)
                    .build();
        });
        assertThrows(IllegalArgumentException.class, () -> {
            SensorDataQueryBuilder
                    .create()
                    .and()
                    .build();
        });
    }

    @Test
    void operatorAtStartNotAdded() {
        var q = SensorDataQueryBuilder
                .create()
                .and()
                .fromDevice(IDENTIFIER)
                .build();
        assertEquals("device.identifier = :identifier", q.query());
        assertEquals(IDENTIFIER, q.params().get("identifier"));
    }

    @Test
    void operatorAtEndThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            SensorDataQueryBuilder
                    .create()
                    .fromDevice(IDENTIFIER)
                    .and()
                    .build();
        });
    }

    @Test
    void nullTimeSkipsFilter() {
        SensorDataQueryBuilder.QueryAndParams q = SensorDataQueryBuilder
                .create()
                .fromDevice(IDENTIFIER)
                .and()
                .afterTime(null, true)
                .and()
                .beforeTime(null, true)
                .build();

        assertEquals("device.identifier = :identifier", q.query());
        assertEquals(1, q.params().size());
        assertEquals(IDENTIFIER, q.params().get("identifier"));
    }
}
