package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;

public class AddCommandTest {

    @Test
    public void constructor_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand((Student) null));
    }

    @Test
    public void execute_studentAcceptedByModel_addSuccessful() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.addStudent(ALEX);

        assertCommandSuccess(new AddCommand(ALEX), model,
                String.format(AddCommand.MESSAGE_STUDENT_SUCCESS, ALEX.getName(), ALEX.getSubject(),
                        ALEX.getCurrentLevel()), expectedModel);
    }

    @Test
    public void execute_duplicateStudent_throwsCommandException() {
        Model model = new ModelManager();
        model.addStudent(ALEX);

        assertCommandFailure(new AddCommand(ALEX), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_STUDENT, ALEX.getName()));
    }

    @Test
    public void equals() {
        AddCommand addAlexCommand = new AddCommand(ALEX);
        AddCommand addBeaCommand = new AddCommand(BEA);

        assertTrue(addAlexCommand.equals(addAlexCommand));
        assertTrue(addAlexCommand.equals(new AddCommand(ALEX)));
        assertFalse(addAlexCommand.equals(1));
        assertFalse(addAlexCommand.equals(null));
        assertFalse(addAlexCommand.equals(addBeaCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALEX);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALEX + "}";
        assertEquals(expected, addCommand.toString());
    }
}
