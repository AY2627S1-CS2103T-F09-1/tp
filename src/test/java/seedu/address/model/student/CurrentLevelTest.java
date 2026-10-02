package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CurrentLevelTest {

    @Test
    public void constructor_invalidCurrentLevel_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new CurrentLevel(null));
        assertThrows(IllegalArgumentException.class, () -> new CurrentLevel("   "));
        assertThrows(IllegalArgumentException.class, () -> new CurrentLevel("Secondary\r3"));
    }

    @Test
    public void constructor_validCurrentLevel_normalizesWhitespace() {
        assertEquals("Secondary 3", new CurrentLevel(" Secondary   3 ").getValue());
    }
}
