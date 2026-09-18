package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void equals() {
        Remark remark = new Remark("Likes swimming");

        assertTrue(remark.equals(remark));
        assertTrue(remark.equals(new Remark("Likes swimming")));
        assertFalse(remark.equals(null));
        assertFalse(remark.equals(5));
        assertFalse(remark.equals(new Remark("Prefers email")));
    }
}
