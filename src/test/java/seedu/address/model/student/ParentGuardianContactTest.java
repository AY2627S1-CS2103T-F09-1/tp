package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ParentGuardianContactTest {

    @Test
    public void constructor_invalidValues_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new ParentGuardianContact(null, Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new ParentGuardianContact("12", Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new ParentGuardianContact("9123 4567",
                Optional.of("not-an-email")));
    }

    @Test
    public void constructor_validValues_preservesPhoneAndOptionalEmail() {
        ParentGuardianContact contact = new ParentGuardianContact(" +65 9123-4567 ",
                Optional.of(" guardian@example.com "));

        assertEquals("+65 9123-4567", contact.getPhone());
        assertEquals(Optional.of("guardian@example.com"), contact.getEmail());
        assertEquals("+6591234567", contact.getNormalizedPhone());
    }

    @Test
    public void isValidPhone() {
        assertFalse(ParentGuardianContact.isValidPhone(""));
        assertFalse(ParentGuardianContact.isValidPhone("+12"));
        assertFalse(ParentGuardianContact.isValidPhone("9123abc"));
        assertFalse(ParentGuardianContact.isValidPhone("+ 9123"));
        assertTrue(ParentGuardianContact.isValidPhone("9123 4567"));
        assertTrue(ParentGuardianContact.isValidPhone("+65-9123-4567"));
    }

    @Test
    public void isValidEmail() {
        assertFalse(ParentGuardianContact.isValidEmail(""));
        assertFalse(ParentGuardianContact.isValidEmail("guardian.example.com"));
        assertFalse(ParentGuardianContact.isValidEmail("guardian@"));
        assertTrue(ParentGuardianContact.isValidEmail("guardian@example.com"));
    }
}
