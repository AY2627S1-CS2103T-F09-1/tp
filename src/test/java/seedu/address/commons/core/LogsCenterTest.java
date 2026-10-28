package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;

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
}
