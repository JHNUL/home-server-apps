package org.juhanir.message_server.rest.utils;

import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.*;

public final class SignalDataQueryBuilder {

    private static final Logger LOG = Logger.getLogger(SignalDataQueryBuilder.class);

    private final List<String> query = new ArrayList<>();
    private final String joiner = "SELECT s FROM SignalData s JOIN FETCH s.device";
    private final Map<String, Object> parameters = new HashMap<>();
    private static final Set<String> OPERATORS = Set.of("AND", "OR");

    public static SignalDataQueryBuilder create() {
        return new SignalDataQueryBuilder();
    }

    public SignalDataQueryBuilder and() {
        if (!query.isEmpty() && !OPERATORS.contains(query.getLast())) {
            query.add("AND");
        }
        return this;
    }

    public SignalDataQueryBuilder or() {
        if (!query.isEmpty() && !OPERATORS.contains(query.getLast())) {
            query.add("OR");
        }
        return this;
    }

    public SignalDataQueryBuilder fromDevices(List<String> identifiers) {
        if (identifiers.isEmpty() || identifiers.getFirst().isBlank()) return handleNullParam();
        doAppend("s.device.identifier", "IN", "identifiers");
        parameters.putIfAbsent("identifiers", identifiers);
        return this;
    }

    public SignalDataQueryBuilder afterTime(String from, boolean inclusive) {
        if (from == null) return handleNullParam();
        String op = inclusive ? ">=" : ">";
        doAppend("s.measurementTime", op, "from");
        parameters.putIfAbsent("from", Instant.parse(from));
        return this;
    }

    public SignalDataQueryBuilder beforeTime(String to, boolean inclusive) {
        if (to == null) return handleNullParam();
        String op = inclusive ? "<=" : "<";
        doAppend("s.measurementTime", op, "to");
        parameters.putIfAbsent("to", Instant.parse(to));
        return this;
    }

    public QueryAndParams build() throws IllegalArgumentException {
        String queryString = "%s%s%s".formatted(joiner, query.isEmpty() ? "" : " WHERE ", String.join(" ", query));
        LOG.debugf("Building query string: %s", queryString);
        int counter = 3;
        for (String piece : query) {
            if (OPERATORS.contains(piece)) {
                counter = 3;
            } else {
                counter -= 1;
            }
        }
        if (!query.isEmpty() && counter != 0) {
            throw new IllegalArgumentException("Invalid query string: %s".formatted(queryString));
        }
        return new QueryAndParams(queryString, parameters);
    }

    private void doAppend(String key, String operator, String paramName) {
        query.add(key);
        query.add(operator);
        query.add(":%s".formatted(paramName));
    }

    private SignalDataQueryBuilder handleNullParam() {
        if (!query.isEmpty() && OPERATORS.contains(query.getLast())) {
            query.removeLast();
        }
        return this;
    }

    public record QueryAndParams(String query, Map<String, Object> params) {
    }

}
