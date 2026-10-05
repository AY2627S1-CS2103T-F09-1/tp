package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;
import seedu.address.testutil.AddressBookBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void getStudentRoster_returnsImmutableStudentSnapshot() {
        Student student = new Student(new StudentName("Alex Tan"),
                new ParentGuardianContact("9123 4567", java.util.Optional.empty()), new Subject("Mathematics"),
                new CurrentLevel("Secondary 3"));
        modelManager.addStudent(student);

        assertEquals(List.of(student.getId()), modelManager.getStudentRoster().getEntries().stream()
                .map(entry -> entry.getStudentId()).toList());
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getStudentRoster().getEntries().clear());
    }

    @Test
    public void constructor_nullClock_throwsNullPointerException() {
        AddressBook addressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        assertThrows(NullPointerException.class, () -> new ModelManager(addressBook, userPrefs, null));
    }

    @Test
    public void addSessionNote_validText_timestampsWithClockToWholeSeconds() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-18T10:35:00.123456Z"), ZoneOffset.ofHours(8));
        ModelManager model = new ModelManager(new AddressBook(), new UserPrefs(), clock);
        model.addStudent(ALEX);

        SessionNote note = model.addSessionNote(ALEX.getId(), "  Reviewed factorisation.  ");

        assertEquals("Reviewed factorisation.", note.getText());
        assertEquals(OffsetDateTime.parse("2026-09-18T18:35:00+08:00"), note.getRecordedAt());
        assertEquals(List.of(note), model.getSessionHistory(ALEX.getId()).getNotes());
    }

    @Test
    public void addSessionNote_invalidText_throwsIllegalArgumentExceptionAndKeepsHistory() {
        modelManager.addStudent(ALEX);

        assertThrows(IllegalArgumentException.class, SessionNote.MESSAGE_CONSTRAINTS, () -> modelManager
                .addSessionNote(ALEX.getId(), "Line one\nLine two"));
        assertTrue(modelManager.getSessionHistory(ALEX.getId()).isEmpty());
    }

    @Test
    public void addSessionNote_studentNotInRoster_throwsIllegalArgumentException() {
        modelManager.addStudent(ALEX);

        assertThrows(IllegalArgumentException.class, () -> modelManager.addSessionNote(BEA.getId(),
                "Reviewed factorisation."));
    }

    @Test
    public void addSessionNote_nullArguments_throwsNullPointerException() {
        String text = "Reviewed indices.";

        assertThrows(NullPointerException.class, () -> modelManager.addSessionNote(null, text));
        assertThrows(NullPointerException.class, () -> modelManager.addSessionNote(ALEX.getId(), null));
    }

    @Test
    public void getStudentRoster_withSessionNotes_reportsNoteCounts() {
        modelManager.addStudent(ALEX);
        modelManager.addStudent(BEA);
        modelManager.addSessionNote(ALEX.getId(), "Reviewed indices.");
        modelManager.addSessionNote(ALEX.getId(), "Reviewed factorisation.");

        List<Integer> noteCounts = modelManager.getStudentRoster().getEntries().stream()
                .map(entry -> entry.getNoteCount()).toList();

        assertEquals(List.of(2, 0), noteCounts);
    }

    @Test
    public void removeSessionHistory_studentWithNotes_returnsCountAndResetsRosterCount() {
        modelManager.addStudent(ALEX);
        modelManager.addSessionNote(ALEX.getId(), "Reviewed indices.");

        assertEquals(1, modelManager.removeSessionHistory(ALEX.getId()));
        assertEquals(0, modelManager.getStudentRoster().getEntries().get(0).getNoteCount());
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }
}
