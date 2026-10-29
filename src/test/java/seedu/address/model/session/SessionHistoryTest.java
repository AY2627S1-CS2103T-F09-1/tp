package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;

public class SessionHistoryTest {

    private static final OffsetDateTime EARLIER = OffsetDateTime.parse("2026-09-11T18:35:00+08:00");
    private static final OffsetDateTime LATER = OffsetDateTime.parse("2026-09-18T18:35:00+08:00");

    private static final SessionNote EARLIER_NOTE = new SessionNote("Reviewed indices.", EARLIER);
    private static final SessionNote LATER_NOTE = new SessionNote("Reviewed factorisation.", LATER);
    private static final SessionNote FIRST_TIED_NOTE = new SessionNote("Started algebra worksheet.", LATER);
    private static final SessionNote SECOND_TIED_NOTE = new SessionNote("Finished algebra worksheet.", LATER);

    @Test
    public void of_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SessionHistory.of(null));
    }

    @Test
    public void of_emptyList_returnsSharedEmptyHistory() {
        assertSame(SessionHistory.empty(), SessionHistory.of(List.of()));
    }

    @Test
    public void of_notesNewestFirst_keepsGivenOrder() {
        List<SessionNote> notes = List.of(SECOND_TIED_NOTE, FIRST_TIED_NOTE, EARLIER_NOTE);

        assertEquals(notes, SessionHistory.of(notes).getNotes());
    }

    @Test
    public void of_notesOutOfOrder_sortsNewestFirstKeepingOrderOfEqualTimestamps() {
        List<SessionNote> notes = List.of(EARLIER_NOTE, SECOND_TIED_NOTE, FIRST_TIED_NOTE);

        assertEquals(List.of(SECOND_TIED_NOTE, FIRST_TIED_NOTE, EARLIER_NOTE),
                SessionHistory.of(notes).getNotes());
    }

    @Test
    public void of_givenListModified_historyUnchanged() {
        List<SessionNote> notes = new ArrayList<>(List.of(EARLIER_NOTE));
        SessionHistory history = SessionHistory.of(notes);

        notes.add(LATER_NOTE);

        assertEquals(List.of(EARLIER_NOTE), history.getNotes());
    }

    @Test
    public void empty_noNotes() {
        SessionHistory history = SessionHistory.empty();

        assertTrue(history.isEmpty());
        assertEquals(0, history.size());
        assertEquals(List.of(), history.getNotes());
    }

    @Test
    public void withNote_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> SessionHistory.empty().withNote(null));
    }

    @Test
    public void withNote_addedInChronologicalOrder_newestFirst() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE).withNote(LATER_NOTE);

        assertEquals(List.of(LATER_NOTE, EARLIER_NOTE), history.getNotes());
        assertEquals(2, history.size());
        assertFalse(history.isEmpty());
    }

    @Test
    public void withNote_addedOutOfOrder_newestFirst() {
        SessionHistory history = SessionHistory.empty().withNote(LATER_NOTE).withNote(EARLIER_NOTE);

        assertEquals(List.of(LATER_NOTE, EARLIER_NOTE), history.getNotes());
    }

    @Test
    public void withNote_equalTimestamps_laterNoteNumberedAfterEarlierNote() {
        SessionHistory history = SessionHistory.empty()
                .withNote(EARLIER_NOTE)
                .withNote(FIRST_TIED_NOTE)
                .withNote(SECOND_TIED_NOTE);

        assertEquals(List.of(SECOND_TIED_NOTE, FIRST_TIED_NOTE, EARLIER_NOTE), history.getNotes());
        assertEquals(FIRST_TIED_NOTE, history.getNote(Index.fromOneBased(2)));
        assertEquals(SECOND_TIED_NOTE, history.getNote(Index.fromOneBased(3)));
    }

    @Test
    public void withNote_sameInstantDifferentOffsets_laterNoteNumberedAfterEarlierNote() {
        SessionNote singapore = new SessionNote("Recorded in Singapore.", LATER);
        OffsetDateTime sameInstantInUtc = LATER.withOffsetSameInstant(ZoneOffset.UTC);
        SessionNote utc = new SessionNote("Recorded in UTC.", sameInstantInUtc);

        SessionHistory history = SessionHistory.empty().withNote(singapore).withNote(utc);

        assertEquals(List.of(utc, singapore), history.getNotes());
    }

    @Test
    public void withNote_differentOffsets_ordersByInstant() {
        // 18:00 UTC is later than 18:35 at +08:00 (10:35 UTC) despite the earlier clock reading.
        OffsetDateTime laterInstantEarlierClock = OffsetDateTime.parse("2026-09-18T18:00:00Z");
        SessionNote laterInstant = new SessionNote("Recorded later.", laterInstantEarlierClock);

        SessionHistory history = SessionHistory.empty().withNote(LATER_NOTE).withNote(laterInstant);

        assertEquals(List.of(laterInstant, LATER_NOTE), history.getNotes());
    }

    @Test
    public void withNote_identicalNotes_bothKept() {
        SessionHistory history = SessionHistory.empty().withNote(LATER_NOTE).withNote(LATER_NOTE);

        assertEquals(List.of(LATER_NOTE, LATER_NOTE), history.getNotes());
    }

    @Test
    public void withNote_originalHistory_unchanged() {
        SessionHistory original = SessionHistory.empty().withNote(EARLIER_NOTE);

        original.withNote(LATER_NOTE);

        assertEquals(List.of(EARLIER_NOTE), original.getNotes());
        assertTrue(SessionHistory.empty().isEmpty());
    }

    @Test
    public void getNote_chronologicalNumber_returnsSelectedNote() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE).withNote(LATER_NOTE);

        assertEquals(EARLIER_NOTE, history.getNote(Index.fromOneBased(1)));
        assertEquals(LATER_NOTE, history.getNote(Index.fromOneBased(2)));
    }

    @Test
    public void withoutNote_chronologicalNumber_removesOnlySelectedNote() {
        SessionNote middleNote = new SessionNote("Practised completing the square.", EARLIER.plusDays(2));
        SessionHistory history = SessionHistory.empty()
                .withNote(EARLIER_NOTE)
                .withNote(middleNote)
                .withNote(LATER_NOTE);

        SessionHistory updatedHistory = history.withoutNote(Index.fromOneBased(2));

        assertEquals(List.of(LATER_NOTE, EARLIER_NOTE), updatedHistory.getNotes());
        assertEquals(List.of(LATER_NOTE, middleNote, EARLIER_NOTE), history.getNotes());
    }

    @Test
    public void withoutNote_lastNote_returnsSharedEmptyHistory() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE);

        assertSame(SessionHistory.empty(), history.withoutNote(Index.fromOneBased(1)));
    }

    @Test
    public void withoutNote_identicalNotes_removesOneOccurrence() {
        SessionHistory history = SessionHistory.empty().withNote(LATER_NOTE).withNote(LATER_NOTE);

        assertEquals(List.of(LATER_NOTE), history.withoutNote(Index.fromOneBased(1)).getNotes());
    }

    @Test
    public void withEditedNote_replacesTextAndPreservesTimestampAndPosition() {
        SessionNote middleNote = new SessionNote("Practised completing the square.", EARLIER.plusDays(2));
        SessionHistory history = SessionHistory.empty()
                .withNote(EARLIER_NOTE)
                .withNote(middleNote)
                .withNote(LATER_NOTE);

        SessionHistory editedHistory = history.withEditedNote(Index.fromOneBased(2), "Corrected summary.");
        SessionNote editedNote = editedHistory.getNote(Index.fromOneBased(2));

        assertEquals("Corrected summary.", editedNote.getText());
        assertEquals(middleNote.getRecordedAt(), editedNote.getRecordedAt());
        assertEquals(List.of(LATER_NOTE, editedNote, EARLIER_NOTE), editedHistory.getNotes());
        assertEquals(List.of(LATER_NOTE, middleNote, EARLIER_NOTE), history.getNotes());
    }

    @Test
    public void withEditedNote_invalidTextOrNumber_throwsAndKeepsHistory() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE);

        assertThrows(IllegalArgumentException.class, SessionNote.MESSAGE_CONSTRAINTS, () -> history
                .withEditedNote(Index.fromOneBased(1), " "));
        assertThrows(IndexOutOfBoundsException.class, () -> history
                .withEditedNote(Index.fromOneBased(2), "Corrected summary."));
        assertEquals(List.of(EARLIER_NOTE), history.getNotes());
    }

    @Test
    public void noteSelection_invalidArguments_throwsException() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE);

        assertThrows(NullPointerException.class, () -> history.getNote(null));
        assertThrows(NullPointerException.class, () -> history.withoutNote(null));
        assertThrows(NullPointerException.class, () -> history.withEditedNote(null, "Corrected summary."));
        assertThrows(NullPointerException.class, () -> history.withEditedNote(Index.fromOneBased(1), null));
        assertThrows(IndexOutOfBoundsException.class, () -> history.getNote(Index.fromOneBased(2)));
        assertThrows(IndexOutOfBoundsException.class, () -> history.withoutNote(Index.fromOneBased(2)));
        assertThrows(IndexOutOfBoundsException.class, () -> SessionHistory.empty()
                .withoutNote(Index.fromOneBased(1)));
    }

    @Test
    public void getNotes_modification_throwsUnsupportedOperationException() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE);

        assertThrows(UnsupportedOperationException.class, () -> history.getNotes().add(LATER_NOTE));
        assertThrows(UnsupportedOperationException.class, () -> SessionHistory.empty().getNotes().clear());
    }

    @Test
    public void empty_calledTwice_sameInstance() {
        assertSame(SessionHistory.empty(), SessionHistory.empty());
    }

    @Test
    public void equals() {
        SessionHistory history = SessionHistory.empty().withNote(EARLIER_NOTE);

        assertTrue(history.equals(history));
        assertTrue(history.equals(SessionHistory.empty().withNote(EARLIER_NOTE)));
        assertFalse(history.equals(null));
        assertFalse(history.equals(EARLIER_NOTE));
        assertFalse(history.equals(SessionHistory.empty()));
        assertFalse(history.equals(SessionHistory.empty().withNote(LATER_NOTE)));
    }

    @Test
    public void hashCode_equalHistories_sameHashCode() {
        assertEquals(SessionHistory.empty().withNote(EARLIER_NOTE).hashCode(),
                SessionHistory.empty().withNote(EARLIER_NOTE).hashCode());
    }
}
