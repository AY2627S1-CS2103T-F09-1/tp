package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class StudentIdentityTest {

    private static final StudentName NAME = new StudentName("Ari Tan");
    private static final ParentGuardianContact CONTACT =
            new ParentGuardianContact("9123 4567", Optional.empty());

    @Test
    public void matches_normalizedNameAndPhone_equalIdentitiesMatch() {
        StudentIdentity identity = new StudentIdentity(NAME, CONTACT);
        StudentIdentity equivalent = new StudentIdentity(new StudentName("ari tan"),
                new ParentGuardianContact("9123-4567", Optional.empty()));

        assertTrue(identity.matches(equivalent));
        assertEquals(identity, equivalent);
        assertEquals(identity.hashCode(), equivalent.hashCode());
    }

    @Test
    public void matches_differentNameOrPhone_doesNotMatch() {
        StudentIdentity identity = new StudentIdentity(NAME, CONTACT);

        assertFalse(identity.matches(new StudentIdentity(new StudentName("Bea Tan"), CONTACT)));
        assertFalse(identity.matches(new StudentIdentity(NAME,
                new ParentGuardianContact("9876 5432", Optional.empty()))));
    }

    @Test
    public void matches_nullOrDifferentType_handledSafely() {
        StudentIdentity identity = new StudentIdentity(NAME, CONTACT);

        assertThrows(NullPointerException.class, () -> identity.matches(null));
        assertFalse(identity.equals(null));
        assertFalse(identity.equals("Ari Tan"));
    }

    @Test
    public void getStableValue_normalizesAndSeparatesNameFromPhone() {
        // Characterisation test: legacy student IDs are derived from this value, so it must stay stable.
        StudentIdentity identity = new StudentIdentity(new StudentName("  ARI   Tan "),
                new ParentGuardianContact("+65 9123-4567", Optional.empty()));

        assertEquals("ari tan\u0000+6591234567", identity.getStableValue());
    }

    @Test
    public void getStableValue_equalIdentities_produceEqualLegacyIds() {
        StudentId firstId = StudentId.fromLegacyIdentity(new StudentIdentity(NAME, CONTACT));
        StudentId secondId = StudentId.fromLegacyIdentity(new StudentIdentity(new StudentName("ari tan"),
                new ParentGuardianContact("9123-4567", Optional.empty())));

        assertEquals(firstId, secondId);
    }

    @Test
    public void studentId_roundTripsThroughString() {
        StudentId stableId = new StudentId(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        assertEquals(stableId.toString(), stableId.getValue());
        assertEquals(stableId, StudentId.fromString(stableId.getValue()));
        assertEquals(stableId.hashCode(), StudentId.fromString(stableId.getValue()).hashCode());
        assertFalse(stableId.equals(null));
        assertThrows(IllegalArgumentException.class, () -> StudentId.fromString("not-a-uuid"));
    }

    @Test
    public void studentId_nullInputs_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> StudentId.fromLegacyIdentity(null));
        assertThrows(NullPointerException.class, () -> StudentId.fromString(null));
    }
}
