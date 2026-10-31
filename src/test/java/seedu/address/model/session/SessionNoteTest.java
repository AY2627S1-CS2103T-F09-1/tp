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
    private static final OffsetDateTime EDITED_AT = OffsetDateTime.parse("2026-10-23T14:10:00+08:00");
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
    public void messageConstraints_namesLengthLimits() {
        assertEquals("Session notes must be 1 to 500 printable characters on one line.",
                SessionNote.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void constructor_editedNote_keepsRecordedAndEditedTimes() {
        SessionNote note = new SessionNote(VALID_TEXT, RECORDED_AT, EDITED_AT);

        assertEquals(RECORDED_AT, note.getRecordedAt());
        assertEquals(EDITED_AT, note.getLastEditedAt().orElseThrow());
    }

    @Test
    public void constructor_lastEditedBeforeRecorded_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, SessionNote.EDITED_AT_CONSTRAINTS, () -> new SessionNote(
                VALID_TEXT, RECORDED_AT, RECORDED_AT.minusSeconds(1)));
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
    public void isValidText_onlyInvisibleCharacters_returnsFalse() {
        assertFalse(SessionNote.isValidText(" "));
        assertFalse(SessionNote.isValidText("​"));
        assertFalse(SessionNote.isValidText("   ​﻿ "));
    }

    @Test
    public void isValidText_visibleTextAroundInvisibleCharacters_returnsTrue() {
        assertTrue(SessionNote.isValidText("Reviewed indices."));
        assertTrue(SessionNote.isValidText("​Reviewed indices. "));
    }

    @Test
    public void hasVisibleCharacter_variousTexts_returnsWhetherAnyCharacterIsVisible() {
        assertFalse(SessionNote.hasVisibleCharacter(""));
        assertFalse(SessionNote.hasVisibleCharacter(" \t  ​﻿"));
        assertTrue(SessionNote.hasVisibleCharacter(" a "));
        assertTrue(SessionNote.hasVisibleCharacter("😀"));
    }

    @Test
    public void constructor_onlyInvisibleCharacters_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, SessionNote.MESSAGE_CONSTRAINTS, () -> new SessionNote(
                " ​", RECORDED_AT));
    }

    @Test
    public void isValidText_lengthBoundaries_acceptsMinimumToMaximum() {
        assertFalse(SessionNote.isValidText("a".repeat(SessionNote.MIN_TEXT_LENGTH - 1)));
        assertTrue(SessionNote.isValidText("a".repeat(SessionNote.MIN_TEXT_LENGTH)));
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
        assertFalse(SessionNote.isValidText("Line one\u2028Line two"));
        assertFalse(SessionNote.isValidText("Para one\u2029Para two"));
        assertFalse(SessionNote.isValidText("Bell\u0007"));
    }

    @Test
    public void countTextLength_surroundingWhitespaceAndSurrogatePairs_countsStrippedCodePoints() {
        assertEquals(0, SessionNote.countTextLength("   "));
        assertEquals(3, SessionNote.countTextLength("  a b  "));
        assertEquals(2, SessionNote.countTextLength("😀😀"));
    }

    @Test
    public void hasNonPrintableCharacter_internalControlCharacter_returnsTrue() {
        assertTrue(SessionNote.hasNonPrintableCharacter("Column\tvalue"));
        assertTrue(SessionNote.hasNonPrintableCharacter("Line one\u2028Line two"));
    }

    @Test
    public void hasNonPrintableCharacter_surroundingWhitespaceOnly_returnsFalse() {
        assertFalse(SessionNote.hasNonPrintableCharacter("\tReviewed indices.\n"));
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
        assertFalse(note.equals(new SessionNote(VALID_TEXT, RECORDED_AT, EDITED_AT)));
    }

    @Test
    public void hashCode_equalNotes_sameHashCode() {
        SessionNote note = new SessionNote(VALID_TEXT, RECORDED_AT);

        assertEquals(note.hashCode(), new SessionNote(VALID_TEXT, RECORDED_AT).hashCode());
        assertNotEquals(note.hashCode(), new SessionNote("Reviewed indices.", RECORDED_AT).hashCode());
        assertNotEquals(note.hashCode(), new SessionNote(VALID_TEXT, RECORDED_AT, EDITED_AT).hashCode());
    }
}
