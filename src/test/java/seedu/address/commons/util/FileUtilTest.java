package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class FileUtilTest {

    @TempDir
    private Path temporaryDirectory;

    @Test
    public void writeToFile_existingFile_replacesContents() throws IOException {
        Path dataFile = temporaryDirectory.resolve("addressbook.json");
        Files.writeString(dataFile, "old data");

        FileUtil.writeToFile(dataFile, "new data");

        assertEquals("new data", Files.readString(dataFile));
    }

    @Test
    public void writeToFile_replacementFails_preservesExistingPath() throws IOException {
        Path dataPath = temporaryDirectory.resolve("addressbook.json");
        Files.createDirectory(dataPath);
        Path markerFile = dataPath.resolve("existing-data");
        Files.writeString(markerFile, "old data");

        assertThrows(IOException.class, () -> FileUtil.writeToFile(dataPath, "new data"));

        assertTrue(Files.isDirectory(dataPath));
        assertEquals("old data", Files.readString(markerFile));
        try (Stream<Path> files = Files.list(temporaryDirectory)) {
            assertFalse(files.anyMatch(path -> path.getFileName().toString().endsWith(".tmp")));
        }
    }
}
