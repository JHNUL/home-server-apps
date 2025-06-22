package org.juhanir.message_server.utils;

import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;

import java.util.concurrent.TimeUnit;

public final class AwaitUtils {

    private AwaitUtils() {
    }

    /**
     * Repeatedly checks the asserted condition at most for 5 seconds in intervals of 500ms
     * until the assertion passes or timeout.
     * @param assertion the assertion that should pass
     */
    public static void awaitAssertion(ThrowingRunnable assertion) {
        Awaitility
                .await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(assertion);
    }

    /**
     * Repeatedly checks the asserted condition for 3 seconds in intervals of 500ms
     * expecting it to pass every time.
     * @param assertion the assertion that should pass
     */
    public static void awaitAssertionMaintained(ThrowingRunnable assertion) {
        Awaitility
                .await()
                .during(3, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(assertion);
    }
}
