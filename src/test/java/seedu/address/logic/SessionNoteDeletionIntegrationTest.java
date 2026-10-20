package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.CommandResult;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class SessionNoteDeletionIntegrationTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-09-18T10:35:00Z"), ZoneOffset.ofHours(8));

    @TempDir
    public Path testFolder;

    private Path dataFile;
    private ModelManager model;
    private StorageManager storage;
    private LogicManager logic;

    @BeforeEach
    public void setUp() throws Exception {
        dataFile = testFolder.resolve("addressbook.json");
        model = new ModelManager(new AddressBook(), new UserPrefs(), FIXED_CLOCK);
        storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(testFolder.resolve("userprefs.json")));
        logic = new LogicManager(model, storage);
        logic.execute("add n/Bea Tan p/9123 4567 sub/English l/JC 1");
        logic.execute("add n/Ari Tan p/9876 5432 sub/Mathematics l/Secondary 3");
    }

    @Test
    public void addViewDeleteMiddleNoteAndReload_preservesStudentAndOtherNotes() throws Exception {
        CommandResult viewResult = logic.execute("view 1");
        StudentId studentId = viewResult.getProfileStudentId().orElseThrow();
        logic.execute("note 1 nt/First session.");
        logic.execute("note 1 nt/Incorrect middle session.");
        logic.execute("note 1 nt/Third session.");

        CommandResult previewResult = logic.execute("delete note 1 2");
        assertEquals("Are you sure you want to delete session note [2] for Ari Tan?\n"
                + "If yes, type: delete note 1 2 /confirm\n"
                + "To cancel, enter any other command.", previewResult.getFeedbackToUser());
        assertEquals(3, model.getSessionHistory(studentId).size());

        CommandResult deleteResult = logic.execute("delete note 1 2 /confirm");

        assertEquals("Successfully deleted session note [2] for Ari Tan.", deleteResult.getFeedbackToUser());
        assertTrue(model.getStudentProfile(studentId).isPresent());
        assertEquals(List.of("Third session.", "First session."), model.getSessionHistory(studentId).getNotes()
                .stream().map(SessionNote::getText).toList());
        assertEquals(List.of(2, 0), model.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getNoteCount).toList());

        ReadOnlyAddressBook persistedData = storage.readAddressBook().orElseThrow();
        ModelManager reloadedModel = new ModelManager(persistedData, new UserPrefs());
        assertTrue(reloadedModel.getStudentProfile(studentId).isPresent());
        assertEquals(model.getSessionHistory(studentId), reloadedModel.getSessionHistory(studentId));
        assertFalse(Files.readString(dataFile).contains("noteNumber"));
    }
}
