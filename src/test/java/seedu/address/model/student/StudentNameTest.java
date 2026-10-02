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
        assertFalse(StudentName.isValidName(""));
        assertFalse(StudentName.isValidName(" "));
        assertFalse(StudentName.isValidName("Ana@Lee"));
        assertTrue(StudentName.isValidName("Ana Lee"));
        assertTrue(StudentName.isValidName("Élodie Tan 2nd"));
    }
}
