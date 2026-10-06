package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

public class ViewCommandTest {

    private ModelManager model;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(BEA);
        addressBook.addStudent(ALEX);
        model = new ModelManager(addressBook, new UserPrefs());
    }

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewCommand(null));
    }

    @Test
    public void execute_validIndex_returnsStableIdOfRosterStudent() throws Exception {
        CommandResult result = new ViewCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals(String.format(ViewCommand.MESSAGE_SUCCESS, ALEX.getName()), result.getFeedbackToUser());
        assertEquals(ALEX.getId(), result.getProfileStudentId().orElseThrow());
        assertEquals(List.of(ALEX.getId(), BEA.getId()), model.getStudentRoster().getEntries().stream()
                .map(entry -> entry.getStudentId()).toList());
    }

    @Test
    public void execute_indexOutOfRange_throwsCommandException() {
        Index outOfRangeIndex = Index.fromOneBased(model.getStudentRoster().size() + 1);

        assertCommandFailure(new ViewCommand(outOfRangeIndex), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyRoster_throwsCommandException() {
        ModelManager emptyModel = new ModelManager();

        assertCommandFailure(new ViewCommand(INDEX_FIRST_PERSON), emptyModel,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        assertTrue(viewCommand.equals(viewCommand));
        assertTrue(viewCommand.equals(new ViewCommand(INDEX_FIRST_PERSON)));
        assertFalse(viewCommand.equals(null));
        assertFalse(viewCommand.equals(new ListCommand()));
        assertFalse(viewCommand.equals(new ViewCommand(INDEX_SECOND_PERSON)));
    }

    @Test
    public void toStringMethod() {
        ViewCommand viewCommand = new ViewCommand(INDEX_FIRST_PERSON);

        assertEquals(ViewCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}",
                viewCommand.toString());
    }
}
