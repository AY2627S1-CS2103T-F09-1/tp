package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents the subject taught to a student.
 */
public final class Subject {

    public static final String MESSAGE_CONSTRAINTS = "Subjects must not be blank or contain line breaks.";
    private final String value;

    /**
     * Constructs a normalized subject.
     *
     * @param subject A valid subject.
     */
    public Subject(String subject) {
        requireNonNull(subject);
        checkArgument(isValidSubject(subject), MESSAGE_CONSTRAINTS);
        String normalizedSubject = normalize(subject);
        value = normalizedSubject;
    }

    /**
     * Returns whether {@code subject} is a valid subject.
     */
    public static boolean isValidSubject(String subject) {
        requireNonNull(subject);
        return !subject.trim().isEmpty() && !subject.matches(".*[\\r\\n].*");
    }

    /**
     * Returns the canonical display form with surrounding and repeated whitespace removed.
     */
    public static String normalize(String subject) {
        requireNonNull(subject);
        return subject.trim().replaceAll("\\s+", " ");
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
