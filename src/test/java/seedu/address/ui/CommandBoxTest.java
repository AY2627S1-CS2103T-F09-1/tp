package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class CommandBoxTest {

    @Test
    public void replaceControlCharactersWithSpaces_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CommandBox.replaceControlCharactersWithSpaces(null));
    }

    // boundary: nothing to replace
    @Test
    public void replaceControlCharactersWithSpaces_emptyText_returnsEmptyText() {
        assertEquals("", CommandBox.replaceControlCharactersWithSpaces(""));
    }

    @Test
    public void replaceControlCharactersWithSpaces_printableText_returnsSameText() {
        String text = "note 1 nt/Reviewed  factorisation, 50% done!";
        assertEquals(text, CommandBox.replaceControlCharactersWithSpaces(text));
    }

    @Test
    public void replaceControlCharactersWithSpaces_tab_replacedBySpace() {
        assertEquals("note 1 nt/Line one tabbed",
                CommandBox.replaceControlCharactersWithSpaces("note 1 nt/Line one\ttabbed"));
    }

    @Test
    public void replaceControlCharactersWithSpaces_lineFeed_replacedBySpace() {
        assertEquals("note 1 nt/First line Second line",
                CommandBox.replaceControlCharactersWithSpaces("note 1 nt/First line\nSecond line"));
    }

    @Test
    public void replaceControlCharactersWithSpaces_carriageReturn_replacedBySpace() {
        assertEquals("First line Second line",
                CommandBox.replaceControlCharactersWithSpaces("First line\rSecond line"));
    }

    @Test
    public void replaceControlCharactersWithSpaces_windowsLineBreak_replacedByOneSpace() {
        assertEquals("First line Second line",
                CommandBox.replaceControlCharactersWithSpaces("First line\r\nSecond line"));
    }

    // Each break is replaced on its own, so a blank line between two lines leaves two spaces.
    @Test
    public void replaceControlCharactersWithSpaces_consecutiveLineBreaks_replacedBySpaceEach() {
        assertEquals("First  Third", CommandBox.replaceControlCharactersWithSpaces("First\n\nThird"));
    }

    @Test
    public void replaceControlCharactersWithSpaces_otherControlCharacters_replacedBySpace() {
        assertEquals("a b c", CommandBox.replaceControlCharactersWithSpaces("a\u0007b\u007Fc"));
    }

    // boundary: control characters at the start and end of the text
    @Test
    public void replaceControlCharactersWithSpaces_controlCharactersAtEnds_replacedBySpace() {
        assertEquals(" list ", CommandBox.replaceControlCharactersWithSpaces("\tlist\n"));
    }

    // Non-breaking and zero-width spaces are not control characters, so the note parser still judges them.
    @Test
    public void replaceControlCharactersWithSpaces_invisibleNonControlCharacters_returnsSameText() {
        String text = "nt/ ​";
        assertEquals(text, CommandBox.replaceControlCharactersWithSpaces(text));
    }
}
