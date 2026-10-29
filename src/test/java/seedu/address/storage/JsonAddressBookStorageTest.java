package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;

public class JsonAddressBookStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonAddressBookStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readAddressBook(null));
    }

    private java.util.Optional<ReadOnlyAddressBook> readAddressBook(String filePath) throws Exception {
        return new JsonAddressBookStorage(Paths.get(filePath)).readAddressBook(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readAddressBook("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("notJsonFormatAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidPersonAddressBook.json"));
    }

    @Test
    public void readAddressBook_invalidAndValidPersonAddressBook_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readAddressBook("invalidAndValidPersonAddressBook.json"));
    }

    @Test
    public void backupAddressBookFile_existingFile_createsUniqueBackup() throws Exception {
        Path dataFile = testFolder.resolve("addressbook.json");
        String data = "invalid data";
        Files.writeString(dataFile, data);

        Path backupFile = new JsonAddressBookStorage(dataFile).backupAddressBookFile();

        assertEquals(dataFile.getParent(), backupFile.getParent());
        assertTrue(backupFile.getFileName().toString().startsWith("addressbook.json.backup-"));
        assertTrue(backupFile.getFileName().toString().endsWith(".bak"));
        assertEquals(data, Files.readString(backupFile));
    }

    @Test
    public void backupAddressBookFile_existingBackup_createsAnotherBackup() throws Exception {
        Path dataFile = testFolder.resolve("addressbook.json");
        Files.writeString(dataFile, "first invalid data");
        Path firstBackupFile = new JsonAddressBookStorage(dataFile).backupAddressBookFile();

        Files.writeString(dataFile, "second invalid data");
        Path secondBackupFile = new JsonAddressBookStorage(dataFile).backupAddressBookFile();

        assertNotEquals(firstBackupFile, secondBackupFile);
        assertEquals("first invalid data", Files.readString(firstBackupFile));
        assertEquals("second invalid data", Files.readString(secondBackupFile));
    }

    @Test
    public void backupAddressBookFile_missingFile_throwsIoExceptionWithoutLeavingBackup() throws Exception {
        Path missingDataFile = testFolder.resolve("missing-addressbook.json");

        assertThrows(IOException.class, () -> new JsonAddressBookStorage(missingDataFile).backupAddressBookFile());

        try (Stream<Path> files = Files.list(testFolder)) {
            assertTrue(files.findAny().isEmpty());
        }
    }

    @Test
    public void deleteIncompleteBackup_deletableFile_removesFileWithoutSuppressedException() throws Exception {
        Path backupFile = Files.createFile(testFolder.resolve("addressbook.json.backup-1.bak"));
        IOException copyException = new IOException("copy failed");

        JsonAddressBookStorage.deleteIncompleteBackup(backupFile, copyException);

        assertFalse(Files.exists(backupFile));
        assertEquals(0, copyException.getSuppressed().length);
    }

    @Test
    public void deleteIncompleteBackup_undeletablePath_attachesDeleteFailureToCopyException() throws Exception {
        // A non-empty directory cannot be deleted by Files#deleteIfExists on any platform.
        Path undeletableDirectory = Files.createDirectory(testFolder.resolve("addressbook.json.backup-2.bak"));
        Files.createFile(undeletableDirectory.resolve("child"));
        IOException copyException = new IOException("copy failed");

        JsonAddressBookStorage.deleteIncompleteBackup(undeletableDirectory, copyException);

        assertEquals(1, copyException.getSuppressed().length);
        assertTrue(copyException.getSuppressed()[0] instanceof IOException);
        assertEquals("copy failed", copyException.getMessage());
    }

    @Test
    public void readAndSaveAddressBook_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = getTypicalAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveAddressBook(original, filePath);
        readBack = jsonAddressBookStorage.readAddressBook(filePath).get();
        assertEquals(original, new AddressBook(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveAddressBook(original); // file path not specified
        readBack = jsonAddressBookStorage.readAddressBook().get(); // file path not specified
        assertEquals(original, new AddressBook(readBack));

    }

    @Test
    public void readAndSaveAddressBook_withSessionNotes_keepsNotes() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json");
        AddressBook original = JsonSerializableAddressBookTest.getTypicalSessionNotesAddressBook();
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();

        assertEquals(original, new AddressBook(readBack));
        assertEquals(original.getSessionHistory(ALEX.getId()), readBack.getSessionHistory(ALEX.getId()));
        assertTrue(Files.readString(filePath).contains("\"recordedAt\" : \"2026-09-18T18:35:00+08:00\""));
    }

    @Test
    public void readAndSaveAddressBook_withEditedSessionNote_keepsLastEditedAt() throws Exception {
        Path filePath = testFolder.resolve("EditedSessionNoteAddressBook.json");
        AddressBook original = new AddressBook();
        original.addStudent(ALEX);
        SessionNote editedNote = new SessionNote("Corrected summary.",
                OffsetDateTime.parse("2026-09-18T18:35:00+08:00"),
                OffsetDateTime.parse("2026-10-23T14:10:00+08:00"));
        original.setSessionHistory(ALEX.getId(), SessionHistory.of(List.of(editedNote)));
        JsonAddressBookStorage jsonAddressBookStorage = new JsonAddressBookStorage(filePath);

        jsonAddressBookStorage.saveAddressBook(original, filePath);
        ReadOnlyAddressBook readBack = jsonAddressBookStorage.readAddressBook(filePath).get();

        assertEquals(editedNote, readBack.getSessionHistory(ALEX.getId()).getNote(Index.fromOneBased(1)));
    }

    @Test
    public void readAndSave_mixedLegacyAndStudentData_preservesDataAndPersonOrder() throws Exception {
        Path filePath = testFolder.resolve("mixed.json");
        AddressBook original = new AddressBook();
        original.addPerson(ALICE);
        original.addPerson(BENSON);
        original.addStudent(ALEX);
        original.addSessionNote(ALEX.getId(), INDICES_NOTE);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);

        storage.saveAddressBook(original);
        ReadOnlyAddressBook loaded = storage.readAddressBook().orElseThrow();
        storage.saveAddressBook(loaded);
        ReadOnlyAddressBook reloaded = storage.readAddressBook().orElseThrow();

        assertEquals(original, new AddressBook(reloaded));
        assertEquals(original.getPersonList(), reloaded.getPersonList());
        assertEquals(original.getSessionHistory(ALEX.getId()), reloaded.getSessionHistory(ALEX.getId()));
        JsonNode savedJson = new ObjectMapper().readTree(Files.readString(filePath));
        assertEquals(2, savedJson.get("persons").size());
        assertEquals(ALICE.getName().fullName, savedJson.get("persons").get(0).get("name").asText());
        assertEquals(BENSON.getName().fullName, savedJson.get("persons").get(1).get("name").asText());
    }

    @Test
    public void readAndSave_emptyLegacyList_keepsPersonsField() throws Exception {
        Path filePath = testFolder.resolve("empty.json");
        JsonAddressBookStorage storage = new JsonAddressBookStorage(filePath);
        Files.writeString(filePath, "{\"persons\": [], \"students\": [], \"unknown\": true}");

        ReadOnlyAddressBook loaded = storage.readAddressBook().orElseThrow();
        storage.saveAddressBook(loaded);

        JsonNode savedJson = new ObjectMapper().readTree(Files.readString(filePath));
        assertTrue(savedJson.get("persons").isArray());
        assertEquals(0, savedJson.get("persons").size());
        assertTrue(storage.readAddressBook().orElseThrow().getStudentList().isEmpty());
    }

    @Test
    public void saveAddressBook_nullAddressBook_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(null, "SomeFile.json"));
    }

    /**
     * Saves {@code addressBook} at the specified {@code filePath}.
     */
    private void saveAddressBook(ReadOnlyAddressBook addressBook, String filePath) {
        try {
            new JsonAddressBookStorage(Paths.get(filePath))
                    .saveAddressBook(addressBook, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveAddressBook_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveAddressBook(new AddressBook(), null));
    }

    @Test
    public void saveAddressBook_missingParentDirectory_createsDataFile() throws Exception {
        Path dataFile = testFolder.resolve("new-directory").resolve("addressbook.json");

        new JsonAddressBookStorage(dataFile).saveAddressBook(new AddressBook());

        assertTrue(Files.isRegularFile(dataFile));
    }
}
