package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

/**
 * Shared normalization and validation helpers for single-line student text fields.
 * Centralizes the rules reused by {@link StudentName}, {@link Subject}, and {@link CurrentLevel}.
 */
final class StudentTextFields {

    private static final String REPEATED_WHITESPACE_REGEX = "\\s+";
    private static final String SINGLE_SPACE = " ";
    private static final String LINE_BREAK_REGEX = ".*[\\r\\n].*";

    private StudentTextFields() {
    }

    /**
     * Returns the canonical display form with surrounding and repeated whitespace removed.
     */
    static String normalizeWhitespace(String value) {
        requireNonNull(value);
        return value.trim().replaceAll(REPEATED_WHITESPACE_REGEX, SINGLE_SPACE);
    }

    /**
     * Returns whether {@code value} has non-whitespace content and contains no line breaks.
     */
    static boolean isNonBlankSingleLine(String value) {
        requireNonNull(value);
        return !value.trim().isEmpty() && !value.matches(LINE_BREAK_REGEX);
    }
}
