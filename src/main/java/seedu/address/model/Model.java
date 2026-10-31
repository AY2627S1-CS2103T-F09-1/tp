package seedu.address.model;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.index.Index;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRoster;

/**
 * The API of the Model component.
 */
public interface Model {
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
     * Returns whether a student with the same identity as {@code student} exists.
     */
    default boolean hasStudent(Student student) {
        return getAddressBook().getStudentList().stream().anyMatch(student::hasSameIdentity);
    }

    /**
     * Returns the immutable student with {@code studentId}, or an empty optional if that student is absent.
     */
    default Optional<Student> getStudent(StudentId studentId) {
        return getAddressBook().getStudent(studentId);
    }

    /**
     * Returns another student with the same identity as {@code editedStudent}, if one exists.
     */
    default Optional<Student> findConflictingStudent(StudentId targetStudentId, Student editedStudent) {
        return getAddressBook().findConflictingStudent(targetStudentId, editedStudent);
    }

    /**
     * Adds a student to the model.
     */
    default void addStudent(Student student) {
        throw new UnsupportedOperationException("Student additions are not supported by this model.");
    }

    /**
     * Replaces the student with {@code studentId} while preserving that student's session history.
     */
    default void setStudent(StudentId studentId, Student editedStudent) {
        throw new UnsupportedOperationException("Student edits are not supported by this model.");
    }

    /**
     * Deletes the student with {@code studentId} and that student's session history.
     *
     * @return The number of session notes removed with the student.
     */
    default int deleteStudent(StudentId studentId) {
        throw new UnsupportedOperationException("Student deletions are not supported by this model.");
    }

    /**
     * Returns an immutable snapshot of the student roster, including each student's session-note count.
     */
    default StudentRoster getStudentRoster() {
        ReadOnlyAddressBook addressBook = getAddressBook();
        Map<StudentId, Integer> noteCounts = addressBook.getStudentList().stream()
                .map(Student::getId)
                .collect(Collectors.toMap(Function.identity(),
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
     * Returns an immutable profile of the student with {@code studentId}, or an empty optional if the student is
     * absent.
     */
    default Optional<StudentProfile> getStudentProfile(StudentId studentId) {
        Objects.requireNonNull(studentId);
        ReadOnlyAddressBook addressBook = getAddressBook();
        return addressBook.getStudentList().stream()
                .filter(student -> student.getId().equals(studentId))
                .findFirst()
                .map(student -> new StudentProfile(student.getId(), student.getName(),
                        student.getParentGuardianContact(), student.getSubject(), student.getCurrentLevel(),
                        addressBook.getSessionHistory(studentId).getNotes()));
    }

    /**
     * Adds a session note with {@code text}, timestamped at the current time, to the student with
     * {@code studentId}.
     *
     * @param studentId The identifier of a student in the roster.
     * @param text Valid note text, as checked by {@link SessionNote#isValidText(String)}.
     * @return The session note that was added.
     * @throws IllegalArgumentException If the text is invalid or no student in the roster has
     *         {@code studentId}.
     */
    default SessionNote addSessionNote(StudentId studentId, String text) {
        throw new UnsupportedOperationException("Session note additions are not supported by this model.");
    }

    /**
     * Deletes and returns the note with the given chronological {@code noteNumber} from the student's history.
     */
    default SessionNote deleteSessionNote(StudentId studentId, Index noteNumber) {
        throw new UnsupportedOperationException("Session note deletions are not supported by this model.");
    }

    /**
     * Replaces a session note's text while preserving its recorded date and time.
     *
     * @param studentId The identifier of a student in the roster.
     * @param noteNumber The chronological number of the note to edit.
     * @param text Valid replacement note text, as checked by {@link SessionNote#isValidText(String)}.
     * @return The replacement note.
     * @throws IllegalArgumentException If the text is invalid or no student in the roster has {@code studentId}.
     * @throws IndexOutOfBoundsException If {@code noteNumber} exceeds the student's history size.
     */
    default SessionNote editSessionNote(StudentId studentId, Index noteNumber, String text) {
        throw new UnsupportedOperationException("Session note editing is not supported by this model.");
    }

    /**
     * Removes the session history of the student with {@code studentId}.
     *
     * @return The number of session notes removed, which is zero if the student had none.
     */
    default int removeSessionHistory(StudentId studentId) {
        throw new UnsupportedOperationException("Session history removal is not supported by this model.");
    }

}
