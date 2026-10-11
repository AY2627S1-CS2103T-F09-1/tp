package seedu.address.model.profile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.session.SessionNote;

public class StudentProfileTest {

    @Test
    public void constructor_validFields_exposesImmutableSnapshot() {
        List<SessionNote> notes = new ArrayList<>(List.of(FACTORISATION_NOTE, INDICES_NOTE));

        StudentProfile profile = profileOfAlex(notes);
        notes.clear();

        assertEquals(ALEX.getId(), profile.getStudentId());
        assertEquals(ALEX.getName(), profile.getName());
        assertEquals(ALEX.getParentGuardianContact(), profile.getParentGuardianContact());
        assertEquals(ALEX.getSubject(), profile.getSubject());
        assertEquals(ALEX.getCurrentLevel(), profile.getCurrentLevel());
        assertEquals(List.of(FACTORISATION_NOTE, INDICES_NOTE), profile.getSessionNotes());
        assertThrows(UnsupportedOperationException.class, () -> profile.getSessionNotes().clear());
    }

    @Test
    public void constructor_nullField_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentProfile(null, ALEX.getName(),
                ALEX.getParentGuardianContact(), ALEX.getSubject(), ALEX.getCurrentLevel(), List.of()));
        assertThrows(NullPointerException.class, () -> new StudentProfile(ALEX.getId(), ALEX.getName(),
                ALEX.getParentGuardianContact(), ALEX.getSubject(), ALEX.getCurrentLevel(), null));
    }

    @Test
    public void equals() {
        StudentProfile profile = profileOfAlex(List.of(FACTORISATION_NOTE));
        StudentProfile equalProfile = profileOfAlex(List.of(FACTORISATION_NOTE));

        assertTrue(profile.equals(profile));
        assertTrue(profile.equals(equalProfile));
        assertFalse(profile.equals(null));
        assertFalse(profile.equals(ALEX));
        assertFalse(profile.equals(new StudentProfile(BEA.getId(), ALEX.getName(),
                ALEX.getParentGuardianContact(), ALEX.getSubject(), ALEX.getCurrentLevel(),
                List.of(FACTORISATION_NOTE))));
        assertFalse(profile.equals(new StudentProfile(ALEX.getId(), BEA.getName(),
                ALEX.getParentGuardianContact(), ALEX.getSubject(), ALEX.getCurrentLevel(),
                List.of(FACTORISATION_NOTE))));
        assertFalse(profile.equals(new StudentProfile(ALEX.getId(), ALEX.getName(),
                BEA.getParentGuardianContact(), ALEX.getSubject(), ALEX.getCurrentLevel(),
                List.of(FACTORISATION_NOTE))));
        assertFalse(profile.equals(new StudentProfile(ALEX.getId(), ALEX.getName(),
                ALEX.getParentGuardianContact(), BEA.getSubject(), ALEX.getCurrentLevel(),
                List.of(FACTORISATION_NOTE))));
        assertFalse(profile.equals(new StudentProfile(ALEX.getId(), ALEX.getName(),
                ALEX.getParentGuardianContact(), ALEX.getSubject(), BEA.getCurrentLevel(),
                List.of(FACTORISATION_NOTE))));
        assertFalse(profile.equals(profileOfAlex(List.of(INDICES_NOTE))));
        assertEquals(profile.hashCode(), equalProfile.hashCode());
    }

    private StudentProfile profileOfAlex(List<SessionNote> notes) {
        return new StudentProfile(ALEX.getId(), ALEX.getName(), ALEX.getParentGuardianContact(), ALEX.getSubject(),
                ALEX.getCurrentLevel(), notes);
    }
}
