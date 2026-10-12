package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

public class UiThemeTest {

    @Test
    public void tutorTrackTheme_resourceDefinesSharedPaletteAndFocusState() throws IOException {
        String theme = readResource("/view/TutorTrackTheme.css");

        assertTrue(theme.contains("-app-accent: #0f766e"));
        assertTrue(theme.contains("-app-surface: #ffffff"));
        assertTrue(theme.contains("-app-focus-ring: #17332f"));
        assertTrue(theme.contains(".button:focused"));
        assertTrue(theme.contains(".error"));
    }

    @Test
    public void mainWindowAndHelpWindow_useTutorTrackBranding() throws IOException {
        String mainWindow = readResource("/view/MainWindow.fxml");
        String helpWindow = readResource("/view/HelpWindow.fxml");

        assertTrue(mainWindow.contains("title=\"TutorTrack\""));
        assertTrue(mainWindow.contains("@TutorTrackTheme.css"));
        assertTrue(helpWindow.contains("title=\"TutorTrack Help\""));
        assertTrue(helpWindow.contains("@TutorTrackTheme.css"));
        assertFalse(mainWindow.contains("DarkTheme.css"));
        assertEquals("https://nus-cs2103-ay2627-s1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }

    private String readResource(String resourcePath) throws IOException {
        URL resource = getClass().getResource(resourcePath);
        assertNotNull(resource, resourcePath + " does not exist.");

        try (InputStream inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
