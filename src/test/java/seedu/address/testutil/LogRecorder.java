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
     * Returns the messages that {@code loggingClass} logs at any level while {@code action} runs.
     */
    public static List<String> captureMessages(Class<?> loggingClass, Runnable action) {
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
