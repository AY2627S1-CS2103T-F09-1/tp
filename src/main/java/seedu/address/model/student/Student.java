package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

/**
 * Represents a learner managed by TutorTrack.
 */
public final class Student {

    private final StudentName name;
    private final ParentGuardianContact parentGuardianContact;
    private final Subject subject;
    private final CurrentLevel currentLevel;
    private final StudentIdentity identity;

    /**
     * Constructs a student with all required tutoring context.
     *
     * @param name The student's name.
     * @param parentGuardianContact The responsible adult's contact details.
     * @param subject The subject taught to the student.
     * @param currentLevel The student's current level in the subject.
     */
    public Student(StudentName name, ParentGuardianContact parentGuardianContact, Subject subject,
            CurrentLevel currentLevel) {
        requireAllNonNull(name, parentGuardianContact, subject, currentLevel);
        this.name = name;
        this.parentGuardianContact = parentGuardianContact;
        this.subject = subject;
        this.currentLevel = currentLevel;
        identity = new StudentIdentity(name, parentGuardianContact);
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
     * Returns whether this student has the same duplicate-detection identity as {@code otherStudent}.
     */
    public boolean hasSameIdentity(Student otherStudent) {
        return otherStudent != null && identity.matches(otherStudent.identity);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof Student otherStudent
                && name.equals(otherStudent.name)
                && parentGuardianContact.equals(otherStudent.parentGuardianContact)
                && subject.equals(otherStudent.subject)
                && currentLevel.equals(otherStudent.currentLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentGuardianContact, subject, currentLevel);
    }
}
