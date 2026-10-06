package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class StudentDeletionIntegrationTest {

    @TempDir
    public Path testFolder;

    private Path dataFile;
    private ModelManager model;
    private StorageManager storage;
    private LogicManager logic;

    @BeforeEach
    public void setUp() throws Exception {
        dataFile = testFolder.resolve("addressbook.json");
        model = new ModelManager();
        storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("userprefs.json")));
        logic = new LogicManager(model, storage);
        logic.execute("add n/Bea Tan p/9123 4567 sub/English l/JC 1");
        logic.execute("add n/Ari Tan p/9876 5432 sub/Mathematics l/Secondary 3");
    }

    @Test
    public void addListViewNoteDeleteAndReload_removesStudentAndSessionHistory() throws Exception {
        assertEquals("Listed 2 students.", logic.execute("list").getFeedbackToUser());
        CommandResult viewResult = logic.execute("view 1");
        StudentId deletedStudentId = viewResult.getProfileStudentId().orElseThrow();
        logic.execute("note 1 nt/Reviewed indices.");
        logic.execute("note 1 nt/Reviewed factorisation.");

        CommandResult deleteResult = logic.execute("delete 1");

        assertEquals("Deleted student: Ari Tan. Session notes removed: 2.", deleteResult.getFeedbackToUser());
        assertTrue(model.getStudentProfile(deletedStudentId).isEmpty());
        assertRemainingStudentIsReindexed(model.getStudentRoster().getEntries());

        ReadOnlyAddressBook persistedData = storage.readAddressBook().orElseThrow();
        ModelManager reloadedModel = new ModelManager(persistedData, new UserPrefs());
        assertTrue(reloadedModel.getStudentProfile(deletedStudentId).isEmpty());
        assertTrue(reloadedModel.getSessionHistory(deletedStudentId).isEmpty());
        assertRemainingStudentIsReindexed(reloadedModel.getStudentRoster().getEntries());
    }

    @Test
    public void invalidDeleteCommands_leaveModelAndDataFileUnchanged() throws Exception {
        String savedData = Files.readString(dataFile);
        List<StudentRosterEntry> originalEntries = model.getStudentRoster().getEntries();

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () ->
                logic.execute("delete 3"));
        assertThrows(ParseException.class, () -> logic.execute("delete 1 2"));

        assertEquals(originalEntries, model.getStudentRoster().getEntries());
        assertEquals(savedData, Files.readString(dataFile));
    }

    private void assertRemainingStudentIsReindexed(List<StudentRosterEntry> entries) {
        assertEquals(List.of("Bea Tan"), entries.stream()
                .map(entry -> entry.getName().getValue()).toList());
        assertEquals(List.of(1), entries.stream()
                .map(StudentRosterEntry::getRosterIndex).toList());
        assertEquals(List.of(0), entries.stream()
                .map(StudentRosterEntry::getNoteCount).toList());
    }
}
