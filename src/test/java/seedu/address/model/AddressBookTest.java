package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void getSessionHistory_studentWithoutNotes_returnsEmptyHistory() {
        addressBook.addStudent(ALEX);

        assertEquals(SessionHistory.empty(), addressBook.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void addSessionNote_studentInRoster_addsNoteToHistory() {
        addressBook.addStudent(ALEX);

        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        addressBook.addSessionNote(ALEX.getId(), FACTORISATION_NOTE);

        List<SessionNote> expectedNotes = List.of(FACTORISATION_NOTE, INDICES_NOTE);
        assertEquals(expectedNotes, addressBook.getSessionHistory(ALEX.getId()).getNotes());
    }

    @Test
    public void addSessionNote_studentNotInRoster_throwsIllegalArgumentException() {
        addressBook.addStudent(ALEX);

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_UNKNOWN_STUDENT, () -> addressBook
                .addSessionNote(BEA.getId(), INDICES_NOTE));
        assertTrue(addressBook.getSessionHistory(BEA.getId()).isEmpty());
    }

    @Test
    public void sessionNoteMethods_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addSessionNote(null, INDICES_NOTE));
        assertThrows(NullPointerException.class, () -> addressBook.addSessionNote(ALEX.getId(), null));
        assertThrows(NullPointerException.class, () -> addressBook.getSessionHistory(null));
        assertThrows(NullPointerException.class, () -> addressBook.removeSessionHistory(null));
    }

    @Test
    public void removeSessionHistory_studentWithNotes_returnsRemovedCount() {
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        addressBook.addSessionNote(ALEX.getId(), FACTORISATION_NOTE);

        assertEquals(2, addressBook.removeSessionHistory(ALEX.getId()));
        assertTrue(addressBook.getSessionHistory(ALEX.getId()).isEmpty());
        assertEquals(0, addressBook.removeSessionHistory(ALEX.getId()));
    }

    @Test
    public void resetData_withSessionNotes_copiesSessionHistories() {
        AddressBook original = new AddressBook();
        original.addStudent(ALEX);
        original.addStudent(BEA);
        original.addSessionNote(ALEX.getId(), INDICES_NOTE);

        AddressBook copy = new AddressBook(original);

        assertEquals(List.of(INDICES_NOTE), copy.getSessionHistory(ALEX.getId()).getNotes());
        assertTrue(copy.getSessionHistory(BEA.getId()).isEmpty());
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
    }

    @Test
    public void resetData_withSessionNotes_replacesExistingHistories() {
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);

        addressBook.resetData(new AddressBook());

        assertTrue(addressBook.getSessionHistory(ALEX.getId()).isEmpty());
    }

    @Test
    public void equals_differentSessionNotes_returnsFalse() {
        AddressBook withNote = new AddressBook();
        withNote.addStudent(ALEX);
        withNote.addSessionNote(ALEX.getId(), INDICES_NOTE);
        AddressBook withoutNote = new AddressBook();
        withoutNote.addStudent(ALEX);

        assertFalse(withNote.equals(withoutNote));
    }

    @Test
    public void equals_differentStudents_returnsFalse() {
        AddressBook withAlex = new AddressBook();
        withAlex.addStudent(ALEX);
        AddressBook withBea = new AddressBook();
        withBea.addStudent(BEA);

        assertFalse(withAlex.equals(withBea));
    }

    @Test
    public void readOnlyGetSessionHistory_default_returnsEmptyHistory() {
        ReadOnlyAddressBook readOnlyAddressBook = new AddressBookStub(List.of());

        assertEquals(SessionHistory.empty(), readOnlyAddressBook.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

}
