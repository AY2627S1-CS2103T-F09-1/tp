package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents the canonical identity used to detect duplicate student records.
 */
public final class StudentIdentity {

    private final String normalizedName;
    private final String normalizedParentPhone;

    /**
     * Constructs the identity from a student name and parent or guardian contact.
     *
     * @param studentName The student's name.
     * @param parentGuardianContact The associated parent or guardian contact.
     */
    public StudentIdentity(StudentName studentName, ParentGuardianContact parentGuardianContact) {
        requireNonNull(studentName);
        requireNonNull(parentGuardianContact);
        normalizedName = studentName.getValue().toLowerCase(Locale.ROOT);
        normalizedParentPhone = parentGuardianContact.getNormalizedPhone();
    }

    /**
     * Returns whether this identity matches {@code otherIdentity}.
     */
    public boolean matches(StudentIdentity otherIdentity) {
        requireNonNull(otherIdentity);
        return equals(otherIdentity);
    }

    /**
     * Returns the canonical value used to derive a legacy student's stable identifier.
     */
    public String getStableValue() {
        return normalizedName + "\u0000" + normalizedParentPhone;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof StudentIdentity otherIdentity
                && normalizedName.equals(otherIdentity.normalizedName)
                && normalizedParentPhone.equals(otherIdentity.normalizedParentPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(normalizedName, normalizedParentPhone);
    }
}
