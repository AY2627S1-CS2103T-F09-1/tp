package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Represents the stable internal identifier of a student.
 */
public final class StudentId {

    public static final String MESSAGE_CONSTRAINTS = "Student IDs must be valid UUIDs.";

    private final UUID value;

    /**
     * Constructs an identifier with the given UUID.
     *
     * @param value The UUID backing this identifier.
     */
    public StudentId(UUID value) {
        this.value = requireNonNull(value);
    }

    /**
     * Returns a newly generated student identifier.
     */
    public static StudentId generate() {
        return new StudentId(UUID.randomUUID());
    }

    /**
     * Returns the deterministic identifier used when loading a legacy student without an ID.
     *
     * @param identity The student's duplicate-detection identity.
     */
    public static StudentId fromLegacyIdentity(StudentIdentity identity) {
        requireNonNull(identity);
        byte[] identityBytes = identity.getStableValue().getBytes(StandardCharsets.UTF_8);
        return new StudentId(UUID.nameUUIDFromBytes(identityBytes));
    }

    /**
     * Returns the identifier represented by the given UUID string.
     *
     * @param value A UUID string.
     * @throws IllegalArgumentException if {@code value} is not a valid UUID string.
     */
    public static StudentId fromString(String value) {
        requireNonNull(value);
        try {
            return new StudentId(UUID.fromString(value));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(MESSAGE_CONSTRAINTS, exception);
        }
    }

    /**
     * Returns the UUID string for this identifier.
     */
    public String getValue() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof StudentId otherStudentId && value.equals(otherStudentId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return getValue();
    }
}
