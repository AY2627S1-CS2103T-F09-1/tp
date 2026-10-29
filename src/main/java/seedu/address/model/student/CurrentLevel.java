package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a student's current level in a subject.
 */
public final class CurrentLevel {

    public static final String MESSAGE_CONSTRAINTS =
            "Current levels must contain a letter or number and must not contain line breaks.";
    private final String value;

    /**
     * Constructs a normalized current level.
     *
     * @param currentLevel A valid current level.
     */
    public CurrentLevel(String currentLevel) {
        requireNonNull(currentLevel);
        checkArgument(isValidCurrentLevel(currentLevel), MESSAGE_CONSTRAINTS);
        value = normalize(currentLevel);
    }

    /**
     * Returns whether {@code currentLevel} is a valid current level.
     */
    public static boolean isValidCurrentLevel(String currentLevel) {
        requireNonNull(currentLevel);
        return currentLevel.matches("(?s).*[\\p{L}\\p{N}].*")
                && !currentLevel.matches("(?s).*[\\r\\n].*");
    }

    /**
     * Returns the canonical display form with surrounding and repeated whitespace removed.
     */
    public static String normalize(String currentLevel) {
        return StudentTextFields.normalizeWhitespace(currentLevel);
    }

    /**
     * Returns the current level.
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
        return other == this || other instanceof CurrentLevel otherLevel && value.equals(otherLevel.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
