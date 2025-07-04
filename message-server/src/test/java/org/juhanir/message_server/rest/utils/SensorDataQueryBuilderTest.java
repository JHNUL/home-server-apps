package org.juhanir.message_server.rest.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SensorDataQueryBuilderTest {

    @Test
    void doesNotAppendAdjacentOperators() {
        var q = SensorDataQueryBuilder
                .create()
                .fromDevice(1L)
                .and()
                .and()
                .or()
                .beforeTime("2020-10-10T10:10:10Z", true)
                .build();
        assertEquals("device.id = :deviceId AND measurementTime <= :to", q.query());
        assertEquals(Instant.parse("2020-10-10T10:10:10Z"), q.params().get("to"));
        assertEquals(1L, q.params().get("deviceId"));
    }

    @Test
    void complexValidQueryBuildsCorrectly() {
        var q = SensorDataQueryBuilder
                .create()
                .fromDevice(42L)
                .and()
                .afterTime("2023-01-01T00:00:00Z", false)
                .or()
                .beforeTime("2024-01-01T00:00:00Z", true)
                .build();

        assertEquals(
                "device.id = :deviceId AND measurementTime > :from OR measurementTime <= :to",
                q.query()
        );
        assertEquals(3, q.params().size());
        assertEquals(42L, q.params().get("deviceId"));
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), q.params().get("from"));
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), q.params().get("to"));
    }


    @Test
    void invalidQueryThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            SensorDataQueryBuilder
                    .create()
                    .fromDevice(1L)
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
                .fromDevice(1L)
                .build();
        assertEquals("device.id = :deviceId", q.query());
        assertEquals(1L, q.params().get("deviceId"));
    }

    @Test
    void operatorAtEndThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            SensorDataQueryBuilder
                    .create()
                    .fromDevice(1L)
                    .and()
                    .build();
        });
    }

    @Test
    void nullTimeSkipsFilter() {
        SensorDataQueryBuilder.QueryAndParams q = SensorDataQueryBuilder
                .create()
                .fromDevice(5L)
                .and()
                .afterTime(null, true)
                .and()
                .beforeTime(null, true)
                .build();

        assertEquals("device.id = :deviceId", q.query());
        assertEquals(1, q.params().size());
        assertEquals(5L, q.params().get("deviceId"));
    }
}
