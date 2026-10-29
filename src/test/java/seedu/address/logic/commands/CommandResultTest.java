package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.student.StudentId;

public class CommandResultTest {

    private static final StudentId ALEX_ID = StudentId.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final StudentId BEA_ID = StudentId.fromString("123e4567-e89b-12d3-a456-426614174001");

    @Test
    public void profileResult_nullStudentId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", (StudentId) null));
    }

    @Test
    public void profileResult_studentId_exposesProfileRequest() {
        CommandResult commandResult = new CommandResult("feedback", ALEX_ID);

        assertEquals(ALEX_ID, commandResult.getProfileStudentId().orElseThrow());
        assertTrue(new CommandResult("feedback").getProfileStudentId().isEmpty());
    }

    @Test
    public void notePreviewResult_nullTarget_throwsNullPointerException() {
        Index noteNumber = Index.fromOneBased(2);

        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", null, noteNumber));
        assertThrows(NullPointerException.class, () -> new CommandResult("feedback", ALEX_ID, null));
    }

    @Test
    public void notePreviewResult_target_exposesProfileAndNoteNumber() {
        Index noteNumber = Index.fromOneBased(2);
        CommandResult commandResult = new CommandResult("feedback", ALEX_ID, noteNumber);

        assertEquals(ALEX_ID, commandResult.getProfileStudentId().orElseThrow());
        assertEquals(noteNumber, commandResult.getPreviewNoteNumber().orElseThrow());
        assertTrue(new CommandResult("feedback").getPreviewNoteNumber().isEmpty());
        assertTrue(new CommandResult("feedback", ALEX_ID).getPreviewNoteNumber().isEmpty());
    }

    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns true
        assertTrue(commandResult.equals(new CommandResult("feedback")));
        assertTrue(commandResult.equals(new CommandResult("feedback", false, false)));

        // same object -> returns true
        assertTrue(commandResult.equals(commandResult));

        // null -> returns false
        assertFalse(commandResult.equals(null));

        // different types -> returns false
        assertFalse(commandResult.equals(0.5f));

        // different feedbackToUser value -> returns false
        assertFalse(commandResult.equals(new CommandResult("different")));

        // different showHelp value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", true, false)));

        // different exit value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", false, true)));

        // different profileStudentId value -> returns false
        assertFalse(commandResult.equals(new CommandResult("feedback", ALEX_ID)));
        assertFalse(new CommandResult("feedback", ALEX_ID).equals(new CommandResult("feedback", BEA_ID)));

        CommandResult previewResult = new CommandResult("feedback", ALEX_ID, Index.fromOneBased(2));
        assertTrue(previewResult.equals(new CommandResult("feedback", ALEX_ID, Index.fromOneBased(2))));
        assertFalse(previewResult.equals(new CommandResult("feedback", ALEX_ID, Index.fromOneBased(1))));
        assertFalse(previewResult.equals(new CommandResult("feedback", BEA_ID, Index.fromOneBased(2))));
        assertFalse(previewResult.equals(new CommandResult("feedback", ALEX_ID)));
    }

    @Test
    public void hashcode() {
        CommandResult commandResult = new CommandResult("feedback");

        // same values -> returns same hashcode
        assertEquals(commandResult.hashCode(), new CommandResult("feedback").hashCode());

        // different feedbackToUser value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("different").hashCode());

        // different showHelp value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", true, false).hashCode());

        // different exit value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", false, true).hashCode());

        // different profileStudentId value -> returns different hashcode
        assertNotEquals(commandResult.hashCode(), new CommandResult("feedback", ALEX_ID).hashCode());
        assertEquals(new CommandResult("feedback", ALEX_ID, Index.fromOneBased(2)).hashCode(),
                new CommandResult("feedback", ALEX_ID, Index.fromOneBased(2)).hashCode());
    }

    @Test
    public void toStringMethod() {
        CommandResult commandResult = new CommandResult("feedback");
        String expected = CommandResult.class.getCanonicalName() + "{feedbackToUser="
                + commandResult.getFeedbackToUser() + ", showHelp=" + commandResult.isShowHelp()
                + ", exit=" + commandResult.isExit() + ", profileStudentId=null, previewNoteNumber=null}";
        assertEquals(expected, commandResult.toString());
    }
}
