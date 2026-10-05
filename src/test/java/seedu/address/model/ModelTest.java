package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.Subject;

public class ModelTest {

    @Test
    public void defaultGetStudentRoster_returnsStudentsFromAddressBook() {
        Student student = new Student(new StudentName("Alex Tan"),
                new ParentGuardianContact("9123 4567", Optional.empty()), new Subject("Mathematics"),
                new CurrentLevel("Secondary 3"));
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(student);
        Model model = new ModelStub(addressBook);

        StudentRoster roster = model.getStudentRoster();

        assertEquals(List.of(student.getId()), roster.getEntries().stream()
                .map(entry -> entry.getStudentId()).toList());
        assertEquals(0, roster.getEntries().get(0).getNoteCount());
        assertThrows(UnsupportedOperationException.class, () -> roster.getEntries().clear());
    }

    @Test
    public void defaultGetStudentRoster_withSessionNotes_reportsNoteCounts() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        Model model = new ModelStub(addressBook);

        assertEquals(1, model.getStudentRoster().getEntries().get(0).getNoteCount());
    }

    @Test
    public void defaultGetSessionHistory_returnsHistoryFromAddressBook() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        Model model = new ModelStub(addressBook);

        assertEquals(List.of(INDICES_NOTE), model.getSessionHistory(ALEX.getId()).getNotes());
    }

    @Test
    public void defaultSessionNoteMutations_throwUnsupportedOperationException() {
        Model model = new ModelStub(new AddressBook());

        assertThrows(UnsupportedOperationException.class, () -> model.addSessionNote(ALEX.getId(),
                "Reviewed indices."));
        assertThrows(UnsupportedOperationException.class, () -> model.removeSessionHistory(ALEX.getId()));
    }

    private static class ModelStub implements Model {
        private final ReadOnlyAddressBook addressBook;

        ModelStub(ReadOnlyAddressBook addressBook) {
            this.addressBook = addressBook;
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return addressBook;
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook addressBook) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }
}
