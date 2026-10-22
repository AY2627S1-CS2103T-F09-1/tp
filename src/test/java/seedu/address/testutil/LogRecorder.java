package seedu.address.testutil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;

/**
 * Records the messages that a class logs, for use in tests.
 */
public class LogRecorder {

    private LogRecorder() {} // prevents instantiation

    /**
     * Represents an action under test that may throw a checked exception of type {@code E}.
     */
    @FunctionalInterface
    public interface Action<E extends Exception> {
        void run() throws E;
    }

    /**
     * Returns the messages that {@code loggingClass} logs at any level while {@code action} runs.
     * Any exception that {@code action} throws is rethrown after the recording stops.
     */
    public static <E extends Exception> List<String> captureMessages(Class<?> loggingClass, Action<E> action)
            throws E {
        List<String> messages = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord logRecord) {
                messages.add(logRecord.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };

        Logger logger = LogsCenter.getLogger(loggingClass);
        Level originalLevel = logger.getLevel();
        logger.setLevel(Level.ALL);
        logger.addHandler(handler);
        try {
            action.run();
        } finally {
            logger.removeHandler(handler);
            logger.setLevel(originalLevel);
        }
        return messages;
    }
}
