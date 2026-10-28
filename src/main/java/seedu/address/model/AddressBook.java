package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.model.session.StudentSessionHistories;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    static final String MESSAGE_UNKNOWN_STUDENT = "Session notes must belong to a student in the roster.";
    static final String MESSAGE_STUDENT_NOT_FOUND = "Student must exist in the roster.";
    static final String MESSAGE_STUDENT_ID_MISMATCH = "Edited student must retain the target student's ID.";
    static final String MESSAGE_DUPLICATE_STUDENT = "Student with the same identity already exists.";

    private final UniquePersonList persons = new UniquePersonList();
    private final List<Student> students = new ArrayList<>();
    private final StudentSessionHistories sessionHistories = new StudentSessionHistories();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        setStudents(newData.getStudentList());
        setSessionHistories(newData);
    }

    /**
     * Replaces the contents of the student list with {@code students}.
     */
    private void setStudents(List<Student> students) {
        this.students.clear();
        this.students.addAll(students);
    }

    /**
     * Replaces the session histories with those {@code newData} holds for the students in this address book.
     * The student list must already be replaced, so that only histories of current students are copied.
     */
    private void setSessionHistories(ReadOnlyAddressBook newData) {
        sessionHistories.clear();
        for (Student student : students) {
            StudentId studentId = student.getId();
            sessionHistories.setHistory(studentId, newData.getSessionHistory(studentId));
        }
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
    }

    /**
     * Returns whether a student with the same identity as {@code student} exists.
     */
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return students.stream().anyMatch(student::hasSameIdentity);
    }

    /**
     * Adds a student to the address book.
     * The student must not already exist.
     */
    public void addStudent(Student student) {
        requireNonNull(student);
        if (hasStudent(student)) {
            throw new IllegalArgumentException(MESSAGE_DUPLICATE_STUDENT);
        }
        students.add(student);
        assert hasStudent(student) : "Student must be present after being added";
    }

    /**
     * Replaces the student with {@code studentId} while preserving the student's stable identity and session history.
     *
     * @throws IllegalArgumentException If the target does not exist, the replacement has a different ID, or another
     *         student has the replacement's duplicate-detection identity.
     */
    public void setStudent(StudentId studentId, Student editedStudent) {
        requireAllNonNull(studentId, editedStudent);
        int targetIndex = findStudentIndex(studentId);
        checkArgument(editedStudent.getId().equals(studentId), MESSAGE_STUDENT_ID_MISMATCH);
        checkArgument(findConflictingStudent(studentId, editedStudent).isEmpty(), MESSAGE_DUPLICATE_STUDENT);

        students.set(targetIndex, editedStudent);
    }

    /**
     * Removes the student with {@code studentId} and that student's session history.
     *
     * @return The number of session notes removed with the student.
     * @throws IllegalArgumentException if no student in the roster has {@code studentId}.
     */
    public int removeStudent(StudentId studentId) {
        requireNonNull(studentId);
        Student studentToRemove = students.get(findStudentIndex(studentId));

        students.remove(studentToRemove);
        return sessionHistories.removeHistory(studentId);
    }

    //// session-note operations

    /**
     * Adds {@code note} to the session history of the student with {@code studentId}.
     *
     * @throws IllegalArgumentException If no student in the roster has {@code studentId}.
     */
    public void addSessionNote(StudentId studentId, SessionNote note) {
        requireAllNonNull(studentId, note);
        checkArgument(hasStudentWithId(studentId), MESSAGE_UNKNOWN_STUDENT);
        sessionHistories.addNote(studentId, note);
    }

    /**
     * Replaces the session history of the student with {@code studentId} with {@code history}.
     *
     * @throws IllegalArgumentException If no student in the roster has {@code studentId}.
     */
    public void setSessionHistory(StudentId studentId, SessionHistory history) {
        requireAllNonNull(studentId, history);
        checkArgument(hasStudentWithId(studentId), MESSAGE_UNKNOWN_STUDENT);
        sessionHistories.setHistory(studentId, history);
    }

    /**
     * Removes and returns the note with the given chronological {@code noteNumber} from the student's history.
     *
     * @throws IllegalArgumentException If no student in the roster has {@code studentId}.
     * @throws IndexOutOfBoundsException If {@code noteNumber} exceeds the student's history size.
     */
    public SessionNote removeSessionNote(StudentId studentId, Index noteNumber) {
        requireAllNonNull(studentId, noteNumber);
        checkArgument(hasStudentWithId(studentId), MESSAGE_UNKNOWN_STUDENT);
        return sessionHistories.removeNote(studentId, noteNumber);
    }

    /**
     * Replaces one session note's text while preserving its recorded date and time.
     *
     * @return The replacement note.
     * @throws IllegalArgumentException If no student in the roster has {@code studentId}.
     * @throws IndexOutOfBoundsException If {@code noteNumber} exceeds the student's history size.
     */
    public SessionNote editSessionNote(StudentId studentId, Index noteNumber, String text) {
        requireAllNonNull(studentId, noteNumber, text);
        checkArgument(hasStudentWithId(studentId), MESSAGE_UNKNOWN_STUDENT);
        return sessionHistories.editNote(studentId, noteNumber, text);
    }

    /**
     * Removes the session history of the student with {@code studentId}.
     *
     * @return The number of session notes removed, which is zero if the student had none.
     */
    public int removeSessionHistory(StudentId studentId) {
        requireNonNull(studentId);
        return sessionHistories.removeHistory(studentId);
    }

    @Override
    public SessionHistory getSessionHistory(StudentId studentId) {
        requireNonNull(studentId);
        return sessionHistories.getHistory(studentId);
    }

    private boolean hasStudentWithId(StudentId studentId) {
        return students.stream().anyMatch(student -> student.getId().equals(studentId));
    }

    /**
     * Returns the list index of the student with {@code studentId}.
     *
     * @throws IllegalArgumentException If no student has {@code studentId}.
     */
    private int findStudentIndex(StudentId studentId) {
        for (int index = 0; index < students.size(); index++) {
            if (students.get(index).getId().equals(studentId)) {
                return index;
            }
        }
        throw new IllegalArgumentException(MESSAGE_STUDENT_NOT_FOUND);
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public List<Student> getStudentList() {
        return List.copyOf(students);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && students.equals(otherAddressBook.students)
                && sessionHistories.equals(otherAddressBook.sessionHistories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, students, sessionHistories);
    }
}
