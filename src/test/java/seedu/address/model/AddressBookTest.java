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
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;
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
    public void getStudent_knownAndUnknownIds_returnsMatchingOptional() {
        addressBook.addStudent(ALEX);

        assertEquals(Optional.of(ALEX), addressBook.getStudent(ALEX.getId()));
        assertEquals(Optional.empty(), addressBook.getStudent(BEA.getId()));
        assertThrows(NullPointerException.class, () -> addressBook.getStudent(null));
    }

    @Test
    public void findConflictingStudent_excludesTargetAndReturnsOtherDuplicate() {
        addressBook.addStudent(ALEX);
        addressBook.addStudent(BEA);
        Student duplicateBea = new Student(BEA.getName(), BEA.getParentGuardianContact(), ALEX.getSubject(),
                ALEX.getCurrentLevel(), ALEX.getId());

        assertEquals(Optional.of(BEA), addressBook.findConflictingStudent(ALEX.getId(), duplicateBea));
        assertEquals(Optional.empty(), addressBook.findConflictingStudent(BEA.getId(), BEA));
        assertThrows(NullPointerException.class, () -> addressBook.findConflictingStudent(null, duplicateBea));
        assertThrows(NullPointerException.class, () -> addressBook.findConflictingStudent(ALEX.getId(), null));
    }

    @Test
    public void setStudent_validReplacement_preservesSessionHistory() {
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        Student editedAlex = editedAlex("Alexis Tan", "9876 0000", "Physics", "Secondary 4");

        addressBook.setStudent(ALEX.getId(), editedAlex);

        assertEquals(List.of(editedAlex), addressBook.getStudentList());
        assertEquals(List.of(INDICES_NOTE), addressBook.getSessionHistory(ALEX.getId()).getNotes());
    }

    @Test
    public void setStudent_sameIdentityWithUpdatedDetails_replacesStudent() {
        addressBook.addStudent(ALEX);
        Student editedAlex = editedAlex(ALEX.getName().getValue(),
                ALEX.getParentGuardianContact().getPhone(), "Physics", "Secondary 4");

        addressBook.setStudent(ALEX.getId(), editedAlex);

        assertEquals(List.of(editedAlex), addressBook.getStudentList());
    }

    @Test
    public void setStudent_unknownStudent_throwsAndLeavesDataUnchanged() {
        addressBook.addStudent(ALEX);
        AddressBook original = new AddressBook(addressBook);
        Student editedBea = new Student(BEA.getName(), BEA.getParentGuardianContact(), BEA.getSubject(),
                BEA.getCurrentLevel(), BEA.getId());

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_STUDENT_NOT_FOUND, () ->
                addressBook.setStudent(BEA.getId(), editedBea));
        assertEquals(original, addressBook);
    }

    @Test
    public void setStudent_mismatchedStudentId_throwsAndLeavesDataUnchanged() {
        addressBook.addStudent(ALEX);
        AddressBook original = new AddressBook(addressBook);

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_STUDENT_ID_MISMATCH, () ->
                addressBook.setStudent(ALEX.getId(), BEA));
        assertEquals(original, addressBook);
    }

    @Test
    public void setStudent_duplicateIdentity_throwsAndLeavesDataUnchanged() {
        addressBook.addStudent(ALEX);
        addressBook.addStudent(BEA);
        AddressBook original = new AddressBook(addressBook);
        Student duplicateBea = new Student(BEA.getName(), BEA.getParentGuardianContact(), ALEX.getSubject(),
                ALEX.getCurrentLevel(), ALEX.getId());

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_DUPLICATE_STUDENT, () ->
                addressBook.setStudent(ALEX.getId(), duplicateBea));
        assertEquals(original, addressBook);
    }

    @Test
    public void setStudent_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.setStudent(null, ALEX));
        assertThrows(NullPointerException.class, () -> addressBook.setStudent(ALEX.getId(), null));
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
    public void removeSessionNote_validNumber_removesAndReturnsSelectedNote() {
        addressBook.addStudent(ALEX);
        addressBook.addStudent(BEA);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        addressBook.addSessionNote(ALEX.getId(), FACTORISATION_NOTE);

        assertEquals(INDICES_NOTE, addressBook.removeSessionNote(ALEX.getId(), Index.fromOneBased(1)));
        assertEquals(List.of(FACTORISATION_NOTE), addressBook.getSessionHistory(ALEX.getId()).getNotes());
        assertTrue(addressBook.getSessionHistory(BEA.getId()).isEmpty());
    }

    @Test
    public void removeSessionNote_unknownStudent_throwsIllegalArgumentException() {
        addressBook.addStudent(ALEX);

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_UNKNOWN_STUDENT, () -> addressBook
                .removeSessionNote(BEA.getId(), Index.fromOneBased(1)));
    }

    @Test
    public void setSessionHistory_studentInRoster_replacesHistory() {
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        SessionHistory history = SessionHistory.of(List.of(FACTORISATION_NOTE));

        addressBook.setSessionHistory(ALEX.getId(), history);

        assertEquals(history, addressBook.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void setSessionHistory_studentNotInRoster_throwsIllegalArgumentException() {
        addressBook.addStudent(ALEX);
        SessionHistory history = SessionHistory.of(List.of(INDICES_NOTE));

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_UNKNOWN_STUDENT, () -> addressBook
                .setSessionHistory(BEA.getId(), history));
        assertTrue(addressBook.getSessionHistory(BEA.getId()).isEmpty());
    }

    @Test
    public void editSessionNote_validNumber_replacesTextAndPreservesTimestamp() {
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);

        SessionNote editedNote = addressBook.editSessionNote(ALEX.getId(), Index.fromOneBased(1), "Corrected summary.");

        assertEquals("Corrected summary.", editedNote.getText());
        assertEquals(INDICES_NOTE.getRecordedAt(), editedNote.getRecordedAt());
        assertEquals(List.of(editedNote), addressBook.getSessionHistory(ALEX.getId()).getNotes());
    }

    @Test
    public void editSessionNote_unknownStudent_throwsIllegalArgumentException() {
        addressBook.addStudent(ALEX);

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_UNKNOWN_STUDENT, () -> addressBook
                .editSessionNote(BEA.getId(), Index.fromOneBased(1), "Corrected summary."));
    }

    @Test
    public void sessionNoteMethods_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.addSessionNote(null, INDICES_NOTE));
        assertThrows(NullPointerException.class, () -> addressBook.addSessionNote(ALEX.getId(), null));
        assertThrows(NullPointerException.class, () -> addressBook.setSessionHistory(null, SessionHistory.empty()));
        assertThrows(NullPointerException.class, () -> addressBook.setSessionHistory(ALEX.getId(), null));
        assertThrows(NullPointerException.class, () -> addressBook.removeSessionNote(null, Index.fromOneBased(1)));
        assertThrows(NullPointerException.class, () -> addressBook.removeSessionNote(ALEX.getId(), null));
        assertThrows(NullPointerException.class, () -> addressBook.editSessionNote(null,
                Index.fromOneBased(1), "text"));
        assertThrows(NullPointerException.class, () -> addressBook.editSessionNote(ALEX.getId(), null, "text"));
        assertThrows(NullPointerException.class, () -> addressBook.editSessionNote(ALEX.getId(),
                Index.fromOneBased(1), null));
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
    public void removeStudent_studentWithNotes_removesStudentAndHistory() {
        addressBook.addStudent(ALEX);
        addressBook.addStudent(BEA);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        addressBook.addSessionNote(ALEX.getId(), FACTORISATION_NOTE);

        assertEquals(2, addressBook.removeStudent(ALEX.getId()));
        assertEquals(List.of(BEA), addressBook.getStudentList());
        assertTrue(addressBook.getSessionHistory(ALEX.getId()).isEmpty());
    }

    @Test
    public void removeStudent_unknownStudent_throwsAndLeavesDataUnchanged() {
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        AddressBook original = new AddressBook(addressBook);

        assertThrows(IllegalArgumentException.class, AddressBook.MESSAGE_STUDENT_NOT_FOUND, () ->
                addressBook.removeStudent(BEA.getId()));
        assertEquals(original, addressBook);
    }

    @Test
    public void removeStudent_nullStudentId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.removeStudent(null));
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

    private static Student editedAlex(String name, String phone, String subject, String currentLevel) {
        return new Student(new StudentName(name), new ParentGuardianContact(phone, Optional.empty()),
                new Subject(subject), new CurrentLevel(currentLevel), ALEX.getId());
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
