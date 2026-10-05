package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.session.SessionNote;
import seedu.address.testutil.TypicalPersons;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");
    private static final Path TYPICAL_SESSION_NOTES_FILE =
            TEST_DATA_FOLDER.resolve("typicalSessionNotesAddressBook.json");
    private static final Path INVALID_SESSION_NOTE_FILE =
            TEST_DATA_FOLDER.resolve("invalidSessionNoteAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_typicalSessionNotesFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_SESSION_NOTES_FILE,
                JsonSerializableAddressBook.class).get();

        assertEquals(getTypicalSessionNotesAddressBook(), dataFromFile.toModelType());
    }

    @Test
    public void toModelType_invalidSessionNoteFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_SESSION_NOTE_FILE,
                JsonSerializableAddressBook.class).get();

        assertThrows(IllegalValueException.class, JsonAdaptedSessionNote.INVALID_RECORDED_AT_MESSAGE,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_sessionNotesWithEqualTimestamps_keepsOrder() throws Exception {
        AddressBook original = new AddressBook();
        original.addStudent(ALEX);
        OffsetDateTime recordedAt = FACTORISATION_NOTE.getRecordedAt();
        SessionNote first = new SessionNote("Started algebra worksheet.", recordedAt);
        SessionNote second = new SessionNote("Finished algebra worksheet.", recordedAt);
        original.addSessionNote(ALEX.getId(), first);
        original.addSessionNote(ALEX.getId(), second);

        AddressBook reloaded = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(List.of(first, second), reloaded.getSessionHistory(ALEX.getId()).getNotes());
    }

    /**
     * Returns the address book that {@code typicalSessionNotesAddressBook.json} represents.
     */
    static AddressBook getTypicalSessionNotesAddressBook() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(ALEX);
        addressBook.addStudent(BEA);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        addressBook.addSessionNote(ALEX.getId(), FACTORISATION_NOTE);
        return addressBook;
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
