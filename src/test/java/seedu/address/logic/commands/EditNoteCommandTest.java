package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
