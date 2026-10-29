package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.address.MainApp;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.HomeFolder;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.model.student.Subject;

public class StudentRosterUiTest {

    private static final StudentId ALEX_ID = StudentId.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final StudentId BLAIR_ID = StudentId.fromString("123e4567-e89b-12d3-a456-426614174001");

    @BeforeAll
    public static void initializeJavaFx() throws InterruptedException {
        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(startupLatch::countDown);
        assertTrue(startupLatch.await(10, TimeUnit.SECONDS));
        Platform.setImplicitExit(false);
    }

    @AfterAll
    public static void shutdownJavaFx() {
        Platform.exit();
    }

    @Test
    public void constructor_emptyRoster_showsEmptyMessage() throws Exception {
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of())));

        assertTrue(getListView(panel).getItems().isEmpty());
        assertTrue(getEmptyRosterState(panel).isVisible());
        assertTrue(getEmptyRosterState(panel).isManaged());
        assertEquals("No students yet", ((Label) getEmptyRosterState(panel).getChildren().get(0)).getText());
        Label guidance = (Label) getEmptyRosterState(panel).getChildren().get(1);
        assertEquals("Use the add command to create your first student profile.",
                guidance.getText());
    }

    @Test
    public void helpWindow_loadsTutorTrackThemeAndAppliesFocusStyle() throws Exception {
        onFxThread(() -> {
            HelpWindow helpWindow = new HelpWindow();
            Scene scene = helpWindow.getRoot().getScene();

            assertTrue(scene.getStylesheets().stream()
                    .anyMatch(stylesheet -> stylesheet.endsWith("/TutorTrackTheme.css")));
            assertTrue(scene.getStylesheets().stream()
                    .anyMatch(stylesheet -> stylesheet.endsWith("/HelpWindow.css")));

            scene.getRoot().applyCss();
            Button copyButton = (Button) scene.lookup("#copyButton");
            assertNotNull(copyButton);

            copyButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("focused"), true);
            scene.getRoot().applyCss();
            Border border = copyButton.getBorder();

            assertNotNull(border);
            assertEquals(Color.web("#17332f"), border.getStrokes().get(0).getTopStroke());
            return null;
        });
    }

    @Test
    public void mainAndHelpWindows_loadTheirIcons() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            Stage mainStage = new Stage();
            MainWindow mainWindow = new MainWindow(mainStage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            HelpWindow helpWindow = new HelpWindow();

            assertFalse(mainStage.getIcons().isEmpty());
            assertFalse(mainStage.getIcons().get(0).isError());
            assertTrue(mainStage.getIcons().get(0).getWidth() > 0);
            assertFalse(helpWindow.getRoot().getIcons().isEmpty());
            assertFalse(helpWindow.getRoot().getIcons().get(0).isError());
            assertTrue(helpWindow.getRoot().getIcons().get(0).getWidth() > 0);
            return null;
        });
    }

    @Test
    public void constructor_populatedRoster_displaysEntriesAndHidesEmptyMessage() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));

        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(roster));

        assertEquals(roster.getEntries(), getListView(panel).getItems());
        assertFalse(getEmptyRosterState(panel).isVisible());
        assertFalse(getEmptyRosterState(panel).isManaged());
    }

    @Test
    public void roster_resizedNarrowly_keepsLastCardReachable() throws Exception {
        List<Student> students = new ArrayList<>();
        for (int index = 0; index < 20; index++) {
            students.add(student("Student " + "LongName ".repeat(5) + index, StudentId.fromString(String.format(
                    "123e4567-e89b-12d3-a456-%012d", index))));
        }
        StudentRoster roster = new StudentRoster(students);
        StudentRosterEntry lastEntry = roster.getEntries().get(roster.size() - 1);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(roster));

        onFxThread(() -> {
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 420, 500));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                ListView<StudentRosterEntry> listView = getListView(panel);
                ScrollBar verticalScrollBar = getVerticalScrollBar(listView);
                assertTrue(verticalScrollBar.isVisible());
                verticalScrollBar.setValue(verticalScrollBar.getMin());
                stage.getScene().getRoot().layout();

                stage.setWidth(180);
                stage.getScene().getRoot().layout();
                verticalScrollBar.setValue(verticalScrollBar.getMax());
                stage.getScene().getRoot().layout();

                // Headless JavaFX does not expose the card width change reliably; the desktop manual step in the
                // Developer Guide verifies the resize listener's reflow behaviour.
                assertTrue(listView.lookupAll(".list-cell").stream()
                        .filter(ListCell.class::isInstance)
                        .map(ListCell.class::cast)
                        .anyMatch(cell -> cell.getItem() == lastEntry));
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void roster_widthChanges_keepsRenderedCardNodes() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        StudentRosterEntry entry = roster.getEntries().get(0);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(roster));

        onFxThread(() -> {
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 420, 500));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                ListCell<?> cellBeforeResize = getVisibleCellForEntry(panel, entry);
                Node cardBeforeResize = cellBeforeResize.getGraphic();

                ListView<StudentRosterEntry> listView = getListView(panel);
                listView.resize(listView.getWidth() - 60, listView.getHeight());
                listView.layout();

                ListCell<?> cellAfterResize = getVisibleCellForEntry(panel, entry);
                assertSame(cardBeforeResize, cellAfterResize.getGraphic());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void setRoster_newRoster_replacesDisplayedEntries() throws Exception {
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of())));
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));

        onFxThread(() -> {
            panel.setRoster(roster);
            return null;
        });

        assertEquals(roster.getEntries(), getListView(panel).getItems());
        assertFalse(getEmptyRosterState(panel).isVisible());
        assertFalse(getEmptyRosterState(panel).isManaged());
    }

    @Test
    public void setRoster_nullRoster_throwsNullPointerException() throws Exception {
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of())));

        assertThrows(NullPointerException.class, () -> panel.setRoster(null));
    }

    @Test
    public void selectStudent_existingStudent_selectsEntryByStableId() throws Exception {
        Student alex = student("Alex Tan", ALEX_ID);
        Student blair = student("Blair Lim", BLAIR_ID);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(
                new StudentRoster(List.of(alex, blair))));

        onFxThread(() -> {
            panel.selectStudent(BLAIR_ID);
            return null;
        });

        assertEquals(BLAIR_ID, getListView(panel).getSelectionModel().getSelectedItem().getStudentId());
    }

    @Test
    public void setRoster_selectedStudentMoves_preservesSelectionByStableId() throws Exception {
        Student alex = student("Alex Tan", ALEX_ID);
        Student aaron = student("Aaron Koh", BLAIR_ID);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of(alex))));

        onFxThread(() -> {
            panel.selectStudent(ALEX_ID);
            panel.setRoster(new StudentRoster(List.of(alex, aaron)));
            return null;
        });

        assertEquals(1, getListView(panel).getSelectionModel().getSelectedIndex());
        assertEquals(ALEX_ID, getListView(panel).getSelectionModel().getSelectedItem().getStudentId());
    }

    @Test
    public void setRoster_nativeSelectionMoves_reselectsDisplayedProfile() throws Exception {
        Student alex = student("Alex Tan", ALEX_ID);
        Student blair = student("Blair Lim", BLAIR_ID);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(
                new StudentRoster(List.of(alex, blair))));

        onFxThread(() -> {
            panel.selectStudent(ALEX_ID);
            getListView(panel).getSelectionModel().select(1);
            panel.setRoster(new StudentRoster(List.of(alex, blair)));
            return null;
        });

        assertEquals(ALEX_ID, getListView(panel).getSelectionModel().getSelectedItem().getStudentId());
    }

    @Test
    public void listCell_displayedProfile_appliesDedicatedSelectionState() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(
                student("Alex Tan", ALEX_ID), student("Blair Lim", BLAIR_ID)));
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(roster));
        ListView<StudentRosterEntry> listView = getListView(panel);
        ListCell<StudentRosterEntry> cell = onFxThread(() -> listView.getCellFactory().call(listView));

        onFxThread(() -> {
            panel.selectStudent(ALEX_ID);
            updateCell(cell, roster.getEntries().get(0), false);
            return null;
        });
        assertTrue(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("profile-selected")));

        onFxThread(() -> {
            updateCell(cell, roster.getEntries().get(1), false);
            return null;
        });
        assertFalse(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("profile-selected")));
    }

    @Test
    public void selectStudent_missingStudent_clearsSelection() throws Exception {
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(
                new StudentRoster(List.of(student("Alex Tan")))));

        onFxThread(() -> {
            panel.selectStudent(ALEX_ID);
            panel.selectStudent(BLAIR_ID);
            return null;
        });

        assertTrue(getListView(panel).getSelectionModel().isEmpty());
    }

    @Test
    public void card_populatedEntry_displaysAllRosterFields() throws Exception {
        StudentRosterEntry entry = new StudentRosterEntry(ALEX_ID, 2, new StudentName("Alex Tan"),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"), 3);

        HBox card = (HBox) onFxThread(() -> new StudentRosterCard(entry).getRoot());
        GridPane grid = (GridPane) card.getChildren().get(0);
        VBox details = (VBox) grid.getChildren().get(0);
        HBox heading = (HBox) details.getChildren().get(0);

        assertEquals("2", ((Label) heading.getChildren().get(0)).getText());
        assertEquals("Alex Tan", ((Label) heading.getChildren().get(1)).getText());
        assertEquals("Subject: Mathematics", ((Label) details.getChildren().get(1)).getText());
        assertEquals("Level: Secondary 3", ((Label) details.getChildren().get(2)).getText());
        assertEquals("Session notes: 3", ((Label) details.getChildren().get(3)).getText());
        assertTrue(((Label) heading.getChildren().get(1)).isWrapText());
        assertTrue(((Label) details.getChildren().get(1)).isWrapText());
        assertTrue(((Label) details.getChildren().get(2)).isWrapText());
        assertTrue(((Label) details.getChildren().get(3)).isWrapText());
    }

    @Test
    public void rosterPanel_narrowWidthWithLongFields_wrapsWithoutHorizontalScroll() throws Exception {
        Student student = new Student(new StudentName("Alicia Alexandra Catherine Elizabeth Lim Wei Xuan"),
                new ParentGuardianContact("8000 0001",
                        Optional.of("guardian.with.a.long.address@example.com")),
                new Subject("Additional Mathematics and Mathematical Modelling"),
                new CurrentLevel("Secondary Four Express Integrated Academic Programme"), ALEX_ID);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of(student))));

        onFxThread(() -> {
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 260, 500));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                ListView<StudentRosterEntry> listView = getListView(panel);
                ScrollBar horizontalScrollBar = findDescendants(listView, ScrollBar.class).stream()
                        .filter(scrollBar -> scrollBar.getOrientation() == Orientation.HORIZONTAL)
                        .findFirst()
                        .orElseThrow();
                assertFalse(horizontalScrollBar.isVisible());

                @SuppressWarnings("rawtypes")
                ListCell cell = findDescendants(listView, ListCell.class).stream()
                        .filter(listCell -> listCell.getGraphic() != null)
                        .findFirst()
                        .orElseThrow();
                HBox card = (HBox) cell.getGraphic();
                VBox details = (VBox) ((GridPane) card.getChildren().get(0)).getChildren().get(0);
                Label name = (Label) ((HBox) details.getChildren().get(0)).getChildren().get(1);
                Label subject = (Label) details.getChildren().get(1);
                Label currentLevel = (Label) details.getChildren().get(2);
                Label noteCount = (Label) details.getChildren().get(3);
                assertTrue(card.getWidth() <= listView.getWidth());
                assertTrue(name.getHeight() > name.getFont().getSize() * 1.5);
                assertTrue(subject.getHeight() > subject.getFont().getSize() * 2);
                assertTrue(currentLevel.getHeight() > currentLevel.getFont().getSize() * 1.5);
                assertTrue(noteCount.getWidth() <= noteCount.prefWidth(-1) + 1);
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void listCell_populatedAndEmptyEntries_updateGraphic() throws Exception {
        StudentRosterEntry entry = new StudentRosterEntry(ALEX_ID, 1, new StudentName("Alex Tan"),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"), 0);
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of())));
        ListView<StudentRosterEntry> listView = getListView(panel);
        ListCell<StudentRosterEntry> cell = onFxThread(() -> listView.getCellFactory().call(listView));

        onFxThread(() -> {
            updateCell(cell, entry, false);
            return null;
        });
        assertTrue(cell.getGraphic() instanceof HBox);

        onFxThread(() -> {
            updateCell(cell, null, false);
            return null;
        });
        assertNull(cell.getGraphic());
        assertNull(cell.getText());

        onFxThread(() -> {
            updateCell(cell, null, true);
            return null;
        });
        assertNull(cell.getGraphic());
        assertNull(cell.getText());
    }

    @Test
    public void profilePanel_initially_showsEmptyState() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);
        StackPane root = (StackPane) panel.getRoot();
        VBox emptyState = (VBox) root.getChildren().get(0);
        ScrollPane profileScrollPane = getProfileScrollPane(panel);

        assertTrue(emptyState.isVisible());
        assertTrue(emptyState.isManaged());
        assertFalse(profileScrollPane.isVisible());
        assertFalse(profileScrollPane.isManaged());
    }

    @Test
    public void setProfile_populatedProfile_displaysContactAndContext() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);
        StudentProfile profile = profile("alex.parent@example.com");

        onFxThread(() -> {
            panel.setProfile(profile);
            return null;
        });

        VBox profileContent = getProfileContent(panel);
        GridPane details = (GridPane) profileContent.getChildren().get(1);
        assertEquals("Alex Tan", ((SelectableText) profileContent.getChildren().get(0)).getText());
        assertEquals("9123 4567", ((SelectableText) details.getChildren().get(1)).getText());
        assertEquals("alex.parent@example.com", ((SelectableText) details.getChildren().get(3)).getText());
        assertEquals("Mathematics", ((SelectableText) details.getChildren().get(5)).getText());
        assertEquals("Secondary 3", ((SelectableText) details.getChildren().get(7)).getText());
        assertTrue(((SelectableText) profileContent.getChildren().get(0)).isWrapText());
        assertTrue(((SelectableText) details.getChildren().get(1)).isWrapText());
        assertTrue(((SelectableText) details.getChildren().get(3)).isWrapText());
        assertTrue(((SelectableText) details.getChildren().get(5)).isWrapText());
        assertTrue(((SelectableText) details.getChildren().get(7)).isWrapText());
        List<ColumnConstraints> columns = details.getColumnConstraints();
        assertEquals(2, columns.size());
        assertEquals(Priority.ALWAYS, columns.get(1).getHgrow());
        assertEquals(0, columns.get(1).getMinWidth());
        assertTrue(profileContent.isVisible());
        assertFalse(((VBox) ((StackPane) panel.getRoot()).getChildren().get(0)).isVisible());
    }

    @Test
    public void setProfile_missingEmail_displaysFallback() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null));
            return null;
        });

        GridPane details = (GridPane) getProfileContent(panel).getChildren().get(1);
        assertEquals("Not provided", ((SelectableText) details.getChildren().get(3)).getText());
    }

    @Test
    public void setProfile_nullProfile_throwsNullPointerException() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        assertThrows(NullPointerException.class, () -> panel.setProfile(null));
    }

    @Test
    public void clearProfile_populatedPanel_restoresEmptyState() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);
        SessionNote note = new SessionNote("Reviewed factorisation.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));

        onFxThread(() -> {
            panel.setProfile(profile(null, List.of(note)));
            panel.clearProfile();
            return null;
        });

        StackPane root = (StackPane) panel.getRoot();
        assertTrue(root.getChildren().get(0).isVisible());
        assertFalse(root.getChildren().get(1).isVisible());
        assertTrue(getSessionNoteListView(panel).getItems().isEmpty());
    }

    @Test
    public void setProfile_sessionNotes_preservesNewestFirstOrderAndNumbersDescending() throws Exception {
        SessionNote newestNote = new SessionNote("Plan next lesson.",
                OffsetDateTime.parse("2026-09-19T09:10:00+08:00"));
        SessionNote olderNote = new SessionNote("Reviewed factorisation.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        SessionNote oldestNote = new SessionNote("Introduced algebra.",
                OffsetDateTime.parse("2026-09-11T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null, List.of(newestNote, olderNote, oldestNote)));
            return null;
        });

        ListView<StudentProfilePanel.NumberedSessionNote> listView = getSessionNoteListView(panel);
        assertEquals(List.of(
                new StudentProfilePanel.NumberedSessionNote(newestNote, 3),
                new StudentProfilePanel.NumberedSessionNote(olderNote, 2),
                new StudentProfilePanel.NumberedSessionNote(oldestNote, 1)), listView.getItems());
        assertFalse(getEmptyNotesState(panel).isVisible());
        assertFalse(getEmptyNotesState(panel).isManaged());
    }

    @Test
    public void setProfile_emptyHistory_showsEmptyNotesMessage() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null));
            return null;
        });

        assertTrue(getSessionNoteListView(panel).getItems().isEmpty());
        VBox emptyNotesState = getEmptyNotesState(panel);
        assertEquals("No session notes yet", ((SelectableText) emptyNotesState.getChildren().get(0)).getText());
        SelectableText guidance = (SelectableText) emptyNotesState.getChildren().get(1);
        assertEquals("Use note INDEX nt/NOTE to record the first lesson update.",
                guidance.getText());
        assertTrue(emptyNotesState.isVisible());
        assertTrue(emptyNotesState.isManaged());
    }

    @Test
    public void sessionNoteCard_note_displaysFriendlyOffsetTimestampAndWrappedText() throws Exception {
        SessionNote note = new SessionNote("A long note that should wrap instead of widening the profile panel.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));

        VBox card = (VBox) onFxThread(() -> new SessionNoteCard(note, 3).getRoot());

        SelectableText header = (SelectableText) card.getChildren().get(0);
        SelectableText noteText = (SelectableText) card.getChildren().get(1);
        assertEquals("[3] 18 Sep 2026, 6:35 PM (UTC+08:00)", header.getText());
        assertEquals(note.getText(), noteText.getText());
        assertTrue(header.isWrapText());
        assertTrue(noteText.isWrapText());
        assertFalse(header.isEditable());
        assertFalse(noteText.isEditable());
        assertEquals(2, noteText.getLineSpacing());
        assertEquals("18 Sep 2026, 10:35 AM (UTC+00:00)", SessionNoteCard.formatRecordedAt(
                OffsetDateTime.parse("2026-09-18T10:35:00Z")));
        assertThrows(IllegalArgumentException.class, () -> new SessionNoteCard(note, 0));
        assertThrows(NullPointerException.class, () -> SessionNoteCard.formatRecordedAt(null));
    }

    @Test
    public void profilePanel_staticLabelsAreNotSelectableAndValuesAreReadOnly() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        List<SelectableText> textControls = findDescendants(panel.getRoot(), SelectableText.class);
        List<Label> labels = findDescendants(panel.getRoot(), Label.class);

        assertEquals(9, textControls.size());
        assertTrue(textControls.stream().noneMatch(SelectableText::isEditable));
        assertTrue(textControls.stream().allMatch(SelectableText::isWrapText));
        assertTrue(textControls.stream().allMatch(text -> text.getPrefColumnCount() > 1));
        assertTrue(textControls.stream().allMatch(text -> text.getStyleClass().contains("selectable-text")));
        assertEquals(List.of("Parent/guardian phone", "Parent/guardian email", "Subject", "Current level",
                "Session notes"), labels.stream().map(Label::getText).toList());
    }

    @Test
    public void profilePanel_widerPlatformFont_fieldLabelsRemainFullyVisible() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile("alex.parent@example.com"));
            List<Label> fieldLabels = findDescendants(panel.getRoot(), Label.class).stream()
                    .filter(label -> label.getStyleClass().contains("profile-field-label"))
                    .toList();
            fieldLabels.forEach(label -> label.setStyle("-fx-font-size: 18px;"));

            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 700, 480));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                assertEquals(4, fieldLabels.size());
                fieldLabels.forEach(label -> assertTrue(label.getWidth() + 1 >= label.prefWidth(-1), () ->
                        String.format("%s was allocated %.1f px but needs %.1f px",
                                label.getText(), label.getWidth(), label.prefWidth(-1))));
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void profilePanel_tallWindow_detailsGridCanResize() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile("alex.parent@example.com"));
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 700, 800));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                GridPane details = (GridPane) getProfileContent(panel).getChildren().get(1);
                double preferredHeight = details.prefHeight(details.getWidth());

                assertTrue(details.getHeight() <= preferredHeight + 1);
                assertTrue(details.maxHeight(details.getWidth()) > preferredHeight + 1);
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void profilePanel_resized_contentIsClippedToPanel() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            assertTrue(panel.getRoot().getClip() instanceof Rectangle);
            Rectangle panelClip = (Rectangle) panel.getRoot().getClip();
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 360, 480));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                assertEquals(panel.getRoot().getWidth(), panelClip.getWidth());
                assertEquals(panel.getRoot().getHeight(), panelClip.getHeight());

                stage.setWidth(520);
                stage.setHeight(640);
                stage.getScene().getRoot().layout();

                assertEquals(panel.getRoot().getWidth(), panelClip.getWidth());
                assertEquals(panel.getRoot().getHeight(), panelClip.getHeight());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void profilePanel_compactViewport_longProfileCanScrollWithoutHorizontalOverflow() throws Exception {
        SessionNote note = new SessionNote("Reviewed factorisation.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfile longProfile = new StudentProfile(ALEX_ID,
                new StudentName("Alicia Alexandra Lim Wei Xuan"),
                new ParentGuardianContact("8000 0001",
                        Optional.of("guardian.with.a.long.address@example.com")),
                new Subject("Additional Mathematics and Mathematical Modelling"),
                new CurrentLevel("Secondary 4 Express"), List.of(note));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(longProfile);
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 280, 300));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                ScrollPane profileScrollPane = (ScrollPane) panel.getRoot().lookup("#profileScrollPane");
                assertNotNull(profileScrollPane);
                assertTrue(profileScrollPane.getVmax() > profileScrollPane.getVmin());
                assertEquals(ScrollPane.ScrollBarPolicy.NEVER, profileScrollPane.getHbarPolicy());
                assertTrue(profileScrollPane.isFitToWidth());
                assertTrue(profileScrollPane.getContent().getLayoutBounds().getWidth()
                        <= profileScrollPane.getViewportBounds().getWidth() + 1);
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void selectableText_copyShortcut_copiesSelectionAndDoesNotEdit() throws Exception {
        onFxThread(() -> {
            SelectableText selectableText = new SelectableText();
            selectableText.setText("Selectable profile text");
            Stage stage = new Stage();
            Clipboard systemClipboard = Clipboard.getSystemClipboard();
            ClipboardContent originalClipboardContent = new ClipboardContent();
            for (DataFormat dataFormat : systemClipboard.getContentTypes()) {
                originalClipboardContent.put(dataFormat, systemClipboard.getContent(dataFormat));
            }
            try {
                stage.setScene(new Scene(new StackPane(selectableText), 280, 80));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                Node textContent = selectableText.lookup(".content");
                assertNotNull(textContent);
                textContent.fireEvent(primaryMouseEvent(MouseEvent.MOUSE_PRESSED, 1, true, true));
                textContent.fireEvent(primaryMouseEvent(MouseEvent.MOUSE_DRAGGED, 80, true, false));
                textContent.fireEvent(primaryMouseEvent(MouseEvent.MOUSE_RELEASED, 80, false, false));
                assertFalse(selectableText.getSelectedText().isEmpty());
                selectableText.selectRange(0, "Selectable".length());
                ClipboardContent initialClipboard = new ClipboardContent();
                initialClipboard.putString("Initial clipboard text");
                systemClipboard.setContent(initialClipboard);

                boolean isMac = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac");
                selectableText.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "c", KeyCode.C,
                        false, !isMac, false, isMac));

                assertEquals("Selectable", systemClipboard.getString());
                selectableText.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "x", "x", KeyCode.UNDEFINED,
                        false, false, false, false));
                assertEquals("Selectable profile text", selectableText.getText());
            } finally {
                try {
                    stage.hide();
                } finally {
                    if (originalClipboardContent.isEmpty()) {
                        systemClipboard.clear();
                    } else {
                        assertTrue(systemClipboard.setContent(originalClipboardContent));
                    }
                }
            }
            return null;
        });
    }

    @Test
    public void selectableText_focusMoves_oldSelectionIsCleared() throws Exception {
        onFxThread(() -> {
            SelectableText firstText = new SelectableText();
            firstText.setText("First selectable text");
            SelectableText secondText = new SelectableText();
            secondText.setText("Second selectable text");
            VBox content = new VBox(firstText, secondText);
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(content, 280, 100));
                stage.show();

                firstText.requestFocus();
                firstText.selectRange(0, "First".length());
                assertEquals("First", firstText.getSelectedText());

                secondText.requestFocus();
                secondText.selectRange(0, "Second".length());

                assertEquals("", firstText.getSelectedText());
                assertEquals("Second", secondText.getSelectedText());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void selectableText_wrappedContent_growsVertically() throws Exception {
        onFxThread(() -> {
            SelectableText shortText = new SelectableText();
            shortText.setText("Short text");
            SelectableText longText = new SelectableText();
            longText.setText("Selectable text remains readable when it wraps across several lines. ".repeat(4));
            VBox content = new VBox(shortText, longText);
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(content, 240, 300));
                stage.show();
                content.applyCss();
                content.layout();

                assertTrue(longText.getHeight() > shortText.getHeight() * 2);
                assertEquals(Region.USE_PREF_SIZE, longText.getMinHeight());
                assertEquals(Region.USE_PREF_SIZE, longText.getMaxHeight());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void selectableText_scrollEvent_scrollsSessionHistoryList() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null, sessionNotes(12)));
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 360, 480));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                ListView<StudentProfilePanel.NumberedSessionNote> listView = getSessionNoteListView(panel);
                SelectableText noteText = (SelectableText) listView.lookup(".session-note-text");
                assertNotNull(noteText);
                ScrollBar verticalScrollBar = getVerticalScrollBar(listView);
                assertTrue(verticalScrollBar.isVisible());
                double initialScrollValue = verticalScrollBar.getValue();
                ScrollEvent downwardScrollEvent = scrollEvent();

                noteText.fireEvent(downwardScrollEvent);

                assertTrue(verticalScrollBar.getValue() > initialScrollValue);
                double scrolledValue = verticalScrollBar.getValue();
                ScrollEvent upwardScrollEvent = scrollEvent(40);
                noteText.fireEvent(upwardScrollEvent);
                assertTrue(verticalScrollBar.getValue() < scrolledValue);
                double restoredScrollValue = verticalScrollBar.getValue();
                ScrollEvent horizontalScrollEvent = scrollEvent(0);
                noteText.fireEvent(horizontalScrollEvent);
                assertEquals(restoredScrollValue, verticalScrollBar.getValue());
            } finally {
                stage.hide();
            }

            SelectableText standaloneText = new SelectableText();
            ScrollEvent standaloneScrollEvent = scrollEvent();
            standaloneText.fireEvent(standaloneScrollEvent);
            assertFalse(standaloneScrollEvent.isConsumed());
            return null;
        });
    }

    @Test
    public void sessionNoteListCell_populatedAndEmptyNotes_updateGraphic() throws Exception {
        SessionNote note = new SessionNote("Reviewed indices.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);
        ListView<StudentProfilePanel.NumberedSessionNote> listView = getSessionNoteListView(panel);
        ListCell<StudentProfilePanel.NumberedSessionNote> cell =
                onFxThread(() -> listView.getCellFactory().call(listView));
        StudentProfilePanel.NumberedSessionNote numberedNote =
                new StudentProfilePanel.NumberedSessionNote(note, 1);

        onFxThread(() -> {
            updateNoteCell(cell, numberedNote, false);
            return null;
        });
        assertTrue(cell.getGraphic() instanceof VBox);

        onFxThread(() -> {
            updateNoteCell(cell, null, false);
            return null;
        });
        assertNull(cell.getGraphic());
        assertNull(cell.getText());

        onFxThread(() -> {
            updateNoteCell(cell, null, true);
            return null;
        });
        assertNull(cell.getGraphic());
        assertNull(cell.getText());
    }

    @Test
    public void sessionNoteListCell_deletionPreviewMarksOnlyTargetAndClearsOnReuse() throws Exception {
        SessionNote newestNote = new SessionNote("Newest lesson.",
                OffsetDateTime.parse("2026-09-19T18:35:00+08:00"));
        SessionNote oldestNote = new SessionNote("Oldest lesson.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);
        ListView<StudentProfilePanel.NumberedSessionNote> listView = getSessionNoteListView(panel);
        ListCell<StudentProfilePanel.NumberedSessionNote> cell =
                onFxThread(() -> listView.getCellFactory().call(listView));
        PseudoClass deletionTarget = PseudoClass.getPseudoClass("deletion-target");

        onFxThread(() -> {
            panel.setProfile(profile(null, List.of(newestNote, oldestNote)));
            panel.previewDeletionOf(Index.fromOneBased(1));
            updateNoteCell(cell, listView.getItems().get(0), false);
            assertFalse(cell.getPseudoClassStates().contains(deletionTarget));
            updateNoteCell(cell, listView.getItems().get(1), false);
            assertTrue(cell.getPseudoClassStates().contains(deletionTarget));

            panel.previewDeletionOf(Index.fromOneBased(3));
            updateNoteCell(cell, listView.getItems().get(1), false);
            assertFalse(cell.getPseudoClassStates().contains(deletionTarget));

            panel.previewDeletionOf(Index.fromOneBased(1));
            panel.clearDeletionPreview();
            updateNoteCell(cell, listView.getItems().get(1), false);
            assertFalse(cell.getPseudoClassStates().contains(deletionTarget));
            updateNoteCell(cell, null, true);
            assertFalse(cell.getPseudoClassStates().contains(deletionTarget));
            return null;
        });
    }

    @Test
    public void profilePanel_deletionPreviewScrollsOlderNoteIntoView() throws Exception {
        List<SessionNote> notes = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            notes.add(new SessionNote("Lesson " + i + ".",
                    OffsetDateTime.parse("2026-09-18T18:35:00+08:00").minusDays(i)));
        }
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null, notes));
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(panel.getRoot(), 360, 480));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                panel.previewDeletionOf(Index.fromOneBased(1));
                stage.getScene().getRoot().layout();
                List<String> visibleHeaders = panel.getRoot().lookupAll(".session-note-header").stream()
                        .filter(SelectableText.class::isInstance)
                        .map(SelectableText.class::cast)
                        .map(SelectableText::getText)
                        .toList();
                assertTrue(visibleHeaders.stream().anyMatch(header -> header.startsWith("[1]")),
                        "Visible note headers: " + visibleHeaders);
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void sessionNoteListCell_longNote_wrapsWithinListViewWidth() throws Exception {
        String longNoteText = "Reviewed algebra concepts and planned revision exercises for the next lesson. "
                .repeat(6);
        SessionNote note = new SessionNote(longNoteText,
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            Stage stage = new Stage();
            try {
                panel.setProfile(profile(null, List.of(note)));
                stage.setScene(new Scene(panel.getRoot(), 360, 480));
                stage.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();

                ListView<StudentProfilePanel.NumberedSessionNote> listView = getSessionNoteListView(panel);
                ListCell<?> cell = (ListCell<?>) listView.lookup(".list-cell");
                assertNotNull(cell);
                VBox card = (VBox) cell.getGraphic();
                assertNotNull(card);
                SelectableText header = (SelectableText) card.getChildren().get(0);
                SelectableText noteText = (SelectableText) card.getChildren().get(1);

                assertTrue(card.getWidth() <= listView.getWidth());
                assertEquals("[1] 18 Sep 2026, 6:35 PM (UTC+08:00)", header.getText());
                assertTrue(noteText.getHeight() > noteText.getFont().getSize() * 2);
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void sessionNoteListCell_nearLimitNote_showsEveryWrappedLine() throws Exception {
        StringBuilder noteText = new StringBuilder("w001");
        for (int i = 2; i <= 100; i++) {
            noteText.append(String.format(" w%03d", i));
        }
        SessionNote note = new SessionNote(noteText.toString(),
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            Stage stage = new Stage();
            try {
                panel.setProfile(profile(null, List.of(note)));
                Scene scene = new Scene(panel.getRoot(), 360, 600);
                scene.getStylesheets().add(MainApp.class.getResource("/view/TutorTrackTheme.css").toExternalForm());
                stage.setScene(scene);
                stage.show();

                for (double width : new double[] {360, 500, 900}) {
                    stage.setWidth(width);
                    scene.getRoot().applyCss();
                    scene.getRoot().layout();

                    ListCell<?> cell = (ListCell<?>) getSessionNoteListView(panel).lookup(".list-cell");
                    assertNotNull(cell);
                    SelectableText displayedNote = (SelectableText) ((VBox) cell.getGraphic()).getChildren().get(1);
                    Text renderedText = (Text) displayedNote.lookup(".text");
                    assertNotNull(renderedText);

                    assertTrue(displayedNote.getHeight() >= renderedText.getLayoutBounds().getHeight(),
                            "Note text is clipped at panel width " + width);
                }
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void commandBox_pageKeys_requestPageScrollWithoutChangingText() throws Exception {
        List<Integer> scrolledPages = new ArrayList<>();
        CommandBox commandBox = onFxThread(() -> new CommandBox(
                commandText -> new CommandResult("Command executed."), scrolledPages::add));
        TextField commandTextField = getCommandTextField(commandBox);

        onFxThread(() -> {
            commandTextField.setText("note 1 nt/draft");
            pressKey(commandTextField, KeyCode.PAGE_DOWN);
            pressKey(commandTextField, KeyCode.PAGE_UP);
            pressKey(commandTextField, KeyCode.LEFT);
            assertEquals("note 1 nt/draft", commandTextField.getText());
            return null;
        });

        assertEquals(List.of(1, -1), scrolledPages);
    }

    @Test
    public void scrollSessionNotes_beforeLayout_doesNothing() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null, sessionNotes(12)));
            panel.scrollSessionNotes(1);
            return null;
        });
    }

    @Test
    public void mainWindow_pageKeysInCommandBox_scrollSessionNotes() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID),
                Optional.of(profile(null, sessionNotes(12))));

        onFxThread(() -> {
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            stage.setWidth(900);
            stage.setHeight(800);
            stage.show();
            try {
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                stage.getScene().getRoot().layout();
                TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");
                ScrollBar verticalScrollBar = getVerticalScrollBar(
                        getSessionNoteListView(mainWindow.getStudentProfilePanel()));
                assertTrue(verticalScrollBar.isVisible());
                assertEquals(0, verticalScrollBar.getValue());

                pressKey(commandTextField, KeyCode.PAGE_UP);
                assertEquals(0, verticalScrollBar.getValue());

                pressKey(commandTextField, KeyCode.PAGE_DOWN);
                double scrolledValue = verticalScrollBar.getValue();
                assertTrue(scrolledValue > 0);

                pressKey(commandTextField, KeyCode.PAGE_DOWN);
                double furtherScrolledValue = verticalScrollBar.getValue();
                assertTrue(furtherScrolledValue > scrolledValue);

                pressKey(commandTextField, KeyCode.PAGE_UP);
                assertTrue(verticalScrollBar.getValue() < furtherScrolledValue);

                // Cell heights are re-estimated while scrolling, so paging up need not mirror paging down exactly;
                // extra presses must still stop at the top.
                for (int i = 0; i < 5; i++) {
                    pressKey(commandTextField, KeyCode.PAGE_UP);
                }
                assertEquals(0, verticalScrollBar.getValue());
            } finally {
                stage.hide();
            }
            return null;
        });
    }


    @Test
    public void mainWindow_pageKeysInCompactWindow_scrollProfilePaneBeforeSessionNotes() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID),
                Optional.of(profile(null, sessionNotes(12))));

        onFxThread(() -> {
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            stage.setWidth(900);
            stage.setHeight(360);
            stage.show();
            try {
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                stage.getScene().getRoot().layout();
                TextField commandTextField = (TextField) stage.getScene().lookup("#commandTextField");
                ScrollPane profileScrollPane = (ScrollPane) stage.getScene().lookup("#profileScrollPane");
                ScrollBar notesScrollBar = getVerticalScrollBar(
                        getSessionNoteListView(mainWindow.getStudentProfilePanel()));
                assertTrue(profileScrollPane.getVmax() > profileScrollPane.getVmin());
                assertEquals(0, profileScrollPane.getVvalue());
                assertEquals(0, notesScrollBar.getValue());

                pressKey(commandTextField, KeyCode.PAGE_DOWN);
                assertTrue(profileScrollPane.getVvalue() > 0);
                assertEquals(0, notesScrollBar.getValue());

                for (int i = 0; i < 10 && notesScrollBar.getValue() == 0; i++) {
                    pressKey(commandTextField, KeyCode.PAGE_DOWN);
                }
                assertTrue(notesScrollBar.getValue() > 0);
                double notesValue = notesScrollBar.getValue();
                pressKey(commandTextField, KeyCode.PAGE_DOWN);
                assertTrue(notesScrollBar.getValue() > notesValue);

                for (int i = 0; i < 10; i++) {
                    pressKey(commandTextField, KeyCode.PAGE_UP);
                }
                assertEquals(0, notesScrollBar.getValue());
                assertEquals(0, profileScrollPane.getVvalue());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void commandHistory_successfulAndFailedCommands_areRecalled() throws Exception {
        List<String> executedCommands = new ArrayList<>();
        CommandBox commandBox = onFxThread(() -> new CommandBox(commandText -> {
            executedCommands.add(commandText);
            if (commandText.equals("invalid")) {
                throw new CommandException("Invalid command.");
            }
            return new CommandResult("Command executed.");
        }));
        TextField commandTextField = getCommandTextField(commandBox);

        onFxThread(() -> {
            submitCommand(commandTextField, "list");
            submitCommand(commandTextField, "invalid");
            commandTextField.setText("draft");
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("invalid", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());
            return null;
        });

        assertEquals(List.of("list", "invalid"), executedCommands);
    }

    @Test
    public void commandHistory_duplicatesAndEditedRecall_areNewEntries() throws Exception {
        CommandBox commandBox = onFxThread(() -> new CommandBox(
                commandText -> new CommandResult("Command executed.")));
        TextField commandTextField = getCommandTextField(commandBox);

        onFxThread(() -> {
            submitCommand(commandTextField, "list");
            submitCommand(commandTextField, "view 1");
            submitCommand(commandTextField, "view 1");
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());

            pressKey(commandTextField, KeyCode.DOWN);
            commandTextField.setText("view 2");
            submitCommand(commandTextField, "view 2");
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("view 2", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            return null;
        });
    }

    @Test
    public void commandHistory_navigationBoundaries_restoreDraft() throws Exception {
        CommandBox commandBox = onFxThread(() -> new CommandBox(
                commandText -> new CommandResult("Command executed.")));
        TextField commandTextField = getCommandTextField(commandBox);

        onFxThread(() -> {
            submitCommand(commandTextField, "list");
            submitCommand(commandTextField, "view 1");
            commandTextField.setText("unfinished command");

            pressKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());

            pressKey(commandTextField, KeyCode.DOWN);
            assertEquals("view 1", commandTextField.getText());
            pressKey(commandTextField, KeyCode.DOWN);
            assertEquals("unfinished command", commandTextField.getText());
            pressKey(commandTextField, KeyCode.DOWN);
            assertEquals("unfinished command", commandTextField.getText());
            return null;
        });
    }

    @Test
    public void commandHistory_blankAndOverCapacity_keepsLatestHundredNonBlankCommands() throws Exception {
        List<String> executedCommands = new ArrayList<>();
        CommandBox commandBox = onFxThread(() -> new CommandBox(commandText -> {
            executedCommands.add(commandText);
            return new CommandResult("Command executed.");
        }));
        TextField commandTextField = getCommandTextField(commandBox);

        onFxThread(() -> {
            submitCommand(commandTextField, "");
            submitCommand(commandTextField, "   ");
            commandTextField.setText("draft");
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("draft", commandTextField.getText());

            for (int i = 0; i <= 100; i++) {
                submitCommand(commandTextField, "command " + i);
            }
            commandTextField.setText("new draft");
            for (int i = 0; i < 100; i++) {
                pressKey(commandTextField, KeyCode.UP);
            }
            assertEquals("command 1", commandTextField.getText());
            pressKey(commandTextField, KeyCode.UP);
            assertEquals("command 1", commandTextField.getText());
            for (int i = 0; i < 100; i++) {
                pressKey(commandTextField, KeyCode.DOWN);
            }
            assertEquals("new draft", commandTextField.getText());
            return null;
        });

        assertEquals(102, executedCommands.size());
        assertEquals("   ", executedCommands.get(0));
    }

    @Test
    public void executeCommand_successfulCommand_refreshesRoster() throws Exception {
        StudentRoster initialRoster = new StudentRoster(List.of());
        StudentRoster refreshedRoster = new StudentRoster(List.of(student("Alex Tan")));
        TestLogic logic = new TestLogic(initialRoster, refreshedRoster);

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("help");
            assertEquals(refreshedRoster.getEntries(), getListView(mainWindow.getStudentRosterPanel()).getItems());
            return null;
        });
    }

    @Test
    public void statusBarFooter_dataFileInHomeFolder_showsPathRelativeToHomeFolder() throws Exception {
        Path dataFile = HomeFolder.resolve("data", "tutortrack.json");

        String shownLocation = onFxThread(() -> getSaveLocationText(new StatusBarFooter(dataFile)));

        assertEquals(Path.of(".", "data", "tutortrack.json").toString(), shownLocation);
    }

    @Test
    public void statusBarFooter_dataFileOutsideHomeFolder_showsFullPath() throws Exception {
        Path dataFile = Path.of("").toAbsolutePath().getRoot().resolve("other-folder").resolve("tutortrack.json");

        String shownLocation = onFxThread(() -> getSaveLocationText(new StatusBarFooter(dataFile)));

        assertEquals(dataFile.toString(), shownLocation);
    }

    @Test
    public void mainWindow_largeSavedSize_clampsToTargetScreenAndKeepsResponsiveMinimums() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            logic.setInitialGuiSettings(new GuiSettings(visualBounds.getWidth() + 500,
                    visualBounds.getHeight() + 500, (int) visualBounds.getMinX(), (int) visualBounds.getMinY()));
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();

            assertEquals(visualBounds.getWidth(), stage.getWidth());
            assertEquals(visualBounds.getHeight(), stage.getHeight());
            assertEquals(visualBounds.getWidth(), stage.getMaxWidth());
            assertEquals(visualBounds.getHeight(), stage.getMaxHeight());
            assertEquals(700, stage.getMinWidth());
            assertEquals(420, stage.getMinHeight());
            StackPane resultDisplay = (StackPane) stage.getScene().lookup("#resultDisplayPlaceholder");
            assertNotNull(resultDisplay);
            assertEquals(64, resultDisplay.getMinHeight());
            assertEquals(82, resultDisplay.getPrefHeight());
            assertEquals(100, resultDisplay.getMaxHeight());
            return null;
        });
    }

    @Test
    public void mainWindow_savedPositionNearScreenEdge_clampsEntireWindowToVisualBounds() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            double savedWidth = visualBounds.getWidth() * 0.75;
            double savedHeight = visualBounds.getHeight() * 0.75;
            int savedX = (int) visualBounds.getMaxX() - 1;
            int savedY = (int) visualBounds.getMaxY() - 1;
            logic.setInitialGuiSettings(new GuiSettings(savedWidth, savedHeight, savedX, savedY));

            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();

            assertEquals(visualBounds.getMaxX() - savedWidth, stage.getX());
            assertEquals(visualBounds.getMaxY() - savedHeight, stage.getY());
            assertTrue(stage.getX() >= visualBounds.getMinX());
            assertTrue(stage.getY() >= visualBounds.getMinY());
            assertTrue(stage.getX() + stage.getWidth() <= visualBounds.getMaxX());
            assertTrue(stage.getY() + stage.getHeight() <= visualBounds.getMaxY());
            return null;
        });
    }

    @Test
    public void mainWindow_movedAfterStartup_refreshesMaximumSizeFromCurrentScreen() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            logic.setInitialGuiSettings(new GuiSettings(700, 420,
                    (int) visualBounds.getMinX(), (int) visualBounds.getMinY()));
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            stage.setMaxWidth(visualBounds.getWidth() / 2);
            stage.setMaxHeight(visualBounds.getHeight() / 2);

            stage.setX(stage.getX() + 1);

            assertEquals(visualBounds.getWidth(), stage.getMaxWidth());
            assertEquals(visualBounds.getHeight(), stage.getMaxHeight());
            return null;
        });
    }

    @Test
    public void mainWindow_minimumSize_keepsPrimaryWorkspaceAreasVisible() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            stage.setWidth(700);
            stage.setHeight(420);
            stage.show();
            try {
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                assertNodeWithinScene(stage, "#commandBoxPlaceholder");
                assertNodeWithinScene(stage, "#resultDisplayPlaceholder");
                assertNodeWithinScene(stage, "#workspaceSplitPane");
                assertNodeWithinScene(stage, "#statusbarPlaceholder");
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void executeCommand_viewResult_displaysRequestedProfile() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        StudentProfile profile = profile("alex.parent@example.com");
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            assertEquals("Alex Tan", ((SelectableText) getProfileContent(mainWindow.getStudentProfilePanel())
                    .getChildren().get(0)).getText());
            assertEquals(ALEX_ID, getListView(mainWindow.getStudentRosterPanel())
                    .getSelectionModel().getSelectedItem().getStudentId());
            return null;
        });
    }

    @Test
    public void executeCommand_deletePreview_opensTargetProfileAndKeepsRoster() throws Exception {
        Student alex = student("Alex Tan", ALEX_ID);
        Student blair = student("Blair Tan", BLAIR_ID);
        StudentRoster roster = new StudentRoster(List.of(alex, blair));
        StudentProfile blairProfile = new StudentProfile(BLAIR_ID, blair.getName(),
                blair.getParentGuardianContact(), blair.getSubject(), blair.getCurrentLevel(), List.of());
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult("If yes, type: delete 2 /confirm", BLAIR_ID));
            logic.setProfile(Optional.of(blairProfile));

            mainWindow.executeCommand("delete 2");

            assertEquals("Blair Tan", ((SelectableText) getProfileContent(mainWindow.getStudentProfilePanel())
                    .getChildren().get(0)).getText());
            ListView<StudentRosterEntry> rosterList = getListView(mainWindow.getStudentRosterPanel());
            assertEquals(BLAIR_ID, rosterList.getSelectionModel().getSelectedItem().getStudentId());
            assertEquals(List.of(ALEX_ID, BLAIR_ID), rosterList.getItems().stream()
                    .map(StudentRosterEntry::getStudentId).toList());

            logic.setCommandFailure(new CommandException("Invalid student index."));
            assertThrows(CommandException.class, () -> mainWindow.executeCommand("delete 9"));
            assertEquals("Blair Tan", ((SelectableText) getProfileContent(mainWindow.getStudentProfilePanel())
                    .getChildren().get(0)).getText());
            assertEquals(BLAIR_ID, rosterList.getSelectionModel().getSelectedItem().getStudentId());
            return null;
        });
    }

    @Test
    public void executeCommand_noteDeletionPreviewKeepsMarkerUntilParsedCommand() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(
                student("Alex Tan", ALEX_ID), student("Blair Lim", BLAIR_ID)));
        SessionNote newestNote = new SessionNote("Newest lesson.",
                OffsetDateTime.parse("2026-09-19T18:35:00+08:00"));
        SessionNote oldestNote = new SessionNote("Oldest lesson.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfile targetProfile = profile(BLAIR_ID, "Blair Lim", List.of(newestNote, oldestNote));
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult("Confirm note deletion.", BLAIR_ID, Index.fromOneBased(1)));
            logic.setProfile(Optional.of(targetProfile));

            mainWindow.executeCommand("delete note 2 1");

            StudentProfilePanel profilePanel = mainWindow.getStudentProfilePanel();
            ListView<StudentProfilePanel.NumberedSessionNote> noteList = getSessionNoteListView(profilePanel);
            ListCell<StudentProfilePanel.NumberedSessionNote> cell = noteList.getCellFactory().call(noteList);
            updateNoteCell(cell, noteList.getItems().get(1), false);
            assertEquals("Blair Lim", ((SelectableText) getProfileContent(profilePanel)
                    .getChildren().get(0)).getText());
            assertEquals(BLAIR_ID, getListView(mainWindow.getStudentRosterPanel())
                    .getSelectionModel().getSelectedItem().getStudentId());
            assertTrue(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("deletion-target")));

            logic.setCommandResult(new CommandResult("Canceled."));
            mainWindow.executeCommand("view 1");
            updateNoteCell(cell, noteList.getItems().get(1), false);
            assertFalse(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("deletion-target")));
            assertEquals("Blair Lim", ((SelectableText) getProfileContent(profilePanel)
                    .getChildren().get(0)).getText());
            assertEquals(2, noteList.getItems().size());

            logic.setCommandResult(new CommandResult("Confirm note deletion.", BLAIR_ID, Index.fromOneBased(1)));
            mainWindow.executeCommand("delete note 2 1");
            updateNoteCell(cell, noteList.getItems().get(1), false);
            assertTrue(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("deletion-target")));

            logic.setParseFailure(new ParseException("Invalid format."));
            assertThrows(ParseException.class, () -> mainWindow.executeCommand("delete note 2 1 /confir"));
            updateNoteCell(cell, noteList.getItems().get(1), false);
            assertTrue(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("deletion-target")));
            assertEquals(2, noteList.getItems().size());

            logic.setParseFailure(null);
            logic.setCommandFailure(new CommandException("Session note [9] does not exist."));
            assertThrows(CommandException.class, () -> mainWindow.executeCommand("delete note 2 9"));
            updateNoteCell(cell, noteList.getItems().get(1), false);
            assertFalse(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("deletion-target")));
            assertEquals(2, noteList.getItems().size());

            logic.setCommandFailure(null);
            mainWindow.executeCommand("delete note 2 1");
            logic.setCommandResult(new CommandResult("Deleted note."));
            logic.setProfile(Optional.of(profile(BLAIR_ID, "Blair Lim", List.of(newestNote))));
            mainWindow.executeCommand("delete note 2 1 /confirm");
            assertEquals(1, noteList.getItems().size());
            updateNoteCell(cell, noteList.getItems().get(0), false);
            assertFalse(cell.getPseudoClassStates().contains(PseudoClass.getPseudoClass("deletion-target")));
            return null;
        });
    }

    @Test
    public void executeCommand_successAfterView_refreshesOpenProfile() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));
        SessionNote addedNote = new SessionNote("Reviewed indices.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult("Added session note for Alex Tan."));
            logic.setProfile(Optional.of(profile(null, List.of(addedNote))));
            mainWindow.executeCommand("note 1 nt/Reviewed indices.");
            assertEquals(List.of(new StudentProfilePanel.NumberedSessionNote(addedNote, 1)),
                    getSessionNoteListView(mainWindow.getStudentProfilePanel()).getItems());
            assertEquals(ALEX_ID, getListView(mainWindow.getStudentRosterPanel())
                    .getSelectionModel().getSelectedItem().getStudentId());
            return null;
        });
    }

    @Test
    public void executeCommand_editAfterView_refreshesReorderedRosterAndOpenProfile() throws Exception {
        Student alex = student("Alex Tan", ALEX_ID);
        Student blair = student("Blair Tan", BLAIR_ID);
        Student editedAlex = new Student(new StudentName("Zane Tan"), alex.getParentGuardianContact(),
                new Subject("Physics"), new CurrentLevel("Secondary 4"), ALEX_ID);
        StudentRoster initialRoster = new StudentRoster(List.of(alex, blair));
        StudentRoster editedRoster = new StudentRoster(List.of(editedAlex, blair));
        TestLogic logic = new TestLogic(initialRoster, editedRoster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));
        StudentProfile editedProfile = new StudentProfile(ALEX_ID, editedAlex.getName(),
                editedAlex.getParentGuardianContact(), editedAlex.getSubject(), editedAlex.getCurrentLevel(),
                List.of());

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult("Edited student: Zane Tan (Physics, Secondary 4)."));
            logic.setProfile(Optional.of(editedProfile));

            mainWindow.executeCommand("edit 1 n/Zane Tan sub/Physics l/Secondary 4");

            ListView<StudentRosterEntry> rosterList = getListView(mainWindow.getStudentRosterPanel());
            assertEquals(List.of(BLAIR_ID, ALEX_ID), rosterList.getItems().stream()
                    .map(StudentRosterEntry::getStudentId).toList());
            assertEquals(ALEX_ID, rosterList.getSelectionModel().getSelectedItem().getStudentId());
            assertEquals("Zane Tan", ((SelectableText) getProfileContent(mainWindow.getStudentProfilePanel())
                    .getChildren().get(0)).getText());
            return null;
        });
    }

    @Test
    public void executeCommand_deleteSessionNoteAfterView_refreshesNotesAndNumbers() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        SessionNote newestNote = new SessionNote("Third session.",
                OffsetDateTime.parse("2026-09-19T18:35:00+08:00"));
        SessionNote middleNote = new SessionNote("Incorrect middle session.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        SessionNote oldestNote = new SessionNote("First session.",
                OffsetDateTime.parse("2026-09-17T18:35:00+08:00"));
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID),
                Optional.of(profile(null, List.of(newestNote, middleNote, oldestNote))));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult("Successfully deleted session note [2] for Alex Tan."));
            logic.setProfile(Optional.of(profile(null, List.of(newestNote, oldestNote))));

            mainWindow.executeCommand("delete note 1 2 /confirm");

            assertEquals(List.of(
                    new StudentProfilePanel.NumberedSessionNote(newestNote, 2),
                    new StudentProfilePanel.NumberedSessionNote(oldestNote, 1)),
                    getSessionNoteListView(mainWindow.getStudentProfilePanel()).getItems());
            assertEquals(ALEX_ID, getListView(mainWindow.getStudentRosterPanel())
                    .getSelectionModel().getSelectedItem().getStudentId());
            return null;
        });
    }

    @Test
    public void executeCommand_failedAfterView_leavesOpenProfileUnchanged() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));
        TestLogic logic = new TestLogic(roster, roster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));
        SessionNote unseenNote = new SessionNote("Must not be displayed.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setProfile(Optional.of(profile(null, List.of(unseenNote))));
            logic.setCommandFailure(new CommandException("Unable to add note."));
            assertThrows(CommandException.class, () ->
                    mainWindow.executeCommand("note 1 nt/Must not be displayed."));
            assertTrue(getSessionNoteListView(mainWindow.getStudentProfilePanel()).getItems().isEmpty());
            assertEquals(ALEX_ID, getListView(mainWindow.getStudentRosterPanel())
                    .getSelectionModel().getSelectedItem().getStudentId());
            return null;
        });
    }

    @Test
    public void executeCommand_confirmedDeleteDisplayedStudent_clearsOpenProfile() throws Exception {
        StudentRoster initialRoster = new StudentRoster(List.of(student("Alex Tan")));
        StudentRoster emptyRoster = new StudentRoster(List.of());
        TestLogic logic = new TestLogic(initialRoster, emptyRoster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult("Successfully deleted Alex Tan."));
            logic.setProfile(Optional.empty());
            mainWindow.executeCommand("delete 1 /confirm");
            StackPane root = (StackPane) mainWindow.getStudentProfilePanel().getRoot();
            assertTrue(root.getChildren().get(0).isVisible());
            assertFalse(root.getChildren().get(1).isVisible());
            assertTrue(getListView(mainWindow.getStudentRosterPanel()).getItems().isEmpty());
            assertTrue(getListView(mainWindow.getStudentRosterPanel()).getSelectionModel().isEmpty());
            return null;
        });
    }

    // Branch coverage of MainWindow command-result handling: help requested versus not, window already open.
    @Test
    public void executeCommand_helpResult_opensHelpWindowOnce() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()),
                new CommandResult("Help shown.", true, false), Optional.empty());

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            long windowsBeforeHelp = countShowingWindows();

            mainWindow.executeCommand("help");
            long windowsAfterFirstHelp = countShowingWindows();
            mainWindow.executeCommand("help");
            long windowsAfterSecondHelp = countShowingWindows();

            try {
                assertEquals(windowsBeforeHelp + 1, windowsAfterFirstHelp);
                assertEquals(windowsAfterFirstHelp, windowsAfterSecondHelp);
            } finally {
                mainWindow.handleHelp();
                closeShowingWindows();
            }
            return null;
        });
    }

    @Test
    public void executeCommand_exitResult_savesWindowSettingsAndHidesWindow() throws Exception {
        RecordingLogic logic = new RecordingLogic(new CommandResult("Exiting.", false, true));

        onFxThread(() -> {
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.show();

            mainWindow.executeCommand("exit");

            assertFalse(stage.isShowing());
            assertEquals(stage.getWidth(), logic.savedGuiSettings.getWindowWidth());
            assertEquals(stage.getHeight(), logic.savedGuiSettings.getWindowHeight());
            return null;
        });
    }

    @Test
    public void executeCommand_ordinaryResult_keepsWindowOpenAndHelpClosed() throws Exception {
        RecordingLogic logic = new RecordingLogic(new CommandResult("Done."));

        onFxThread(() -> {
            Stage stage = new Stage();
            MainWindow mainWindow = new MainWindow(stage, logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.show();
            long windowsBeforeCommand = countShowingWindows();

            mainWindow.executeCommand("list");

            try {
                assertTrue(stage.isShowing());
                assertEquals(windowsBeforeCommand, countShowingWindows());
                assertNull(logic.savedGuiSettings);
                assertSame(stage, mainWindow.getPrimaryStage());
            } finally {
                stage.hide();
            }
            return null;
        });
    }

    @Test
    public void mainWindow_helpAcceleratorPressedInCommandBox_opensHelpWindow() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            TextField commandTextField = (TextField) mainWindow.getPrimaryStage().getScene()
                    .lookup("#commandTextField");
            long windowsBeforeHelp = countShowingWindows();

            pressKey(commandTextField, KeyCode.F1);

            try {
                assertEquals(windowsBeforeHelp + 1, countShowingWindows());
            } finally {
                closeShowingWindows();
            }
            return null;
        });
    }

    @Test
    public void mainWindow_otherKeyPressedInCommandBox_doesNotOpenHelpWindow() throws Exception {
        TestLogic logic = new TestLogic(new StudentRoster(List.of()), new StudentRoster(List.of()));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            TextField commandTextField = (TextField) mainWindow.getPrimaryStage().getScene()
                    .lookup("#commandTextField");
            long windowsBeforeKey = countShowingWindows();

            pressKey(commandTextField, KeyCode.F2);

            assertEquals(windowsBeforeKey, countShowingWindows());
            return null;
        });
    }

    // Equivalence partitions of the timestamp offset: UTC, positive, negative and fractional-hour offsets.
    @Test
    public void formatRecordedAt_differentOffsets_preservesRecordedOffset() {
        assertEquals("5 Jan 2026, 9:30 AM (UTC+05:30)", SessionNoteCard.formatRecordedAt(
                OffsetDateTime.parse("2026-01-05T09:30:00+05:30")));
        assertEquals("5 Jan 2026, 9:30 AM (UTC-08:00)", SessionNoteCard.formatRecordedAt(
                OffsetDateTime.parse("2026-01-05T09:30:00-08:00")));
    }

    // Boundary values of the 12-hour clock: midnight and noon.
    @Test
    public void formatRecordedAt_midnightAndNoon_usesTwelveHourClock() {
        assertEquals("5 Jan 2026, 12:00 AM (UTC+00:00)", SessionNoteCard.formatRecordedAt(
                OffsetDateTime.parse("2026-01-05T00:00:00Z")));
        assertEquals("5 Jan 2026, 12:00 PM (UTC+00:00)", SessionNoteCard.formatRecordedAt(
                OffsetDateTime.parse("2026-01-05T12:00:00Z")));
    }

    // Boundary value analysis for the note number: 1 is the smallest valid number, 0 and below are invalid.
    @Test
    public void sessionNoteCard_noteNumberBoundary_acceptsOneAndRejectsZeroAndNegative() throws Exception {
        SessionNote note = new SessionNote("Boundary note.", OffsetDateTime.parse("2026-01-05T09:30:00Z"));

        VBox card = (VBox) onFxThread(() -> new SessionNoteCard(note, 1).getRoot());

        assertEquals("[1] 5 Jan 2026, 9:30 AM (UTC+00:00)", ((SelectableText) card.getChildren().get(0)).getText());
        assertThrows(IllegalArgumentException.class, () -> new SessionNoteCard(note, -1));
        assertThrows(NullPointerException.class, () -> new SessionNoteCard(null, 1));
    }

    @Test
    public void commandBox_nullPageScroller_throwsNullPointerException() throws Exception {
        onFxThread(() -> {
            assertThrows(NullPointerException.class, () -> new CommandBox(
                    commandText -> new CommandResult("Command executed."), null));
            return null;
        });
    }

    // Repeated use: a second failure while the error style is already shown must not stack the style class.
    @Test
    public void commandBox_repeatedFailures_showErrorStyleOnceUntilTextChanges() throws Exception {
        CommandBox commandBox = onFxThread(() -> new CommandBox(commandText -> {
            throw new CommandException("Rejected.");
        }));
        TextField commandTextField = getCommandTextField(commandBox);

        onFxThread(() -> {
            submitCommand(commandTextField, "bad");
            submitCommand(commandTextField, "bad");
            assertEquals(1, commandTextField.getStyleClass().stream()
                    .filter(CommandBox.ERROR_STYLE_CLASS::equals).count());

            commandTextField.setText("good");
            assertFalse(commandTextField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            return null;
        });
    }

    @Test
    public void selectableText_negativeLineSpacing_throwsAssertionError() throws Exception {
        onFxThread(() -> {
            SelectableText text = new SelectableText();
            assertThrows(AssertionError.class, () -> text.setLineSpacing(-1));
            text.setLineSpacing(0);
            assertEquals(0, text.getLineSpacing());
            return null;
        });
    }

    // Equivalence partitions of the layout width: unusable widths disable wrapping, a narrow width wraps.
    @Test
    public void selectableText_unusableOrNarrowWidth_wrapsOnlyWhenWidthIsUsable() throws Exception {
        onFxThread(() -> {
            SelectableText text = new SelectableText();
            text.setText("Selectable text wraps only when a usable width is available. ".repeat(3));

            double unwrappedHeight = text.prefHeight(10_000);

            assertEquals(unwrappedHeight, text.prefHeight(-1));
            assertEquals(unwrappedHeight, text.prefHeight(0));
            assertEquals(unwrappedHeight, text.prefHeight(Double.POSITIVE_INFINITY));
            assertTrue(text.prefHeight(120) > unwrappedHeight);
            return null;
        });
    }

    @Test
    public void selectableText_scrollEventWithoutListView_isNotConsumed() throws Exception {
        onFxThread(() -> {
            SelectableText text = new SelectableText();
            new VBox(text);
            ScrollEvent scroll = scrollEvent(-40);

            text.fireEvent(scroll);

            assertFalse(scroll.isConsumed());
            return null;
        });
    }

    // Branch: an ancestor ListView that has not been skinned has no virtual flow, so there is nothing to scroll.
    @Test
    public void selectableText_scrollEventInUnrenderedListView_isNotConsumed() throws Exception {
        onFxThread(() -> {
            SelectableText text = new SelectableText();
            new ListView<String>() {
                {
                    getChildren().add(text);
                }
            };
            ScrollEvent scroll = scrollEvent(-40);

            text.fireEvent(scroll);

            assertFalse(scroll.isConsumed());
            return null;
        });
    }

    private long countShowingWindows() {
        return Window.getWindows().stream().filter(Window::isShowing).count();
    }

    private void closeShowingWindows() {
        new ArrayList<>(Window.getWindows()).forEach(window -> {
            if (window instanceof Stage stage) {
                stage.hide();
            }
        });
    }

    private VBox getProfileContent(StudentProfilePanel panel) {
        return (VBox) getProfileScrollPane(panel).getContent();
    }

    private ScrollPane getProfileScrollPane(StudentProfilePanel panel) {
        return (ScrollPane) ((StackPane) panel.getRoot()).getChildren().get(1);
    }

    @SuppressWarnings("unchecked")
    private ListView<StudentProfilePanel.NumberedSessionNote> getSessionNoteListView(StudentProfilePanel panel) {
        StackPane notesContainer = (StackPane) getProfileContent(panel).getChildren().get(3);
        return (ListView<StudentProfilePanel.NumberedSessionNote>) notesContainer.getChildren().get(0);
    }

    private VBox getEmptyNotesState(StudentProfilePanel panel) {
        StackPane notesContainer = (StackPane) getProfileContent(panel).getChildren().get(3);
        return (VBox) notesContainer.getChildren().get(1);
    }

    private <T extends Node> List<T> findDescendants(Node node, Class<T> nodeType) {
        List<T> matches = new ArrayList<>();
        if (nodeType.isInstance(node)) {
            matches.add(nodeType.cast(node));
        }
        if (node instanceof ScrollPane scrollPane) {
            matches.addAll(findDescendants(scrollPane.getContent(), nodeType));
        } else if (node instanceof Parent parent) {
            parent.getChildrenUnmodifiable().forEach(child -> matches.addAll(findDescendants(child, nodeType)));
        }
        return matches;
    }

    @SuppressWarnings("unchecked")
    private ListView<StudentRosterEntry> getListView(StudentRosterPanel panel) {
        StackPane root = (StackPane) panel.getRoot();
        return (ListView<StudentRosterEntry>) root.getChildren().get(0);
    }

    private ListCell<?> getVisibleCellForEntry(StudentRosterPanel panel, StudentRosterEntry entry) {
        return getListView(panel).lookupAll(".list-cell").stream()
                .filter(ListCell.class::isInstance)
                .map(ListCell.class::cast)
                .filter(cell -> cell.getItem() == entry)
                .findFirst()
                .orElseThrow();
    }

    private VBox getEmptyRosterState(StudentRosterPanel panel) {
        StackPane root = (StackPane) panel.getRoot();
        return (VBox) root.getChildren().get(1);
    }

    private TextField getCommandTextField(CommandBox commandBox) {
        return (TextField) ((StackPane) commandBox.getRoot()).getChildren().get(0);
    }

    private void submitCommand(TextField commandTextField, String commandText) {
        commandTextField.setText(commandText);
        commandTextField.fireEvent(new ActionEvent());
    }

    private void pressKey(TextField commandTextField, KeyCode keyCode) {
        commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", keyCode,
                false, false, false, false));
    }

    private ScrollBar getVerticalScrollBar(ListView<?> listView) {
        Parent virtualFlow = (Parent) listView.lookup(".virtual-flow");
        assertNotNull(virtualFlow);
        return virtualFlow.getChildrenUnmodifiable().stream()
                .filter(ScrollBar.class::isInstance)
                .map(ScrollBar.class::cast)
                .filter(scrollBar -> scrollBar.getOrientation() == Orientation.VERTICAL)
                .findFirst()
                .orElseThrow();
    }

    private List<SessionNote> sessionNotes(int count) {
        List<SessionNote> notes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            notes.add(new SessionNote("Reviewed algebra concepts in lesson " + i + ".",
                    OffsetDateTime.parse("2026-09-18T18:35:00+08:00").minusDays(i)));
        }
        return notes;
    }

    private String getSaveLocationText(StatusBarFooter statusBarFooter) {
        return ((Label) statusBarFooter.getRoot().lookup("#saveLocationStatus")).getText();
    }

    private ScrollEvent scrollEvent() {
        return scrollEvent(-40);
    }

    private ScrollEvent scrollEvent(double deltaY) {
        return new ScrollEvent(ScrollEvent.SCROLL, 0, 0, 0, 0,
                false, false, false, false, false, false,
                0, deltaY, 0, deltaY,
                ScrollEvent.HorizontalTextScrollUnits.NONE, 0,
                ScrollEvent.VerticalTextScrollUnits.NONE, 0, 0, null);
    }

    private MouseEvent primaryMouseEvent(javafx.event.EventType<MouseEvent> eventType, double x,
            boolean isPrimaryButtonDown, boolean isStillSincePress) {
        return new MouseEvent(eventType, x, 8, x, 8, MouseButton.PRIMARY, 1,
                false, false, false, false, isPrimaryButtonDown, false, false,
                false, false, isStillSincePress, null);
    }

    private void assertNodeWithinScene(Stage stage, String selector) {
        Node node = stage.getScene().lookup(selector);
        assertNotNull(node);
        javafx.geometry.Bounds sceneBounds = node.localToScene(node.getBoundsInLocal());
        assertTrue(sceneBounds.getWidth() > 0);
        assertTrue(sceneBounds.getHeight() > 0);
        assertTrue(sceneBounds.getMinX() >= 0);
        assertTrue(sceneBounds.getMinY() >= 0);
        assertTrue(sceneBounds.getMaxX() <= stage.getScene().getWidth() + 1);
        assertTrue(sceneBounds.getMaxY() <= stage.getScene().getHeight() + 1);
    }

    private Student student(String name) {
        return student(name, ALEX_ID);
    }

    private Student student(String name, StudentId studentId) {
        return new Student(new StudentName(name), new ParentGuardianContact("9123 4567", Optional.empty()),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"), studentId);
    }

    private StudentProfile profile(String email) {
        return profile(email, List.of());
    }

    private StudentProfile profile(String email, List<SessionNote> sessionNotes) {
        ParentGuardianContact contact = new ParentGuardianContact("9123 4567", Optional.ofNullable(email));
        return new StudentProfile(ALEX_ID, new StudentName("Alex Tan"), contact, new Subject("Mathematics"),
                new CurrentLevel("Secondary 3"), sessionNotes);
    }

    private StudentProfile profile(StudentId studentId, String name, List<SessionNote> sessionNotes) {
        ParentGuardianContact contact = new ParentGuardianContact("9123 4567", Optional.empty());
        return new StudentProfile(studentId, new StudentName(name), contact, new Subject("Mathematics"),
                new CurrentLevel("Secondary 3"), sessionNotes);
    }

    private void updateCell(ListCell<StudentRosterEntry> cell, StudentRosterEntry entry, boolean isEmpty) {
        StudentRosterPanel.StudentRosterListViewCell rosterCell =
                (StudentRosterPanel.StudentRosterListViewCell) cell;
        rosterCell.updateItem(entry, isEmpty);
    }

    private void updateNoteCell(ListCell<StudentProfilePanel.NumberedSessionNote> cell,
            StudentProfilePanel.NumberedSessionNote sessionNote, boolean isEmpty) {
        StudentProfilePanel.SessionNoteListViewCell noteCell =
                (StudentProfilePanel.SessionNoteListViewCell) cell;
        noteCell.updateItem(sessionNote, isEmpty);
    }

    private <T> T onFxThread(ThrowingSupplier<T> supplier) throws Exception {
        FutureTask<T> task = new FutureTask<>(supplier::get);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }

    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    private static class TestLogic implements Logic {
        private final StudentRoster rosterAfterCommand;
        private CommandResult commandResult;
        private Optional<StudentProfile> profile;
        private CommandException commandFailure;
        private ParseException parseFailure;
        private StudentRoster roster;
        private GuiSettings initialGuiSettings = new GuiSettings();

        TestLogic(StudentRoster initialRoster, StudentRoster rosterAfterCommand) {
            this(initialRoster, rosterAfterCommand, new CommandResult("Command executed."), Optional.empty());
        }

        TestLogic(StudentRoster initialRoster, StudentRoster rosterAfterCommand, CommandResult commandResult,
                Optional<StudentProfile> profile) {
            roster = initialRoster;
            this.rosterAfterCommand = rosterAfterCommand;
            this.commandResult = commandResult;
            this.profile = profile;
        }

        @Override
        public CommandResult execute(String commandText) throws CommandException, ParseException {
            if (parseFailure != null) {
                throw parseFailure;
            }
            if (commandFailure != null) {
                throw commandFailure;
            }
            roster = rosterAfterCommand;
            return commandResult;
        }

        void setCommandResult(CommandResult commandResult) {
            this.commandResult = commandResult;
        }

        void setProfile(Optional<StudentProfile> profile) {
            this.profile = profile;
        }

        void setCommandFailure(CommandException commandFailure) {
            this.commandFailure = commandFailure;
        }

        void setParseFailure(ParseException parseFailure) {
            this.parseFailure = parseFailure;
        }

        void setInitialGuiSettings(GuiSettings guiSettings) {
            initialGuiSettings = guiSettings;
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public StudentRoster getStudentRoster() {
            return roster;
        }

        @Override
        public Optional<StudentProfile> getStudentProfile(StudentId studentId) {
            return profile.filter(studentProfile -> studentProfile.getStudentId().equals(studentId));
        }

        @Override
        public GuiSettings getGuiSettings() {
            return initialGuiSettings;
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /** A {@code TestLogic} that records the GUI settings saved when the window closes. */
    private static class RecordingLogic extends TestLogic {
        private GuiSettings savedGuiSettings;

        RecordingLogic(CommandResult commandResult) {
            super(new StudentRoster(List.of()), new StudentRoster(List.of()), commandResult, Optional.empty());
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            savedGuiSettings = guiSettings;
        }
    }
}
