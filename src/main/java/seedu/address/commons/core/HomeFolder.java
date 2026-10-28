package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Locale;

/**
 * Locates TutorTrack's home folder, the folder containing the TutorTrack JAR.
 * TutorTrack keeps its files in the home folder so that they stay with the JAR wherever it is launched from.
 * When TutorTrack is not running from a JAR, such as during development and tests, the working directory is used.
 */
public class HomeFolder {

    private static final String JAR_EXTENSION = ".jar";
    private static final String CURRENT_FOLDER = ".";
    private static final Path WORKING_DIRECTORY = Paths.get("");
    private static final Path HOME_FOLDER = findHomeFolder(findCodeLocation());

    private HomeFolder() {}

    /**
     * Returns the path of a file or folder inside the home folder.
     *
     * @param first The first name of the path relative to the home folder.
     * @param more The remaining names of the path, if any.
     */
    public static Path resolve(String first, String... more) {
        requireNonNull(first);
        return HOME_FOLDER.resolve(Paths.get(first, more));
    }

    /**
     * Returns {@code path} as it should be shown to the user: relative to the home folder when it is inside it.
     */
    public static Path toDisplayPath(Path path) {
        return toDisplayPath(HOME_FOLDER, path);
    }

    /**
     * Returns {@code path} relative to {@code homeFolder}, prefixed with {@code ./}, when it is inside
     * {@code homeFolder}, or {@code path} unchanged otherwise.
     */
    static Path toDisplayPath(Path homeFolder, Path path) {
        requireNonNull(homeFolder);
        requireNonNull(path);
        Path absoluteHomeFolder = homeFolder.toAbsolutePath().normalize();
        Path absolutePath = path.toAbsolutePath().normalize();
        if (!absolutePath.startsWith(absoluteHomeFolder)) {
            return path;
        }
        return Paths.get(CURRENT_FOLDER).resolve(absoluteHomeFolder.relativize(absolutePath));
    }

    /**
     * Returns the home folder for code loaded from {@code codeLocation}: the folder containing it when it is a JAR
     * file, or the working directory when it is not a JAR file or cannot be converted to a local path.
     *
     * @param codeLocation The location the code was loaded from, or null if it is unknown.
     */
    static Path findHomeFolder(URL codeLocation) {
        if (codeLocation == null) {
            return WORKING_DIRECTORY;
        }
        try {
            Path codePath = Paths.get(codeLocation.toURI());
            return isJarFile(codePath) ? codePath.getParent() : WORKING_DIRECTORY;
        } catch (URISyntaxException | IllegalArgumentException | FileSystemNotFoundException e) {
            // Logging is unavailable here because LogsCenter needs the home folder to set up its log file.
            return WORKING_DIRECTORY;
        }
    }

    private static boolean isJarFile(Path path) {
        Path fileName = path.getFileName();
        boolean hasJarExtension = fileName != null
                && fileName.toString().toLowerCase(Locale.ROOT).endsWith(JAR_EXTENSION);
        return hasJarExtension && Files.isRegularFile(path);
    }

    /**
     * Returns the location this class was loaded from, or null if the JVM does not report one.
     */
    private static URL findCodeLocation() {
        CodeSource codeSource = HomeFolder.class.getProtectionDomain().getCodeSource();
        return codeSource == null ? null : codeSource.getLocation();
    }
}
