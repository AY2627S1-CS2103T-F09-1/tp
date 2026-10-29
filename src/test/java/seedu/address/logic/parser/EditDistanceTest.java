package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EditDistanceTest {

    // Equivalence partition: identical strings, including the empty string, need no edits.
    @Test
    public void between_identicalStrings_returnsZero() {
        assertEquals(0, EditDistance.between("note", "note"));
        assertEquals(0, EditDistance.between("", ""));
    }

    // Boundary values: an empty string on one side costs the full length of the other.
    @Test
    public void between_oneEmptyString_returnsLengthOfOther() {
        assertEquals(4, EditDistance.between("", "note"));
        assertEquals(4, EditDistance.between("note", ""));
    }

    // Equivalence partitions: one insertion, one deletion and one substitution each cost exactly one.
    @Test
    public void between_singleEdit_returnsOne() {
        assertEquals(1, EditDistance.between("nte", "note"));
        assertEquals(1, EditDistance.between("notes", "note"));
        assertEquals(1, EditDistance.between("nota", "note"));
    }

    @Test
    public void between_adjacentCharactersSwapped_returnsTwo() {
        assertEquals(2, EditDistance.between("ntoe", "note"));
    }

    @Test
    public void between_completelyDifferentStrings_returnsLongerLength() {
        assertEquals(4, EditDistance.between("wxyz", "note"));
        assertEquals(5, EditDistance.between("wxyzq", "note"));
    }

    @Test
    public void between_swappedArguments_returnsSameDistance() {
        assertEquals(EditDistance.between("nots", "note"), EditDistance.between("note", "nots"));
        assertEquals(EditDistance.between("no", "note"), EditDistance.between("note", "no"));
    }

    @Test
    public void between_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> EditDistance.between(null, "note"));
        assertThrows(NullPointerException.class, () -> EditDistance.between("note", null));
    }
}
