package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        assertEquals("91234567", contact.getNormalizedPhone());
    }

    @Test
    public void isValidPhone() {
        assertThrows(NullPointerException.class, () -> ParentGuardianContact.isValidPhone(null));
        // boundary: fewer than three digits is rejected, exactly three is accepted
        assertFalse(ParentGuardianContact.isValidPhone(""));
        assertFalse(ParentGuardianContact.isValidPhone("12"));
        assertFalse(ParentGuardianContact.isValidPhone("+12"));
        assertTrue(ParentGuardianContact.isValidPhone("123"));
        assertTrue(ParentGuardianContact.isValidPhone("1 2 3"));
        assertTrue(ParentGuardianContact.isValidPhone("+123"));
        // equivalence partition: unsupported characters are rejected
        assertFalse(ParentGuardianContact.isValidPhone("9123abc"));
        assertFalse(ParentGuardianContact.isValidPhone("+ 9123"));
        assertTrue(ParentGuardianContact.isValidPhone("9123 4567"));
        assertTrue(ParentGuardianContact.isValidPhone("+65-9123-4567"));
    }

    @Test
    public void isValidEmail() {
        assertThrows(NullPointerException.class, () -> ParentGuardianContact.isValidEmail(null));
        // equivalence partition: missing or extra '@', or whitespace, is rejected
        assertFalse(ParentGuardianContact.isValidEmail(""));
        assertFalse(ParentGuardianContact.isValidEmail("a@b"));
        assertFalse(ParentGuardianContact.isValidEmail("guardian.example.com"));
        assertFalse(ParentGuardianContact.isValidEmail("guardian@"));
        assertFalse(ParentGuardianContact.isValidEmail("@example.com"));
        assertFalse(ParentGuardianContact.isValidEmail("a@b@example.com"));
        assertFalse(ParentGuardianContact.isValidEmail("guardian @example.com"));
        assertTrue(ParentGuardianContact.isValidEmail("guardian@example.com"));
    }

    @Test
    public void equals_distinguishesPhoneEmailPresenceAndType() {
        ParentGuardianContact withEmail =
                new ParentGuardianContact("9123 4567", Optional.of("guardian@example.com"));
        ParentGuardianContact withoutEmail = new ParentGuardianContact("9123 4567", Optional.empty());

        // one difference at a time
        assertEquals(withEmail, new ParentGuardianContact("9123 4567", Optional.of("guardian@example.com")));
        assertEquals(withEmail.hashCode(),
                new ParentGuardianContact("9123 4567", Optional.of("guardian@example.com")).hashCode());
        assertEquals(withoutEmail, new ParentGuardianContact("9123 4567", Optional.empty()));
        assertEquals(withoutEmail.hashCode(),
                new ParentGuardianContact("9123 4567", Optional.empty()).hashCode());
        assertNotEquals(withEmail, withoutEmail);
        assertNotEquals(withEmail,
                new ParentGuardianContact("9123 4567", Optional.of("other@example.com")));
        assertNotEquals(withoutEmail,
                new ParentGuardianContact("9876 5432", Optional.empty()));
        assertFalse(withEmail.equals(null));
        assertFalse(withEmail.equals("9123 4567"));
    }
}
