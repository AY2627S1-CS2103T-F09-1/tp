package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class HomeFolderTest {

    private static final Path WORKING_DIRECTORY = Paths.get("");

    @TempDir
    public Path testFolder;

    @Test
    public void findHomeFolder_jarFile_returnsFolderContainingJar() throws IOException {
        Path jarFile = Files.createFile(testFolder.resolve("tutortrack.jar"));

        assertEquals(testFolder, HomeFolder.findHomeFolder(jarFile.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_jarFileInFolderWithSpace_returnsDecodedFolder() throws IOException {
        Path homeFolder = Files.createDirectory(testFolder.resolve("Tutor Track 100%"));
        Path jarFile = Files.createFile(homeFolder.resolve("tutortrack.jar"));

        assertEquals(homeFolder, HomeFolder.findHomeFolder(jarFile.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_upperCaseJarExtension_returnsFolderContainingJar() throws IOException {
        Path jarFile = Files.createFile(testFolder.resolve("TUTORTRACK.JAR"));

        assertEquals(testFolder, HomeFolder.findHomeFolder(jarFile.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_classesFolder_returnsWorkingDirectory() throws IOException {
        Path classesFolder = Files.createDirectory(testFolder.resolve("classes"));

        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(classesFolder.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_folderNamedLikeJar_returnsWorkingDirectory() throws IOException {
        Path jarNamedFolder = Files.createDirectory(testFolder.resolve("tutortrack.jar"));

        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(jarNamedFolder.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_fileSystemRoot_returnsWorkingDirectory() throws IOException {
        Path root = testFolder.toAbsolutePath().getRoot();

        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(root.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_missingJarFile_returnsWorkingDirectory() throws IOException {
        Path missingJarFile = testFolder.resolve("missing.jar");

        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(missingJarFile.toUri().toURL()));
    }

    @Test
    public void findHomeFolder_nullLocation_returnsWorkingDirectory() {
        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(null));
    }

    @Test
    public void findHomeFolder_nonFileLocation_returnsWorkingDirectory() throws IOException {
        URL remoteJar = URI.create("https://example.com/tutortrack.jar").toURL();

        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(remoteJar));
    }

    @Test
    // Only the deprecated constructor can build a URL that is not a valid URI.
    @SuppressWarnings("deprecation")
    public void findHomeFolder_unparsableLocation_returnsWorkingDirectory() throws IOException {
        URL unparsableLocation = new URL("file:/tutor track/tutortrack.jar");

        assertEquals(WORKING_DIRECTORY, HomeFolder.findHomeFolder(unparsableLocation));
    }

    @Test
    public void resolve_notRunningFromJar_returnsPathInWorkingDirectory() {
        assertEquals(Paths.get("data", "tutortrack.json"), HomeFolder.resolve("data", "tutortrack.json"));
    }

    @Test
    public void toDisplayPath_notRunningFromJar_returnsPathRelativeToWorkingDirectory() {
        Path dataFile = HomeFolder.resolve("data", "tutortrack.json");

        assertEquals(Paths.get(".", "data", "tutortrack.json"), HomeFolder.toDisplayPath(dataFile));
    }

    @Test
    public void resolve_nullFirstName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> HomeFolder.resolve(null));
    }

    @Test
    public void toDisplayPath_pathInsideHomeFolder_returnsPathRelativeToHomeFolder() {
        Path dataFile = testFolder.resolve("data").resolve("tutortrack.json");

        assertEquals(Paths.get(".", "data", "tutortrack.json"),
                HomeFolder.toDisplayPath(testFolder, dataFile));
    }

    @Test
    public void toDisplayPath_relativePathInWorkingDirectory_returnsPathPrefixedWithCurrentFolder() {
        Path dataFile = Paths.get("data", "tutortrack.json");

        assertEquals(Paths.get(".", "data", "tutortrack.json"),
                HomeFolder.toDisplayPath(WORKING_DIRECTORY, dataFile));
    }

    @Test
    public void toDisplayPath_pathOutsideHomeFolder_returnsPathUnchanged() {
        Path homeFolder = testFolder.resolve("home");
        Path otherFile = testFolder.resolve("other").resolve("tutortrack.json");

        assertEquals(otherFile, HomeFolder.toDisplayPath(homeFolder, otherFile));
    }

    @Test
    public void toDisplayPath_siblingFolderWithSharedPrefix_returnsPathUnchanged() {
        Path homeFolder = testFolder.resolve("home");
        Path siblingFile = testFolder.resolve("home-backup").resolve("tutortrack.json");

        assertEquals(siblingFile, HomeFolder.toDisplayPath(homeFolder, siblingFile));
    }
}
