package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SessionHistoryTest {

    private static final OffsetDateTime EARLIER = OffsetDateTime.parse("2026-09-11T18:35:00+08:00");
    private static final OffsetDateTime LATER = OffsetDateTime.parse("2026-09-18T18:35:00+08:00");

    private static final SessionNote EARLIER_NOTE = new SessionNote("Reviewed indices.", EARLIER);
    private static final SessionNote LATER_NOTE = new SessionNote("Reviewed factorisation.", LATER);

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
    public void withNote_equalTimestamps_keepsInsertionOrder() {
        SessionNote first = new SessionNote("Started algebra worksheet.", LATER);
        SessionNote second = new SessionNote("Finished algebra worksheet.", LATER);

        SessionHistory history = SessionHistory.empty()
                .withNote(EARLIER_NOTE)
                .withNote(first)
                .withNote(second);

        assertEquals(List.of(first, second, EARLIER_NOTE), history.getNotes());
    }

    @Test
    public void withNote_sameInstantDifferentOffsets_keepsInsertionOrder() {
        SessionNote singapore = new SessionNote("Recorded in Singapore.", LATER);
        OffsetDateTime sameInstantInUtc = LATER.withOffsetSameInstant(ZoneOffset.UTC);
        SessionNote utc = new SessionNote("Recorded in UTC.", sameInstantInUtc);

        SessionHistory history = SessionHistory.empty().withNote(singapore).withNote(utc);

        assertEquals(List.of(singapore, utc), history.getNotes());
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
