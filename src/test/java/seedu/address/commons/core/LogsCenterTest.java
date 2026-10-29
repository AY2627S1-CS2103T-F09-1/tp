package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {

    @TempDir
    public Path testFolder;

    @Test
    public void toFileHandlerPattern_pathWithoutPlaceholder_returnsPathUnchanged() {
        Path logFile = Paths.get("logs", "tutortrack.log");

        assertEquals(logFile.toString(), LogsCenter.toFileHandlerPattern(logFile));
    }

    @Test
    public void toFileHandlerPattern_folderWithPercentSign_writesLogFileInThatFolder() throws IOException {
        Path homeFolder = Files.createDirectory(testFolder.resolve("100%tutors"));
        Path logFile = homeFolder.resolve("tutortrack.log");

        FileHandler fileHandler = new FileHandler(LogsCenter.toFileHandlerPattern(logFile));
        fileHandler.close();

        assertTrue(Files.exists(logFile));
    }

    @Test
    public void toFileHandlerPattern_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> LogsCenter.toFileHandlerPattern(null));
    }

    @Test
    public void addFileHandler_writableFolder_addsFileHandler() {
        Logger target = newIsolatedLogger();
        Path logFile = testFolder.resolve("tutortrack.log");

        LogsCenter.addFileHandler(target, logFile);

        assertTrue(Arrays.stream(target.getHandlers()).anyMatch(FileHandler.class::isInstance));
        // a rotating FileHandler appends the generation number to a pattern without one
        assertTrue(Files.exists(testFolder.resolve("tutortrack.log.0")));
        closeHandlers(target);
    }

    @Test
    public void addFileHandler_unwritableLogFile_warnsWithoutThrowing() {
        Logger target = newIsolatedLogger();
        List<LogRecord> records = new ArrayList<>();
        target.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                records.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });
        // FileHandler cannot create a file in a folder that does not exist, which fails like a read-only folder
        Path logFile = testFolder.resolve("missing").resolve("tutortrack.log");

        LogsCenter.addFileHandler(target, logFile);

        assertTrue(Arrays.stream(target.getHandlers()).noneMatch(FileHandler.class::isInstance));
        assertEquals(1, records.size());
        assertEquals(Level.WARNING, records.get(0).getLevel());
        assertFalse(Files.exists(logFile.getParent()));
    }

    @Test
    public void addFileHandler_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> LogsCenter.addFileHandler(null, testFolder.resolve("a.log")));
        assertThrows(NullPointerException.class, () -> LogsCenter.addFileHandler(newIsolatedLogger(), null));
    }

    private static Logger newIsolatedLogger() {
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        return logger;
    }

    private static void closeHandlers(Logger logger) {
        Arrays.stream(logger.getHandlers()).forEach(Handler::close);
    }
}
