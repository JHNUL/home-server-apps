package org.juhanir.message_server.rest.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SignalDataQueryBuilderTest {

    private static final String IDENTIFIER = "abcdefg123456";

    @Test
    void doesNotAppendAdjacentOperators() {
        var q = SignalDataQueryBuilder
                .create()
                .fromDevices(List.of(IDENTIFIER))
                .and()
                .and()
                .or()
                .beforeTime("2020-10-10T10:10:10Z", true)
                .build();
        assertEquals("SELECT s FROM SignalData s JOIN FETCH s.device WHERE s.device.identifier IN :identifiers AND s.measurementTime <= :to", q.query());
        assertEquals(Instant.parse("2020-10-10T10:10:10Z"), q.params().get("to"));
        assertEquals(List.of(IDENTIFIER), q.params().get("identifiers"));
    }

    @Test
    void complexValidQueryBuildsCorrectly() {
        var q = SignalDataQueryBuilder
                .create()
                .fromDevices(List.of(IDENTIFIER, "foo"))
                .and()
                .afterTime("2023-01-01T00:00:00Z", false)
                .or()
                .beforeTime("2024-01-01T00:00:00Z", true)
                .build();

        assertEquals(
                "SELECT s FROM SignalData s JOIN FETCH s.device WHERE s.device.identifier IN :identifiers AND s.measurementTime > :from OR s.measurementTime <= :to",
                q.query()
        );
        assertEquals(3, q.params().size());
        assertEquals(List.of(IDENTIFIER, "foo"), q.params().get("identifiers"));
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), q.params().get("from"));
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), q.params().get("to"));
    }

    @Test
    void emptyDevicesList() {
        var q = SignalDataQueryBuilder
                .create()
                .fromDevices(List.of())
                .and()
                .afterTime("2023-01-01T00:00:00Z", false)
                .or()
                .beforeTime("2024-01-01T00:00:00Z", true)
                .build();

        assertEquals(
                "SELECT s FROM SignalData s JOIN FETCH s.device WHERE s.measurementTime > :from OR s.measurementTime <= :to",
                q.query()
        );
        assertEquals(2, q.params().size());
        assertNull(q.params().get("identifiers"));
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), q.params().get("from"));
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), q.params().get("to"));
    }

    @Test
    void noQueryParams() {
        var q = SignalDataQueryBuilder
                .create()
                .fromDevices(List.of())
                .build();

        assertEquals(
                "SELECT s FROM SignalData s JOIN FETCH s.device",
                q.query()
        );
        assertEquals(0, q.params().size());
        assertNull(q.params().get("identifiers"));
    }

    @Test
    void emptyDevicesListInTheMiddle() {
        var q = SignalDataQueryBuilder
                .create()
                .afterTime("2023-01-01T00:00:00Z", false)
                .and()
                .fromDevices(List.of())
                .and()
                .beforeTime("2024-01-01T00:00:00Z", true)
                .build();

        assertEquals(
                "SELECT s FROM SignalData s JOIN FETCH s.device WHERE s.measurementTime > :from AND s.measurementTime <= :to",
                q.query()
        );
        assertEquals(2, q.params().size());
        assertNull(q.params().get("identifiers"));
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), q.params().get("from"));
        assertEquals(Instant.parse("2024-01-01T00:00:00Z"), q.params().get("to"));
    }


    @Test
    void invalidQueryThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            SignalDataQueryBuilder
                    .create()
                    .fromDevices(List.of(IDENTIFIER))
                    .beforeTime("2020-10-10T10:10:10Z", true)
                    .build();
        });
    }

    @Test
    void operatorAtStartNotAdded() {
        var q = SignalDataQueryBuilder
                .create()
                .and()
                .fromDevices(List.of(IDENTIFIER))
                .build();
        assertEquals("SELECT s FROM SignalData s JOIN FETCH s.device WHERE s.device.identifier IN :identifiers", q.query());
        assertEquals(List.of(IDENTIFIER), q.params().get("identifiers"));
    }

    @Test
    void operatorAtEndThrows() {
        assertThrows(IllegalArgumentException.class, () -> {
            SignalDataQueryBuilder
                    .create()
                    .fromDevices(List.of("foo"))
                    .and()
                    .build();
        });
    }

    @Test
    void nullTimeSkipsFilter() {
        SignalDataQueryBuilder.QueryAndParams q = SignalDataQueryBuilder
                .create()
                .fromDevices(List.of(IDENTIFIER))
                .and()
                .afterTime(null, true)
                .and()
                .beforeTime(null, true)
                .build();

        assertEquals("SELECT s FROM SignalData s JOIN FETCH s.device WHERE s.device.identifier IN :identifiers", q.query());
        assertEquals(1, q.params().size());
        assertEquals(List.of(IDENTIFIER), q.params().get("identifiers"));
    }
}
