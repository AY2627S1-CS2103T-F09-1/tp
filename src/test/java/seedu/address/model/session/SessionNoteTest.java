package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

public class SessionNoteTest {

    private static final OffsetDateTime RECORDED_AT = OffsetDateTime.parse("2026-09-18T18:35:00+08:00");
    private static final String VALID_TEXT = "Reviewed factorisation.";

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SessionNote(null, RECORDED_AT));
        assertThrows(NullPointerException.class, () -> new SessionNote(VALID_TEXT, null));
    }

    @Test
    public void constructor_invalidText_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, SessionNote.MESSAGE_CONSTRAINTS, () -> new SessionNote(
                "   ", RECORDED_AT));
    }

    @Test
    public void constructor_validText_trimsSurroundingWhitespaceOnly() {
        SessionNote note = new SessionNote("  Paper 2:  Q3 & Q5 (50%)  ", RECORDED_AT);

        assertEquals("Paper 2:  Q3 & Q5 (50%)", note.getText());
        assertEquals(RECORDED_AT, note.getRecordedAt());
    }

    @Test
    public void isValidText_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SessionNote.isValidText(null));
    }

    @Test
    public void isValidText_blankText_returnsFalse() {
        assertFalse(SessionNote.isValidText(""));
        assertFalse(SessionNote.isValidText("    "));
    }

    @Test
    public void isValidText_lengthBoundaries_acceptsUpToMaximum() {
        assertTrue(SessionNote.isValidText("a"));
        assertTrue(SessionNote.isValidText("a".repeat(SessionNote.MAX_TEXT_LENGTH)));
        assertFalse(SessionNote.isValidText("a".repeat(SessionNote.MAX_TEXT_LENGTH + 1)));
    }

    @Test
    public void isValidText_surroundingWhitespace_notCountedInLength() {
        assertTrue(SessionNote.isValidText("  " + "a".repeat(SessionNote.MAX_TEXT_LENGTH) + "  "));
    }

    @Test
    public void isValidText_surrogatePairs_countedAsOneCharacterEach() {
        String smile = "😀";

        assertTrue(SessionNote.isValidText(smile.repeat(SessionNote.MAX_TEXT_LENGTH)));
        assertFalse(SessionNote.isValidText(smile.repeat(SessionNote.MAX_TEXT_LENGTH + 1)));
    }

    @Test
    public void isValidText_nonPrintableCharacters_returnsFalse() {
        assertFalse(SessionNote.isValidText("Line one\nLine two"));
        assertFalse(SessionNote.isValidText("Line one\rLine two"));
        assertFalse(SessionNote.isValidText("Column\tvalue"));
        assertFalse(SessionNote.isValidText("Line one Line two"));
        assertFalse(SessionNote.isValidText("Para one Para two"));
        assertFalse(SessionNote.isValidText("Bell\u0007"));
    }

    @Test
    public void isValidText_ordinarySymbolsAndUnicode_returnsTrue() {
        assertTrue(SessionNote.isValidText("Next: try x^2 - 4x + 4 = 0; HW p.12 #3-7 @ 50% / 2 hrs"));
        assertTrue(SessionNote.isValidText("Revised 分数 and café vocabulary"));
    }

    @Test
    public void equals() {
        SessionNote note = new SessionNote(VALID_TEXT, RECORDED_AT);

        assertTrue(note.equals(note));
        assertTrue(note.equals(new SessionNote(" " + VALID_TEXT + " ", RECORDED_AT)));
        assertFalse(note.equals(null));
        assertFalse(note.equals(VALID_TEXT));
        assertFalse(note.equals(new SessionNote("Reviewed indices.", RECORDED_AT)));
        assertFalse(note.equals(new SessionNote(VALID_TEXT, RECORDED_AT.plusMinutes(1))));
    }

    @Test
    public void hashCode_equalNotes_sameHashCode() {
        SessionNote note = new SessionNote(VALID_TEXT, RECORDED_AT);

        assertEquals(note.hashCode(), new SessionNote(VALID_TEXT, RECORDED_AT).hashCode());
        assertNotEquals(note.hashCode(), new SessionNote("Reviewed indices.", RECORDED_AT).hashCode());
    }
}
