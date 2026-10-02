package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's name.
 */
public final class StudentName {

    public static final String MESSAGE_CONSTRAINTS =
            "Student names must contain letters or numbers and may include spaces, apostrophes, periods, and hyphens.";
    private static final String VALIDATION_REGEX = "[\\p{L}\\p{N}][\\p{L}\\p{N} .'\\-]*";

    private final String value;

    /**
     * Constructs a normalized student name.
     *
     * @param name A valid student name.
     */
    public StudentName(String name) {
        requireNonNull(name);
        String normalizedName = normalize(name);
        checkArgument(isValidName(normalizedName), MESSAGE_CONSTRAINTS);
        value = normalizedName;
    }

    /**
     * Returns whether {@code name} is a valid student name.
     */
    public static boolean isValidName(String name) {
        requireNonNull(name);
        return name.matches(VALIDATION_REGEX);
    }

    /**
     * Returns the canonical display form with surrounding and repeated whitespace removed.
     */
    public static String normalize(String name) {
        requireNonNull(name);
        return name.trim().replaceAll("\\s+", " ");
    }

    /**
     * Returns the student name.
     */
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof StudentName otherName && value.equals(otherName.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
