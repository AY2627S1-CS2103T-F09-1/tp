package seedu.address.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a short, timestamped record of what mattered in one lesson.
 */
public final class SessionNote {

    public static final int MIN_TEXT_LENGTH = 1;
    public static final int MAX_TEXT_LENGTH = 500;
    public static final String MESSAGE_CONSTRAINTS = "Session notes must be " + MIN_TEXT_LENGTH + " to "
            + MAX_TEXT_LENGTH + " printable characters on one line.";

    /**
     * Matches control characters (including tabs and line breaks) and Unicode line or paragraph separators.
     */
    private static final Pattern NON_PRINTABLE_PATTERN = Pattern.compile("[\\p{Cc}\\p{Zl}\\p{Zp}]");

    private final String text;
    private final OffsetDateTime recordedAt;

    /**
     * Constructs a session note with surrounding whitespace removed from its text.
     *
     * @param text A valid note text.
     * @param recordedAt The date-time the note was recorded.
     */
    public SessionNote(String text, OffsetDateTime recordedAt) {
        requireNonNull(text);
        requireNonNull(recordedAt);
        checkArgument(isValidText(text), MESSAGE_CONSTRAINTS);
        this.text = text.strip();
        this.recordedAt = recordedAt;
    }

    /**
     * Returns whether {@code text} is a valid note text once surrounding whitespace is removed.
     * Internal whitespace is preserved and length is counted in Unicode code points.
     */
    public static boolean isValidText(String text) {
        int length = countTextLength(text);
        boolean hasValidLength = length >= MIN_TEXT_LENGTH && length <= MAX_TEXT_LENGTH;
        return hasValidLength && !hasNonPrintableCharacter(text);
    }

    /**
     * Returns the length of {@code text} once surrounding whitespace is removed, counted in Unicode code
     * points so that an emoji counts as one character.
     */
    public static int countTextLength(String text) {
        requireNonNull(text);
        String strippedText = text.strip();
        return strippedText.codePointCount(0, strippedText.length());
    }

    /**
     * Returns whether {@code text} contains a control character (such as a tab or line break) or a Unicode
     * line or paragraph separator once surrounding whitespace is removed.
     */
    public static boolean hasNonPrintableCharacter(String text) {
        requireNonNull(text);
        return NON_PRINTABLE_PATTERN.matcher(text.strip()).find();
    }

    /**
     * Returns the note text.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the date-time the note was recorded.
     */
    public OffsetDateTime getRecordedAt() {
        return recordedAt;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof SessionNote otherNote
                && text.equals(otherNote.text)
                && recordedAt.equals(otherNote.recordedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, recordedAt);
    }
}
