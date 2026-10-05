package seedu.address.model;

import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.Person;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRoster;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Returns whether a student with the same identity as {@code student} exists.
     */
    default boolean hasStudent(Student student) {
        return getAddressBook().getStudentList().stream().anyMatch(student::hasSameIdentity);
    }

    /**
     * Adds a student to the model.
     */
    default void addStudent(Student student) {
        throw new UnsupportedOperationException("Student additions are not supported by this model.");
    }

    /**
     * Returns an immutable snapshot of the student roster, including each student's session-note count.
     */
    default StudentRoster getStudentRoster() {
        ReadOnlyAddressBook addressBook = getAddressBook();
        Map<StudentId, Integer> noteCounts = addressBook.getStudentList().stream()
                .map(Student::getId)
                .collect(Collectors.toMap(studentId -> studentId,
                        studentId -> addressBook.getSessionHistory(studentId).size()));
        return new StudentRoster(addressBook.getStudentList(), noteCounts);
    }

    /**
     * Returns the newest-first session history of the student with {@code studentId}.
     * Returns an empty history if the student has no session notes.
     */
    default SessionHistory getSessionHistory(StudentId studentId) {
        return getAddressBook().getSessionHistory(studentId);
    }

    /**
     * Adds a session note with {@code text}, timestamped at the current time, to the student with
     * {@code studentId}.
     *
     * @param studentId The identifier of a student in the roster.
     * @param text Valid note text, as checked by {@link SessionNote#isValidText(String)}.
     * @return The session note that was added.
     * @throws IllegalArgumentException if the text is invalid or no student in the roster has
     *         {@code studentId}.
     */
    default SessionNote addSessionNote(StudentId studentId, String text) {
        throw new UnsupportedOperationException("Session note additions are not supported by this model.");
    }

    /**
     * Removes the session history of the student with {@code studentId}.
     *
     * @return The number of session notes removed, which is zero if the student had none.
     */
    default int removeSessionHistory(StudentId studentId) {
        throw new UnsupportedOperationException("Session history removal is not supported by this model.");
    }

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);
}
