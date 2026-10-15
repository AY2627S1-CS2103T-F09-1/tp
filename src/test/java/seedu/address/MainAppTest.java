package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;

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

    private Storage createStorage() {
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(testFolder.resolve("addressbook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(testFolder.resolve("preferences.json"));
        return new StorageManager(addressBookStorage, userPrefsStorage);
    }
}
