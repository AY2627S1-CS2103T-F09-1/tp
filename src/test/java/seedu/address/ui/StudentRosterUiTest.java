package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.model.person.Person;
import seedu.address.model.profile.StudentProfile;
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

    @SuppressWarnings("unchecked")
    private ListView<StudentRosterEntry> getListView(StudentRosterPanel panel) {
        StackPane root = (StackPane) panel.getRoot();
        return (ListView<StudentRosterEntry>) root.getChildren().get(0);
    }

    private Label getEmptyMessage(StudentRosterPanel panel) {
        StackPane root = (StackPane) panel.getRoot();
        return (Label) root.getChildren().get(1);
    }

    private Student student(String name) {
        return new Student(new StudentName(name), new ParentGuardianContact("9123 4567", Optional.empty()),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"), ALEX_ID);
    }

    private void updateCell(ListCell<StudentRosterEntry> cell, StudentRosterEntry entry, boolean isEmpty) {
        StudentRosterPanel.StudentRosterListViewCell rosterCell =
                (StudentRosterPanel.StudentRosterListViewCell) cell;
        rosterCell.updateItem(entry, isEmpty);
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
        private StudentRoster roster;

        TestLogic(StudentRoster initialRoster, StudentRoster rosterAfterCommand) {
            roster = initialRoster;
            this.rosterAfterCommand = rosterAfterCommand;
        }

        @Override
        public CommandResult execute(String commandText) {
            roster = rosterAfterCommand;
            return new CommandResult("Command executed.");
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
            return Optional.empty();
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
