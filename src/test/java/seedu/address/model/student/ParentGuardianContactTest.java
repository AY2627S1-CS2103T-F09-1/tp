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
        assertThrows(IllegalArgumentException.class, () -> new ParentGuardianContact("10101010", Optional.empty()));
        assertThrows(IllegalArgumentException.class, () -> new ParentGuardianContact("9123 4567",
                Optional.of("not-an-email")));
    }

    @Test
    public void constructor_validValues_normalizesSingaporeCountryCodeAndOptionalEmail() {
        ParentGuardianContact contact = new ParentGuardianContact(" +65 9123-4567 ",
                Optional.of(" guardian@example.com "));

        assertEquals("91234567", contact.getPhone());
        assertEquals(Optional.of("guardian@example.com"), contact.getEmail());
        assertEquals("91234567", contact.getNormalizedPhone());
    }

    @Test
    public void constructor_nonSingaporeInternationalPhone_preservesPhone() {
        ParentGuardianContact contact = new ParentGuardianContact(" +61 412-345-678 ", Optional.empty());

        assertEquals("+61 412-345-678", contact.getPhone());
    }

    @Test
    public void constructor_nonLocalSingaporePhone_preservesPhone() {
        ParentGuardianContact contact = new ParentGuardianContact("+65 1234 5678", Optional.empty());

        assertEquals("+65 1234 5678", contact.getPhone());
    }

    @Test
    public void isValidPhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParentGuardianContact.isValidPhone(null));
    }

    @Test
    public void isValidPhone_localNumberDigitBoundaries_acceptsOnlyEightDigits() {
        assertFalse(ParentGuardianContact.isValidPhone("9123456")); // seven digits
        assertTrue(ParentGuardianContact.isValidPhone("91234567")); // eight digits
        assertFalse(ParentGuardianContact.isValidPhone("912345678")); // nine digits
    }

    @Test
    public void isValidPhone_localNumberLeadingDigit_acceptsOnlySixEightOrNine() {
        assertFalse(ParentGuardianContact.isValidPhone("10101010"));
        assertFalse(ParentGuardianContact.isValidPhone("7123 4567"));
        assertTrue(ParentGuardianContact.isValidPhone("61234567"));
        assertTrue(ParentGuardianContact.isValidPhone("81234567"));
    }

    @Test
    public void isValidPhone_internationalNumberDigitBoundaries_acceptsEightToFifteenDigits() {
        assertFalse(ParentGuardianContact.isValidPhone("+1234567")); // seven digits
        assertTrue(ParentGuardianContact.isValidPhone("+12345678")); // eight digits
        assertTrue(ParentGuardianContact.isValidPhone("+123456789012345")); // fifteen digits
        assertFalse(ParentGuardianContact.isValidPhone("+1234567890123456")); // sixteen digits
    }

    @Test
    public void isValidPhone_separators_areIgnoredForLengthButMustFollowDigits() {
        assertTrue(ParentGuardianContact.isValidPhone("9123 4567"));
        assertTrue(ParentGuardianContact.isValidPhone("+65-9123-4567"));
        assertFalse(ParentGuardianContact.isValidPhone("+ 9123"));
        assertFalse(ParentGuardianContact.isValidPhone("+65 9123 4567 8901 23"));
    }

    @Test
    public void isValidPhone_tooShortOrInvalidCharacters_returnsFalse() {
        assertFalse(ParentGuardianContact.isValidPhone(""));
        assertFalse(ParentGuardianContact.isValidPhone("12"));
        assertFalse(ParentGuardianContact.isValidPhone("+12"));
        assertFalse(ParentGuardianContact.isValidPhone("+65 91234"));
        assertFalse(ParentGuardianContact.isValidPhone("9123abc"));
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
