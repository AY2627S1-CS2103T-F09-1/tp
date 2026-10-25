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
        assertEquals("https://ay2627s1-cs2103t-f09-1.github.io/tp/UserGuide.html", HelpWindow.USERGUIDE_URL);
    }

    @Test
    public void rosterTheme_interactionStatesAreScopedToRosterList() throws IOException {
        String theme = readResource("/view/TutorTrackTheme.css");

        assertFalse(theme.contains(".student-roster-list .list-cell:filled:hover #cardPane"));
        assertTrue(theme.contains(".student-roster-list .list-cell:filled:profile-selected #cardPane"));
        assertTrue(theme.contains(".student-roster-list .list-cell:filled:hover"));
        assertTrue(theme.contains(".student-roster-list .list-cell:filled:selected"));
        assertTrue(theme.contains(".student-roster-list .list-cell:filled:focused"));
        assertFalse(theme.contains("#cardPane" + ":focused"));
        assertFalse(theme.contains(".student-roster-list:focused .list-cell:filled:focused #cardPane"));
        assertFalse(theme.contains(
                ".student-roster-list:focused .list-cell:filled:profile-selected:focused #cardPane"));
        assertFalse(theme.contains("\n.list-cell:filled:hover"));
        assertFalse(theme.contains("\n.list-cell:filled:selected"));
    }

    @Test
    public void rosterTheme_noteCountUsesIndexBadgeBackground() throws IOException {
        String theme = readResource("/view/TutorTrackTheme.css");
        String indexBadgeRule = getRule(theme, ".roster-index-badge");
        String noteCountRule = getRule(theme, ".roster-note-count");

        assertTrue(indexBadgeRule.contains("-fx-background-color: -app-surface-muted;"));
        assertTrue(noteCountRule.contains("-fx-background-color: -app-surface-muted;"));
        assertFalse(noteCountRule.contains("-app-accent-soft"));
    }

    @Test
    public void nonInteractiveResultDisplay_doesNotAdvertiseFocusState() throws IOException {
        String theme = readResource("/view/TutorTrackTheme.css");
        String resultDisplay = readResource("/view/ResultDisplay.fxml");

        assertFalse(theme.contains(".text-area:focused"));
        assertTrue(theme.contains(".result-display:focused"));
        assertTrue(resultDisplay.contains("focusTraversable=\"false\""));
    }

    private String readResource(String resourcePath) throws IOException {
        URL resource = getClass().getResource(resourcePath);
        assertNotNull(resource, resourcePath + " does not exist.");

        try (InputStream inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String getRule(String stylesheet, String selector) {
        int ruleStart = stylesheet.indexOf(selector + " {");
        assertTrue(ruleStart >= 0, selector + " does not exist.");
        int ruleEnd = stylesheet.indexOf('}', ruleStart);
        assertTrue(ruleEnd >= 0, selector + " is incomplete.");
        return stylesheet.substring(ruleStart, ruleEnd);
    }
}
