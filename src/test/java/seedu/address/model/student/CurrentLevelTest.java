package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CurrentLevelTest {

    @Test
    public void constructor_invalidCurrentLevel_throwsIllegalArgumentException() {
        assertThrows(NullPointerException.class, () -> new CurrentLevel(null));
        assertThrows(IllegalArgumentException.class, () -> new CurrentLevel("   "));
        assertThrows(IllegalArgumentException.class, () -> new CurrentLevel("???"));
        assertThrows(IllegalArgumentException.class, () -> new CurrentLevel("Secondary\r3"));
    }

    @Test
    public void constructor_validCurrentLevel_normalizesWhitespace() {
        assertEquals("Secondary 3", new CurrentLevel(" Secondary   3 ").getValue());
        assertEquals("P6+", new CurrentLevel("P6+").getValue());
    }

    @Test
    public void isValidCurrentLevel() {
        assertThrows(NullPointerException.class, () -> CurrentLevel.isValidCurrentLevel(null));
        // equivalence partition: blank or line-break content is rejected
        assertFalse(CurrentLevel.isValidCurrentLevel(""));
        assertFalse(CurrentLevel.isValidCurrentLevel("   "));
        assertFalse(CurrentLevel.isValidCurrentLevel("\r"));
        assertFalse(CurrentLevel.isValidCurrentLevel("JC\n1"));
        // boundary: shortest non-blank value is accepted
        assertTrue(CurrentLevel.isValidCurrentLevel("P"));
        assertTrue(CurrentLevel.isValidCurrentLevel("Secondary 3"));
    }

    @Test
    public void equals_distinguishesValueAndType() {
        CurrentLevel level = new CurrentLevel("Secondary 3");

        assertEquals(level, new CurrentLevel("Secondary 3"));
        assertEquals(level.hashCode(), new CurrentLevel("Secondary 3").hashCode());
        assertEquals("Secondary 3", level.toString());
        assertFalse(level.equals(null));
        assertFalse(level.equals("Secondary 3"));
        assertFalse(level.equals(new CurrentLevel("JC 1")));
    }
}
