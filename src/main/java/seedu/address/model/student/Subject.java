package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the subject taught to a student.
 */
public final class Subject {

    public static final String MESSAGE_CONSTRAINTS =
            "Subjects must contain a letter or number and must not contain line breaks.";
    private final String value;

    /**
     * Constructs a normalized subject.
     *
     * @param subject A valid subject.
     */
    public Subject(String subject) {
        requireNonNull(subject);
        checkArgument(isValidSubject(subject), MESSAGE_CONSTRAINTS);
        value = normalize(subject);
    }

    /**
     * Returns whether {@code subject} is a valid subject.
     */
    public static boolean isValidSubject(String subject) {
        requireNonNull(subject);
        return subject.matches(".*[\\p{L}\\p{N}].*") && !subject.matches(".*[\\r\\n].*");
    }

    /**
     * Returns the canonical display form with surrounding and repeated whitespace removed.
     */
    public static String normalize(String subject) {
        return StudentTextFields.normalizeWhitespace(subject);
    }

    /**
     * Returns the subject.
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
        return other == this || other instanceof Subject otherSubject && value.equals(otherSubject.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
