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

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = createStudentModel();

    @Test
    public void execute_validIndex_returnsConfirmationWithoutDeletingStudent() {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE,
                ALEX.getName(), 0, "session notes", INDEX_FIRST_PERSON.getOneBased());

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, new CommandResult(expectedMessage, ALEX.getId()), expectedModel);
    }

    @Test
    public void execute_studentWithOneNote_usesSingularNoteLabel() {
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE,
                ALEX.getName(), 1, "session note", INDEX_FIRST_PERSON.getOneBased());
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, new CommandResult(expectedMessage, ALEX.getId()), expectedModel);
    }

    @Test
    public void execute_secondStudent_returnsTargetProfileWithoutDeletingStudent() {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE,
                BEA.getName(), 0, "session notes", INDEX_SECOND_PERSON.getOneBased());
        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        assertCommandSuccess(deleteCommand, model, new CommandResult(expectedMessage, BEA.getId()), expectedModel);
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getStudentRoster().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_confirmedRequestWithoutLogicManager_throwsCommandException() {
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON, true);

        assertCommandFailure(deleteCommand, model, DeleteCommand.MESSAGE_NO_PENDING_DELETION);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);
        DeleteCommand confirmedDeleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON, true);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different student -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // different confirmation state -> returns false
        assertFalse(deleteFirstCommand.equals(confirmedDeleteFirstCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex, true);
        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetIndex=" + targetIndex + ", isConfirmed=true}";
        assertEquals(expected, deleteCommand.toString());
    }

    private static Model createStudentModel() {
        Model studentModel = new ModelManager();
        studentModel.addStudent(BEA);
        studentModel.addStudent(ALEX);
        return studentModel;
    }
}
