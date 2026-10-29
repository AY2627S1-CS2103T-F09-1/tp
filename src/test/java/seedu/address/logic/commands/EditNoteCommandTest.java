package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.model.session.SessionNote;

public class EditNoteCommandTest {

    private final ModelManager model = new ModelManager();

    @Test
    public void execute_validRequest_replacesSelectedNote() throws Exception {
        model.addStudent(ALEX);
        SessionNote originalNote = model.addSessionNote(ALEX.getId(), "Original summary.");
        EditNoteCommand command = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(1),
                "Corrected summary.");

        CommandResult result = command.execute(model);

        assertEquals(String.format(EditNoteCommand.MESSAGE_SUCCESS, 1, ALEX.getName()), result.getFeedbackToUser());
        SessionNote editedNote = model.getSessionHistory(ALEX.getId()).getNote(Index.fromOneBased(1));
        assertEquals("Corrected summary.", editedNote.getText());
        assertEquals(originalNote.getRecordedAt(), editedNote.getRecordedAt());
    }

    @Test
    public void execute_invalidStudentIndex_throwsCommandException() {
        model.addStudent(ALEX);
        model.addStudent(BEA);
        model.addSessionNote(ALEX.getId(), "Original summary.");
        EditNoteCommand command = new EditNoteCommand(Index.fromOneBased(3), Index.fromOneBased(1),
                "Corrected summary.");

        Executable executeInvalidCommand = () -> command.execute(model);
        assertThrows(CommandException.class, String.format(Messages.MESSAGE_STUDENT_INDEX_OUT_OF_RANGE, 3, 2),
                executeInvalidCommand);
    }

    @Test
    public void execute_emptyRoster_throwsCommandException() {
        EditNoteCommand command = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(1),
                "Corrected summary.");

        assertThrows(CommandException.class, Messages.MESSAGE_EMPTY_ROSTER, () -> command.execute(model));
    }

    @Test
    public void execute_missingNote_throwsCommandException() {
        model.addStudent(ALEX);
        EditNoteCommand command = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(1),
                "Corrected summary.");

        assertThrows(CommandException.class, String.format(EditNoteCommand.MESSAGE_NO_SESSION_NOTES,
                ALEX.getName()), () -> command.execute(model));
    }

    @Test
    public void execute_noteNumberOutsideHistory_preservesHistory() throws Exception {
        model.addStudent(ALEX);
        SessionNote originalNote = model.addSessionNote(ALEX.getId(), "Original summary.");
        EditNoteCommand command = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(2),
                "Corrected summary.");

        String expectedMessage = String.format(EditNoteCommand.MESSAGE_INVALID_NOTE_NUMBER, 2, ALEX.getName(), 1);
        Executable executeInvalidCommand = () -> command.execute(model);
        assertThrows(CommandException.class, expectedMessage, executeInvalidCommand);

        SessionNote unchangedNote = model.getSessionHistory(ALEX.getId()).getNote(Index.fromOneBased(1));
        assertEquals(originalNote, unchangedNote);
    }

    @Test
    public void constructor_nullArgument_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EditNoteCommand(null, Index.fromOneBased(1), "summary"));
        Executable nullNoteNumber = () -> new EditNoteCommand(Index.fromOneBased(1), null, "summary");
        Executable nullText = () -> new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(1), null);
        assertThrows(NullPointerException.class, nullNoteNumber);
        assertThrows(NullPointerException.class, nullText);
    }

    @Test
    public void accessorsAndEquality_validCommands_returnValuesAndRedactedString() {
        EditNoteCommand command = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(2),
                "Corrected summary.");
        EditNoteCommand equalCommand = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(2),
                "Corrected summary.");
        EditNoteCommand differentCommand = new EditNoteCommand(Index.fromOneBased(2), Index.fromOneBased(2),
                "Corrected summary.");
        EditNoteCommand differentNoteNumber = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(3),
                "Corrected summary.");
        EditNoteCommand differentText = new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(2),
                "Another summary.");

        assertEquals(Index.fromOneBased(1), command.getStudentIndex());
        assertEquals(Index.fromOneBased(2), command.getNoteNumber());
        assertEquals("Corrected summary.", command.getText());
        assertEquals(command, equalCommand);
        assertEquals(command, command);
        assertFalse(command.equals(null));
        assertFalse(command.equals("not a command"));
        assertFalse(command.equals(differentCommand));
        assertFalse(command.equals(differentNoteNumber));
        assertFalse(command.equals(differentText));
        assertTrue(command.toString().contains("[REDACTED]"));
        assertFalse(command.toString().contains("Corrected summary."));
    }
}
