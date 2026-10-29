package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.LogRecorder;

public class MainAppTest {

    @TempDir
    public Path testFolder;

    @Test
    public void initModelManager_missingDataFile_startsWithEmptyAddressBook() {
        Storage storage = createStorage();

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertTrue(model.getAddressBook().getPersonList().isEmpty());
        assertTrue(model.getAddressBook().getStudentList().isEmpty());
    }

    @Test
    public void initModelManager_existingDataFile_loadsPersistedData() throws Exception {
        Storage storage = createStorage();
        AddressBook persistedData = new AddressBook();
        persistedData.addPerson(ALICE);
        persistedData.addStudent(ALEX);
        storage.saveAddressBook(persistedData);

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertEquals(persistedData, new AddressBook(model.getAddressBook()));
    }

    @Test
    public void getDataFilePath_default_returnsTutorTrackDataFile() {
        assertEquals(Paths.get("data", "tutortrack.json"), new MainApp().getDataFilePath());
    }

    @Test
    public void getUserPrefsFilePath_default_returnsPreferencesFile() {
        assertEquals(Paths.get("preferences.json"), new MainApp().getUserPrefsFilePath());
    }

    @Test
    public void init_filesInTestFolder_logsInitializationAndUsesDataFile() throws Exception {
        Path dataFilePath = testFolder.resolve("tutortrack.json");
        MainApp app = new MainApp() {
            @Override
            protected Path getUserPrefsFilePath() {
                return testFolder.resolve("preferences.json");
            }

            @Override
            protected Path getDataFilePath() {
                return dataFilePath;
            }
        };

        List<String> logMessages = LogRecorder.captureMessages(MainApp.class, app::init);

        assertTrue(logMessages.stream().anyMatch(message -> message.contains("[ Initializing TutorTrack ]")));
        assertEquals(dataFilePath, app.storage.getAddressBookFilePath());
        assertTrue(app.model.getAddressBook().getStudentList().isEmpty());
    }

    @Test
    public void initModelManager_invalidDataFile_logsEmptyRosterWarning() throws Exception {
        Storage storage = createStorage();
        Files.writeString(storage.getAddressBookFilePath(), "not valid json");

        List<String> logMessages = LogRecorder.captureMessages(MainApp.class, () ->
                new MainApp().initModelManager(storage, new UserPrefs()));

        assertTrue(logMessages.stream()
                .anyMatch(message -> message.endsWith("Will be starting with an empty roster.")));
    }

    @Test
    public void initModelManager_invalidLegacyPerson_preservesFileInBackup() throws Exception {
        Storage storage = createStorage();
        String invalidData = "{\"persons\":[{\"name\":\"\",\"phone\":\"123\","
                + "\"email\":\"a@example.com\",\"address\":\"Example\",\"tags\":[]}],\"students\":[]}";
        Files.writeString(storage.getAddressBookFilePath(), invalidData);

        Model model = new MainApp().initModelManager(storage, new UserPrefs());

        assertTrue(model.getAddressBook().getStudentList().isEmpty());
        assertEquals(invalidData, Files.readString(storage.getAddressBookFilePath()));
        try (var files = Files.list(testFolder)) {
            List<Path> backups = files.filter(path -> path.getFileName().toString()
                    .startsWith("addressbook.json.backup-")).toList();
            assertEquals(1, backups.size());
            assertEquals(invalidData, Files.readString(backups.get(0)));
        }
    }

    @Test
    public void start_validUi_logsVersionAndStartsUi() {
        MainApp app = new MainApp();
        boolean[] isUiStarted = {false};
        app.ui = primaryStage -> isUiStarted[0] = true;

        List<String> logMessages = LogRecorder.captureMessages(MainApp.class, () -> app.start(null));

        assertTrue(logMessages.contains("Starting TutorTrack " + MainApp.VERSION));
        assertTrue(isUiStarted[0]);
    }

    @Test
    public void stop_validModel_logsStopAndSavesUserPrefs() throws Exception {
        MainApp app = new MainApp();
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(800, 600, 10, 20));
        app.storage = createStorage();
        app.model = new ModelManager(new AddressBook(), userPrefs);

        List<String> logMessages = LogRecorder.captureMessages(MainApp.class, app::stop);

        assertTrue(logMessages.stream().anyMatch(message -> message.contains("[ Stopping TutorTrack ]")));
        assertEquals(Optional.of(userPrefs), app.storage.readUserPrefs());
    }

    @Test
    public void initModelManager_invalidDataFileAndBackupFails_logsBackupFailureAndStartsEmpty() throws Exception {
        JsonAddressBookStorage unreadableAndUnbackableStorage =
                new JsonAddressBookStorage(testFolder.resolve("addressbook.json")) {
                    @Override
                    public Optional<ReadOnlyAddressBook> readAddressBook() throws DataLoadingException {
                        throw new DataLoadingException(new IOException("unreadable"));
                    }

                    @Override
                    public Path backupAddressBookFile() throws IOException {
                        throw new IOException("backup failed");
                    }
                };
        Storage storage = new StorageManager(unreadableAndUnbackableStorage,
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        MainApp app = new MainApp();

        List<String> logMessages = LogRecorder.captureMessages(MainApp.class, () ->
                assertTrue(app.initModelManager(storage, new UserPrefs()).getAddressBook()
                        .getStudentList().isEmpty()));

        assertTrue(logMessages.stream().anyMatch(message -> message.startsWith("Failed to back up data file")));
    }

    private Storage createStorage() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(testFolder.resolve("preferences.json"));
        return new StorageManager(addressBookStorage, userPrefsStorage);
    }
}
