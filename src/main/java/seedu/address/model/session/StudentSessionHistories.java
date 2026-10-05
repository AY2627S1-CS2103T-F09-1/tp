package seedu.address.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.HashMap;
import java.util.Map;

import seedu.address.model.student.StudentId;

/**
 * Holds the session history of each student, keyed by the student's stable identifier.
 * Students without any session notes have no stored entry and are reported as having an empty history.
 */
public class StudentSessionHistories {

    private final Map<StudentId, SessionHistory> histories = new HashMap<>();

    /**
     * Returns the session history of the student with {@code studentId}.
     * Returns an empty history if the student has no session notes.
     */
    public SessionHistory getHistory(StudentId studentId) {
        requireNonNull(studentId);
        return histories.getOrDefault(studentId, SessionHistory.empty());
    }

    /**
     * Adds {@code note} to the session history of the student with {@code studentId}.
     */
    public void addNote(StudentId studentId, SessionNote note) {
        requireAllNonNull(studentId, note);
        histories.put(studentId, getHistory(studentId).withNote(note));
    }

    /**
     * Replaces the session history of the student with {@code studentId} with {@code history}.
     * An empty {@code history} removes the student's entry.
     */
    public void setHistory(StudentId studentId, SessionHistory history) {
        requireAllNonNull(studentId, history);
        if (history.isEmpty()) {
            histories.remove(studentId);
            return;
        }
        histories.put(studentId, history);
    }

    /**
     * Removes the session history of the student with {@code studentId}.
     *
     * @return The number of session notes removed, which is zero if the student had none.
     */
    public int removeHistory(StudentId studentId) {
        requireNonNull(studentId);
        SessionHistory removedHistory = histories.remove(studentId);
        return removedHistory == null ? 0 : removedHistory.size();
    }

    /**
     * Removes the session histories of all students.
     */
    public void clear() {
        histories.clear();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof StudentSessionHistories otherHistories
                && histories.equals(otherHistories.histories);
    }

    @Override
    public int hashCode() {
        return histories.hashCode();
    }
}
