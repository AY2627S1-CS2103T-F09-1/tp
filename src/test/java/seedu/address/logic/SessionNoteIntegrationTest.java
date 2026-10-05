package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
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

public class SessionNoteIntegrationTest {

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
    public void addNotesAndReload_notesAndCountsPersist() throws Exception {
        assertEquals("Added session note for Ari Tan.",
                logic.execute("note 1 nt/Reviewed indices.").getFeedbackToUser());
        logic.execute("note 1 nt/Reviewed factorisation.");

        ReadOnlyAddressBook persistedData = storage.readAddressBook().orElseThrow();
        ModelManager reloadedModel = new ModelManager(persistedData, new UserPrefs());

        List<StudentRosterEntry> entries = reloadedModel.getStudentRoster().getEntries();
        assertEquals(List.of(2, 0), entries.stream().map(StudentRosterEntry::getNoteCount).toList());
        StudentId ariId = entries.get(0).getStudentId();
        List<String> reloadedTexts = reloadedModel.getSessionHistory(ariId).getNotes().stream()
                .map(SessionNote::getText).toList();
        // Both notes share the fixed clock's timestamp, so they keep the order in which they were added.
        assertEquals(List.of("Reviewed indices.", "Reviewed factorisation."), reloadedTexts);
        assertEquals(model.getSessionHistory(ariId), reloadedModel.getSessionHistory(ariId));
        assertTrue(Files.readString(dataFile).contains("\"recordedAt\" : \"2026-09-18T18:35:00+08:00\""));
    }

    @Test
    public void invalidNoteCommands_leaveDataFileUnchanged() throws Exception {
        logic.execute("note 2 nt/Reviewed essay structure.");
        String savedData = Files.readString(dataFile);

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX, () -> logic
                .execute("note 3 nt/Reviewed indices."));
        assertThrows(ParseException.class, SessionNote.MESSAGE_CONSTRAINTS, () -> logic
                .execute("note 1 nt/   "));

        assertEquals(savedData, Files.readString(dataFile));
        assertEquals(List.of(0, 1), model.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getNoteCount).toList());
    }
}
