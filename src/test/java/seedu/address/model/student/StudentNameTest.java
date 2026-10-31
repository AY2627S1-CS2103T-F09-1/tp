package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class StudentNameTest {

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new StudentName(null));
        assertThrows(IllegalArgumentException.class, () -> new StudentName(""));
        assertThrows(IllegalArgumentException.class, () -> new StudentName("   "));
        assertThrows(IllegalArgumentException.class, () -> new StudentName("!Learner"));
    }

    @Test
    public void constructor_validName_normalizesWhitespace() {
        StudentName studentName = new StudentName("  Ana   O'Neil-Smith.  ");

        assertEquals("Ana O'Neil-Smith.", studentName.getValue());
    }

    @Test
    public void isValidName() {
        // equivalence partition: invalid first character
        assertFalse(StudentName.isValidName(""));
        assertFalse(StudentName.isValidName(" "));
        assertFalse(StudentName.isValidName("'Ana"));
        assertFalse(StudentName.isValidName("-Ana"));
        assertFalse(StudentName.isValidName(".Ana"));
        assertFalse(StudentName.isValidName("Ana@Lee"));
        // equivalence partition: valid first character, allowed inner symbols
        assertTrue(StudentName.isValidName("A"));
        assertTrue(StudentName.isValidName("Ana Lee"));
        assertTrue(StudentName.isValidName("Ana O'Neil-Smith Jr."));
        assertTrue(StudentName.isValidName("Raj s/o Kumar"));
        assertTrue(StudentName.isValidName("Élodie Tan 2nd"));
    }

    @Test
    public void equals_distinguishesValueAndType() {
        StudentName name = new StudentName("Ari Tan");

        assertEquals(name, new StudentName("Ari Tan"));
        assertEquals(name.hashCode(), new StudentName("Ari Tan").hashCode());
        assertEquals("Ari Tan", name.toString());
        assertFalse(name.equals(null));
        assertFalse(name.equals("Ari Tan"));
        assertFalse(name.equals(new StudentName("Bea Tan")));
    }
}
