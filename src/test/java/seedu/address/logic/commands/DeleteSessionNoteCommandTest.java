package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class DeleteSessionNoteCommandTest {

    private static final Index NOTE_ONE = Index.fromOneBased(1);
    private static final Index NOTE_TWO = Index.fromOneBased(2);

    @Test
    public void execute_validNumbers_returnsConfirmationWithoutDeletingNote() {
        Model model = createStudentModelWithNotes();
        DeleteSessionNoteCommand command = new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_TWO);
        String expectedMessage = String.format(DeleteSessionNoteCommand.MESSAGE_CONFIRM_DELETE,
                NOTE_TWO.getOneBased(), ALEX.getName(), INDEX_FIRST_PERSON.getOneBased());
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(command, model, new CommandResult(expectedMessage, ALEX.getId(), NOTE_TWO),
                expectedModel);
    }

    @Test
    public void execute_studentWithoutNotes_throwsCommandException() {
        Model model = createStudentModel();
        DeleteSessionNoteCommand command = new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_ONE);

        assertCommandFailure(command, model,
                String.format(DeleteSessionNoteCommand.MESSAGE_NO_SESSION_NOTES, ALEX.getName()));
    }

    @Test
    public void execute_noteNumberOutOfRange_throwsCommandException() {
        Model model = createStudentModelWithNotes();
        Index outOfRangeNote = Index.fromOneBased(3);
        DeleteSessionNoteCommand command = new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, outOfRangeNote);

        assertCommandFailure(command, model, String.format(DeleteSessionNoteCommand.MESSAGE_INVALID_NOTE_NUMBER,
                outOfRangeNote.getOneBased(), ALEX.getName(), 2));
    }

    @Test
    public void execute_studentIndexOutOfRange_throwsCommandException() {
        Model model = createStudentModelWithNotes();
        Index outOfRangeStudent = Index.fromOneBased(model.getStudentRoster().size() + 1);
        DeleteSessionNoteCommand command = new DeleteSessionNoteCommand(outOfRangeStudent, NOTE_ONE);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_confirmedRequestWithoutLogicManager_throwsCommandException() {
        Model model = createStudentModelWithNotes();
        DeleteSessionNoteCommand command = new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_ONE, true);

        assertCommandFailure(command, model, DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION);
    }

    @Test
    public void equals() {
        DeleteSessionNoteCommand deleteFirstNote =
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_ONE);
        DeleteSessionNoteCommand deleteSecondNote =
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_TWO);
        DeleteSessionNoteCommand deleteOtherStudentNote =
                new DeleteSessionNoteCommand(INDEX_SECOND_PERSON, NOTE_ONE);
        DeleteSessionNoteCommand confirmedDeleteFirstNote =
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_ONE, true);

        assertTrue(deleteFirstNote.equals(deleteFirstNote));
        assertTrue(deleteFirstNote.equals(new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_ONE)));
        assertFalse(deleteFirstNote.equals(deleteSecondNote));
        assertFalse(deleteFirstNote.equals(deleteOtherStudentNote));
        assertFalse(deleteFirstNote.equals(confirmedDeleteFirstNote));
        assertFalse(deleteFirstNote.equals(null));
        assertFalse(deleteFirstNote.equals(1));
    }

    @Test
    public void toStringMethod() {
        DeleteSessionNoteCommand command = new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, NOTE_TWO, true);

        String expected = DeleteSessionNoteCommand.class.getCanonicalName()
                + "{studentIndex=" + INDEX_FIRST_PERSON + ", noteNumber=" + NOTE_TWO + ", isConfirmed=true}";
        assertEquals(expected, command.toString());
    }

    private static Model createStudentModel() {
        Model model = new ModelManager();
        model.addStudent(BEA);
        model.addStudent(ALEX);
        return model;
    }

    private static Model createStudentModelWithNotes() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(BEA);
        addressBook.addStudent(ALEX);
        addressBook.addSessionNote(ALEX.getId(), INDICES_NOTE);
        addressBook.addSessionNote(ALEX.getId(), FACTORISATION_NOTE);
        return new ModelManager(addressBook, new UserPrefs());
    }
}
