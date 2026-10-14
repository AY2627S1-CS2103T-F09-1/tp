package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
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
    }

    @AfterAll
    public static void shutdownJavaFx() {
        Platform.exit();
    }

    @Test
    public void constructor_emptyRoster_showsEmptyMessage() throws Exception {
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of())));

        assertTrue(getListView(panel).getItems().isEmpty());
        assertTrue(getEmptyMessage(panel).isVisible());
        assertTrue(getEmptyMessage(panel).isManaged());
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
    public void constructor_populatedRoster_displaysEntriesAndHidesEmptyMessage() throws Exception {
        StudentRoster roster = new StudentRoster(List.of(student("Alex Tan")));

        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(roster));

        assertEquals(roster.getEntries(), getListView(panel).getItems());
        assertFalse(getEmptyMessage(panel).isVisible());
        assertFalse(getEmptyMessage(panel).isManaged());
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
        assertFalse(getEmptyMessage(panel).isVisible());
        assertFalse(getEmptyMessage(panel).isManaged());
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
        StudentRosterPanel panel = onFxThread(() -> new StudentRosterPanel(new StudentRoster(List.of(alex))));

        onFxThread(() -> {
            panel.selectStudent(ALEX_ID);
            panel.setRoster(new StudentRoster(List.of(alex, student("Aaron Koh", BLAIR_ID))));
            return null;
        });

        assertEquals(1, getListView(panel).getSelectionModel().getSelectedIndex());
        assertEquals(ALEX_ID, getListView(panel).getSelectionModel().getSelectedItem().getStudentId());
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

        assertEquals("2. ", ((Label) heading.getChildren().get(0)).getText());
        assertEquals("Alex Tan", ((Label) heading.getChildren().get(1)).getText());
        assertEquals("Subject: Mathematics", ((Label) details.getChildren().get(1)).getText());
        assertEquals("Level: Secondary 3", ((Label) details.getChildren().get(2)).getText());
        assertEquals("Session notes: 3", ((Label) details.getChildren().get(3)).getText());
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
        VBox profileContent = (VBox) root.getChildren().get(1);

        assertTrue(emptyState.isVisible());
        assertTrue(emptyState.isManaged());
        assertFalse(profileContent.isVisible());
        assertFalse(profileContent.isManaged());
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
        assertEquals("Alex Tan", ((Label) profileContent.getChildren().get(0)).getText());
        assertEquals("9123 4567", ((Label) details.getChildren().get(1)).getText());
        assertEquals("alex.parent@example.com", ((Label) details.getChildren().get(3)).getText());
        assertEquals("Mathematics", ((Label) details.getChildren().get(5)).getText());
        assertEquals("Secondary 3", ((Label) details.getChildren().get(7)).getText());
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
        assertEquals("Not provided", ((Label) details.getChildren().get(3)).getText());
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
    public void setProfile_sessionNotes_preservesNewestFirstOrderAndHidesEmptyMessage() throws Exception {
        SessionNote newestNote = new SessionNote("Plan next lesson.",
                OffsetDateTime.parse("2026-09-19T09:10:00+08:00"));
        SessionNote olderNote = new SessionNote("Reviewed factorisation.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null, List.of(newestNote, olderNote)));
            return null;
        });

        ListView<SessionNote> listView = getSessionNoteListView(panel);
        assertEquals(List.of(newestNote, olderNote), listView.getItems());
        assertFalse(getEmptyNotesMessage(panel).isVisible());
        assertFalse(getEmptyNotesMessage(panel).isManaged());
    }

    @Test
    public void setProfile_emptyHistory_showsEmptyNotesMessage() throws Exception {
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);

        onFxThread(() -> {
            panel.setProfile(profile(null));
            return null;
        });

        assertTrue(getSessionNoteListView(panel).getItems().isEmpty());
        assertEquals("No session notes recorded.", getEmptyNotesMessage(panel).getText());
        assertTrue(getEmptyNotesMessage(panel).isVisible());
        assertTrue(getEmptyNotesMessage(panel).isManaged());
    }

    @Test
    public void sessionNoteCard_note_displaysFriendlyOffsetTimestampAndWrappedText() throws Exception {
        SessionNote note = new SessionNote("A long note that should wrap instead of widening the profile panel.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));

        VBox card = (VBox) onFxThread(() -> new SessionNoteCard(note).getRoot());

        assertEquals("18 Sep 2026, 6:35 PM (UTC+08:00)", ((Label) card.getChildren().get(0)).getText());
        assertEquals(note.getText(), ((Label) card.getChildren().get(1)).getText());
        assertTrue(((Label) card.getChildren().get(1)).isWrapText());
        assertEquals("18 Sep 2026, 10:35 AM (UTC+00:00)", SessionNoteCard.formatRecordedAt(
                OffsetDateTime.parse("2026-09-18T10:35:00Z")));
        assertThrows(NullPointerException.class, () -> SessionNoteCard.formatRecordedAt(null));
    }

    @Test
    public void sessionNoteListCell_populatedAndEmptyNotes_updateGraphic() throws Exception {
        SessionNote note = new SessionNote("Reviewed indices.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"));
        StudentProfilePanel panel = onFxThread(StudentProfilePanel::new);
        ListView<SessionNote> listView = getSessionNoteListView(panel);
        ListCell<SessionNote> cell = onFxThread(() -> listView.getCellFactory().call(listView));

        onFxThread(() -> {
            updateNoteCell(cell, note, false);
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
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("invalid", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
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
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());

            pressHistoryKey(commandTextField, KeyCode.DOWN);
            commandTextField.setText("view 2");
            submitCommand(commandTextField, "view 2");
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("view 2", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
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

            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("view 1", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("list", commandTextField.getText());

            pressHistoryKey(commandTextField, KeyCode.DOWN);
            assertEquals("view 1", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.DOWN);
            assertEquals("unfinished command", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.DOWN);
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
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("draft", commandTextField.getText());

            for (int i = 0; i <= 100; i++) {
                submitCommand(commandTextField, "command " + i);
            }
            commandTextField.setText("new draft");
            for (int i = 0; i < 100; i++) {
                pressHistoryKey(commandTextField, KeyCode.UP);
            }
            assertEquals("command 1", commandTextField.getText());
            pressHistoryKey(commandTextField, KeyCode.UP);
            assertEquals("command 1", commandTextField.getText());
            for (int i = 0; i < 100; i++) {
                pressHistoryKey(commandTextField, KeyCode.DOWN);
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
            mainWindow.executeCommand("list");
            assertEquals(refreshedRoster.getEntries(), getListView(mainWindow.getStudentRosterPanel()).getItems());
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
            assertEquals("Alex Tan", ((Label) getProfileContent(mainWindow.getStudentProfilePanel())
                    .getChildren().get(0)).getText());
            assertEquals(ALEX_ID, getListView(mainWindow.getStudentRosterPanel())
                    .getSelectionModel().getSelectedItem().getStudentId());
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
            assertEquals(List.of(addedNote), getSessionNoteListView(mainWindow.getStudentProfilePanel()).getItems());
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
    public void executeCommand_deleteDisplayedStudent_clearsOpenProfile() throws Exception {
        StudentRoster initialRoster = new StudentRoster(List.of(student("Alex Tan")));
        StudentRoster emptyRoster = new StudentRoster(List.of());
        TestLogic logic = new TestLogic(initialRoster, emptyRoster,
                new CommandResult("Displaying profile for Alex Tan.", ALEX_ID), Optional.of(profile(null)));

        onFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("addressbook.json"));
            mainWindow.fillInnerParts();
            mainWindow.executeCommand("view 1");
            logic.setCommandResult(new CommandResult(
                    "Deleted student: Alex Tan. Session notes removed: 0."));
            logic.setProfile(Optional.empty());
            mainWindow.executeCommand("delete 1");
            StackPane root = (StackPane) mainWindow.getStudentProfilePanel().getRoot();
            assertTrue(root.getChildren().get(0).isVisible());
            assertFalse(root.getChildren().get(1).isVisible());
            assertTrue(getListView(mainWindow.getStudentRosterPanel()).getItems().isEmpty());
            assertTrue(getListView(mainWindow.getStudentRosterPanel()).getSelectionModel().isEmpty());
            return null;
        });
    }

    private VBox getProfileContent(StudentProfilePanel panel) {
        return (VBox) ((StackPane) panel.getRoot()).getChildren().get(1);
    }

    @SuppressWarnings("unchecked")
    private ListView<SessionNote> getSessionNoteListView(StudentProfilePanel panel) {
        StackPane notesContainer = (StackPane) getProfileContent(panel).getChildren().get(3);
        return (ListView<SessionNote>) notesContainer.getChildren().get(0);
    }

    private Label getEmptyNotesMessage(StudentProfilePanel panel) {
        StackPane notesContainer = (StackPane) getProfileContent(panel).getChildren().get(3);
        return (Label) notesContainer.getChildren().get(1);
    }

    @SuppressWarnings("unchecked")
    private ListView<StudentRosterEntry> getListView(StudentRosterPanel panel) {
        StackPane root = (StackPane) panel.getRoot();
        return (ListView<StudentRosterEntry>) root.getChildren().get(0);
    }

    private Label getEmptyMessage(StudentRosterPanel panel) {
        StackPane root = (StackPane) panel.getRoot();
        return (Label) root.getChildren().get(1);
    }

    private TextField getCommandTextField(CommandBox commandBox) {
        return (TextField) ((StackPane) commandBox.getRoot()).getChildren().get(0);
    }

    private void submitCommand(TextField commandTextField, String commandText) {
        commandTextField.setText(commandText);
        commandTextField.fireEvent(new ActionEvent());
    }

    private void pressHistoryKey(TextField commandTextField, KeyCode keyCode) {
        commandTextField.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", keyCode,
                false, false, false, false));
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

    private void updateCell(ListCell<StudentRosterEntry> cell, StudentRosterEntry entry, boolean isEmpty) {
        StudentRosterPanel.StudentRosterListViewCell rosterCell =
                (StudentRosterPanel.StudentRosterListViewCell) cell;
        rosterCell.updateItem(entry, isEmpty);
    }

    private void updateNoteCell(ListCell<SessionNote> cell, SessionNote sessionNote, boolean isEmpty) {
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
        private StudentRoster roster;

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
        public CommandResult execute(String commandText) throws CommandException {
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
            return new GuiSettings();
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }
    }
}
