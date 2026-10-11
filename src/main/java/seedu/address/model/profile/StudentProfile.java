package seedu.address.model.profile;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Represents an immutable snapshot of one student's lesson-preparation context and session history.
 */
public final class StudentProfile {

    private final StudentId studentId;
    private final StudentName name;
    private final ParentGuardianContact parentGuardianContact;
    private final Subject subject;
    private final CurrentLevel currentLevel;
    private final List<SessionNote> sessionNotes;

    /**
     * Constructs a profile from one student's context and newest-first session notes.
     *
     * @param studentId The student's stable internal identifier.
     * @param name The student's name.
     * @param parentGuardianContact The responsible adult's contact details.
     * @param subject The subject taught to the student.
     * @param currentLevel The student's current level.
     * @param sessionNotes The student's session notes in newest-first order.
     */
    public StudentProfile(StudentId studentId, StudentName name, ParentGuardianContact parentGuardianContact,
            Subject subject, CurrentLevel currentLevel, List<SessionNote> sessionNotes) {
        requireAllNonNull(studentId, name, parentGuardianContact, subject, currentLevel, sessionNotes);
        this.studentId = studentId;
        this.name = name;
        this.parentGuardianContact = parentGuardianContact;
        this.subject = subject;
        this.currentLevel = currentLevel;
        this.sessionNotes = List.copyOf(sessionNotes);
    }

    /**
     * Returns the student's stable internal identifier.
     */
    public StudentId getStudentId() {
        return studentId;
    }

    /**
     * Returns the student's name.
     */
    public StudentName getName() {
        return name;
    }

    /**
     * Returns the parent or guardian contact details.
     */
    public ParentGuardianContact getParentGuardianContact() {
        return parentGuardianContact;
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
     * Returns an immutable newest-first list of the student's session notes.
     */
    public List<SessionNote> getSessionNotes() {
        return sessionNotes;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof StudentProfile otherProfile
                && studentId.equals(otherProfile.studentId)
                && name.equals(otherProfile.name)
                && parentGuardianContact.equals(otherProfile.parentGuardianContact)
                && subject.equals(otherProfile.subject)
                && currentLevel.equals(otherProfile.currentLevel)
                && sessionNotes.equals(otherProfile.sessionNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, name, parentGuardianContact, subject, currentLevel, sessionNotes);
    }
}
