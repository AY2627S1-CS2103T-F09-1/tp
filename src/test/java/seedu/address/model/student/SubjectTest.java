package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SubjectTest {

    @Test
    public void constructor_invalidSubject_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new Subject(null));
        assertThrows(IllegalArgumentException.class, () -> new Subject("   "));
        assertThrows(IllegalArgumentException.class, () -> new Subject("Mathematics\nAdvanced"));
    }

    @Test
    public void constructor_validSubject_normalizesWhitespace() {
        assertEquals("English Literature", new Subject(" English   Literature ").getValue());
    }
}
