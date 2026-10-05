package seedu.address.model;

import java.util.List;

import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    /**
     * Returns an unmodifiable view of the students added through TutorTrack commands.
     */
    default List<Student> getStudentList() {
        return List.of();
    }

    /**
     * Returns the newest-first session history of the student with {@code studentId}.
     * Returns an empty history if the student has no session notes.
     */
    default SessionHistory getSessionHistory(StudentId studentId) {
        return SessionHistory.empty();
    }

}
