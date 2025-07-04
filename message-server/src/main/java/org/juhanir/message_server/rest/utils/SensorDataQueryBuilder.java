package org.juhanir.message_server.rest.utils;

import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.*;

public final class SensorDataQueryBuilder {

    private static final Logger LOG = Logger.getLogger(SensorDataQueryBuilder.class);

    private final List<String> query = new ArrayList<>();
    private final Map<String, Object> parameters = new HashMap<>();
    private static final Set<String> OPERATORS = Set.of("AND", "OR");

    private SensorDataQueryBuilder() {
    }

    public static SensorDataQueryBuilder create() {
        return new SensorDataQueryBuilder();
    }


    public SensorDataQueryBuilder and() {
        if (!query.isEmpty() && !OPERATORS.contains(query.getLast())) {
            query.add("AND");
        }
        return this;
    }

    public SensorDataQueryBuilder or() {
        if (!query.isEmpty() && !OPERATORS.contains(query.getLast())) {
            query.add("OR");
        }
        return this;
    }

    public SensorDataQueryBuilder fromDevice(long deviceId) {
        doAppend("device.id", "=", "deviceId");
        parameters.putIfAbsent("deviceId", deviceId);
        return this;
    }

    public SensorDataQueryBuilder afterTime(String from, boolean inclusive) {
        if (from == null) return handleNullParam();
        String op = inclusive ? ">=" : ">";
        doAppend("measurementTime", op, "from");
        parameters.putIfAbsent("from", Instant.parse(from));
        return this;
    }

    public SensorDataQueryBuilder beforeTime(String to, boolean inclusive) {
        if (to == null) return handleNullParam();
        String op = inclusive ? "<=" : "<";
        doAppend("measurementTime", op, "to");
        parameters.putIfAbsent("to", Instant.parse(to));
        return this;
    }

    public QueryAndParams build() throws IllegalArgumentException {
        String queryString = String.join(" ", query);
        LOG.infof("Building query string: %s", queryString);
        int counter = 3;
        for (String piece : query) {
            if (OPERATORS.contains(piece)) {
                counter = 3;
            } else {
                counter -= 1;
            }
        }
        if (counter != 0) {
            throw new IllegalArgumentException("Invalid query string: " + queryString);
        }
        return new QueryAndParams(queryString, parameters);
    }

    private void doAppend(String key, String operator, String paramName) {
        query.add(key);
        query.add(operator);
        query.add(":%s".formatted(paramName));
    }

    private SensorDataQueryBuilder handleNullParam() {
        if (!query.isEmpty() && OPERATORS.contains(query.getLast())) {
            query.removeLast();
        }
        return this;
    }

    public record QueryAndParams(String query, Map<String, Object> params) {
    }

}
