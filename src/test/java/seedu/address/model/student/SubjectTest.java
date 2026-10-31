package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_invalidSubject_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
        assertThrows(IllegalArgumentException.class, () -> new Subject("   "));
        assertThrows(IllegalArgumentException.class, () -> new Subject("!!!"));
        assertThrows(IllegalArgumentException.class, () -> new Subject("Mathematics\nAdvanced"));
    }

    @Test
    public void constructor_validSubject_normalizesWhitespace() {
        assertEquals("English Literature", new Subject(" English   Literature ").getValue());
        assertEquals("C++", new Subject("C++").getValue());
    }

    @Test
    public void isValidSubject() {
        assertThrows(NullPointerException.class, () -> Subject.isValidSubject(null));
        // equivalence partition: blank or line-break content is rejected
        assertFalse(Subject.isValidSubject(""));
        assertFalse(Subject.isValidSubject("   "));
        assertFalse(Subject.isValidSubject("\n"));
        assertFalse(Subject.isValidSubject("Maths\rScience"));
        // boundary: shortest non-blank value is accepted
        assertTrue(Subject.isValidSubject("A"));
        assertTrue(Subject.isValidSubject("E Math/A Math"));
    }

    @Test
    public void equals_distinguishesValueAndType() {
        Subject subject = new Subject("Mathematics");

        assertEquals(subject, new Subject("Mathematics"));
        assertEquals(subject.hashCode(), new Subject("Mathematics").hashCode());
        assertEquals("Mathematics", subject.toString());
        assertFalse(subject.equals(null));
        assertFalse(subject.equals("Mathematics"));
        assertFalse(subject.equals(new Subject("English")));
    }
}
