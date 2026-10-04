package seedu.address.model.student;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents an immutable student snapshot prepared for roster display.
 */
public final class StudentRosterEntry {

    private final StudentId studentId;
    private final int rosterIndex;
    private final StudentName name;
    private final Subject subject;
    private final CurrentLevel currentLevel;
    private final int noteCount;

    /**
     * Constructs a roster entry with its display index and session-note count.
     *
     * @param studentId The stable internal student identifier.
     * @param rosterIndex The one-based index shown to the user.
     * @param name The student's name.
     * @param subject The subject taught to the student.
     * @param currentLevel The student's current level.
     * @param noteCount The number of session notes belonging to the student.
     */
    public StudentRosterEntry(StudentId studentId, int rosterIndex, StudentName name, Subject subject,
            CurrentLevel currentLevel, int noteCount) {
        requireAllNonNull(studentId, name, subject, currentLevel);
        checkArgument(rosterIndex > 0, "Roster indices must be positive.");
        checkArgument(noteCount >= 0, "Session-note counts must not be negative.");
        this.studentId = studentId;
        this.rosterIndex = rosterIndex;
        this.name = name;
        this.subject = subject;
        this.currentLevel = currentLevel;
        this.noteCount = noteCount;
    }

    /**
     * Returns the stable internal student identifier.
     */
    public StudentId getStudentId() {
        return studentId;
    }

    /**
     * Returns the one-based roster index shown to the user.
     */
    public int getRosterIndex() {
        return rosterIndex;
    }

    /**
     * Returns the student's name.
     */
    public StudentName getName() {
        return name;
    }

    /**
     * Returns the subject taught to the student.
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * Returns the student's current level.
     */
    public CurrentLevel getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Returns the number of session notes belonging to the student.
     */
    public int getNoteCount() {
        return noteCount;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof StudentRosterEntry otherEntry
                && studentId.equals(otherEntry.studentId)
                && rosterIndex == otherEntry.rosterIndex
                && name.equals(otherEntry.name)
                && subject.equals(otherEntry.subject)
                && currentLevel.equals(otherEntry.currentLevel)
                && noteCount == otherEntry.noteCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, rosterIndex, name, subject, currentLevel, noteCount);
    }
}
