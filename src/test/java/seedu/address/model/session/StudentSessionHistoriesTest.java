package seedu.address.model.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.StudentId;

public class StudentSessionHistoriesTest {

    private static final StudentId ALEX_ID = ALEX.getId();
    private static final StudentId BEA_ID = BEA.getId();

    private final StudentSessionHistories histories = new StudentSessionHistories();

    @Test
    public void getHistory_studentWithoutNotes_returnsEmptyHistory() {
        assertEquals(SessionHistory.empty(), histories.getHistory(ALEX_ID));
    }

    @Test
    public void addNote_sameStudent_appendsNewestFirst() {
        histories.addNote(ALEX_ID, INDICES_NOTE);
        histories.addNote(ALEX_ID, FACTORISATION_NOTE);

        assertEquals(List.of(FACTORISATION_NOTE, INDICES_NOTE), histories.getHistory(ALEX_ID).getNotes());
    }

    @Test
    public void addNote_differentStudents_keepsHistoriesSeparate() {
        histories.addNote(ALEX_ID, INDICES_NOTE);
        histories.addNote(BEA_ID, FACTORISATION_NOTE);

        assertEquals(List.of(INDICES_NOTE), histories.getHistory(ALEX_ID).getNotes());
        assertEquals(List.of(FACTORISATION_NOTE), histories.getHistory(BEA_ID).getNotes());
    }

    @Test
    public void setHistory_nonEmptyHistory_replacesHistory() {
        histories.addNote(ALEX_ID, INDICES_NOTE);
        SessionHistory replacement = SessionHistory.empty().withNote(FACTORISATION_NOTE);

        histories.setHistory(ALEX_ID, replacement);

        assertEquals(replacement, histories.getHistory(ALEX_ID));
    }

    @Test
    public void setHistory_emptyHistory_removesEntry() {
        histories.addNote(ALEX_ID, INDICES_NOTE);

        histories.setHistory(ALEX_ID, SessionHistory.empty());

        assertEquals(new StudentSessionHistories(), histories);
    }

    @Test
    public void removeHistory_studentWithNotes_returnsRemovedCount() {
        histories.addNote(ALEX_ID, INDICES_NOTE);
        histories.addNote(ALEX_ID, FACTORISATION_NOTE);

        assertEquals(2, histories.removeHistory(ALEX_ID));
        assertTrue(histories.getHistory(ALEX_ID).isEmpty());
    }

    @Test
    public void removeHistory_studentWithoutNotes_returnsZero() {
        assertEquals(0, histories.removeHistory(ALEX_ID));
    }

    @Test
    public void clear_withHistories_removesAllHistories() {
        histories.addNote(ALEX_ID, INDICES_NOTE);
        histories.addNote(BEA_ID, FACTORISATION_NOTE);

        histories.clear();

        assertEquals(new StudentSessionHistories(), histories);
    }

    @Test
    public void methods_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> histories.getHistory(null));
        assertThrows(NullPointerException.class, () -> histories.addNote(null, INDICES_NOTE));
        assertThrows(NullPointerException.class, () -> histories.addNote(ALEX_ID, null));
        assertThrows(NullPointerException.class, () -> histories.setHistory(null, SessionHistory.empty()));
        assertThrows(NullPointerException.class, () -> histories.setHistory(ALEX_ID, null));
        assertThrows(NullPointerException.class, () -> histories.removeHistory(null));
    }

    @Test
    public void equals() {
        histories.addNote(ALEX_ID, INDICES_NOTE);
        StudentSessionHistories sameHistories = new StudentSessionHistories();
        sameHistories.addNote(ALEX_ID, INDICES_NOTE);
        StudentSessionHistories differentHistories = new StudentSessionHistories();
        differentHistories.addNote(BEA_ID, INDICES_NOTE);

        assertTrue(histories.equals(histories));
        assertTrue(histories.equals(sameHistories));
        assertEquals(histories.hashCode(), sameHistories.hashCode());
        assertFalse(histories.equals(null));
        assertFalse(histories.equals(INDICES_NOTE));
        assertFalse(histories.equals(differentHistories));
    }
}
