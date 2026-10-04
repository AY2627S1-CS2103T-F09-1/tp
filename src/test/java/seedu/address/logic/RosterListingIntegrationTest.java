package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class RosterListingIntegrationTest {

    @TempDir
    public Path testFolder;

    private ModelManager model;
    private StorageManager storage;
    private LogicManager logic;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        storage = new StorageManager(new JsonAddressBookStorage(testFolder.resolve("addressbook.json")),
                new JsonUserPrefsStorage(testFolder.resolve("userprefs.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void addListAndReload_studentsRemainSortedIndexedAndIdentified() throws Exception {
        logic.execute("add n/Bea Tan p/9123 4567 sub/English l/JC 1");
        logic.execute("add n/Ari Tan p/9876 5432 sub/Mathematics l/Secondary 3");

        List<StudentRosterEntry> beforeReload = model.getStudentRoster().getEntries();
        ReadOnlyAddressBook persistedData = storage.readAddressBook().orElseThrow();
        ModelManager reloadedModel = new ModelManager(persistedData, new UserPrefs());
        List<StudentRosterEntry> afterReload = reloadedModel.getStudentRoster().getEntries();

        assertEquals(List.of("Ari Tan", "Bea Tan"), afterReload.stream()
                .map(entry -> entry.getName().getValue()).toList());
        assertEquals(List.of(1, 2), afterReload.stream()
                .map(StudentRosterEntry::getRosterIndex).toList());
        assertEquals(beforeReload.stream().map(StudentRosterEntry::getStudentId).toList(),
                afterReload.stream().map(StudentRosterEntry::getStudentId).toList());
        assertEquals("Listed 2 students.", logic.execute("list").getFeedbackToUser());
    }

    @Test
    public void list_emptyPersistedRoster_reportsRecoveryMessage() throws Exception {
        assertEquals(ListCommand.MESSAGE_EMPTY_ROSTER, logic.execute("list").getFeedbackToUser());

        ReadOnlyAddressBook persistedData = storage.readAddressBook().orElseThrow();
        assertEquals(0, new ModelManager(persistedData, new UserPrefs()).getStudentRoster().size());
    }

    @Test
    public void rosterProjection_preservesNoteCountsForSessionContext() throws Exception {
        logic.execute("add n/Ari Tan p/9876 5432 sub/Mathematics l/Secondary 3");
        StudentId studentId = model.getStudentRoster().getEntries().get(0).getStudentId();

        StudentRoster roster = new StudentRoster(model.getAddressBook().getStudentList(), Map.of(studentId, 3));

        assertEquals(3, roster.getEntries().get(0).getNoteCount());
    }

    @Test
    public void legacyStudent_saveAfterReload_writesMigratedStudentId() throws Exception {
        Path filePath = testFolder.resolve("addressbook.json");
        Files.writeString(filePath, "{\n"
                + "  \"persons\": [],\n"
                + "  \"students\": [{\n"
                + "    \"name\": \"Legacy Learner\",\n"
                + "    \"parentPhone\": \"9123 4567\",\n"
                + "    \"subject\": \"Mathematics\",\n"
                + "    \"currentLevel\": \"Secondary 3\"\n"
                + "  }]\n"
                + "}");

        ReadOnlyAddressBook loadedData = storage.readAddressBook().orElseThrow();
        String migratedStudentId = loadedData.getStudentList().get(0).getId().getValue();
        storage.saveAddressBook(loadedData);

        String savedJson = Files.readString(filePath);
        assertTrue(savedJson.contains("\"studentId\" : \"" + migratedStudentId + "\""));
    }
}
