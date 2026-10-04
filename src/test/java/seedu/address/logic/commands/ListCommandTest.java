package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(student("Alex Tan", "Mathematics", "Secondary 3"));
        addressBook.addStudent(student("Bea Tan", "English", "JC 1"));
        model = new ModelManager(addressBook, new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_populatedRoster_returnsStudentCount() {
        assertCommandSuccess(new ListCommand(), model, String.format(ListCommand.MESSAGE_SUCCESS, 2), expectedModel);
    }

    @Test
    public void execute_emptyRoster_returnsRecoveryMessage() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        assertCommandSuccess(new ListCommand(), emptyModel, ListCommand.MESSAGE_EMPTY_ROSTER,
                new ModelManager(emptyModel.getAddressBook(), new UserPrefs()));
    }

    private Student student(String name, String subject, String level) {
        return new Student(new StudentName(name), new ParentGuardianContact("9123 4567", Optional.empty()),
                new Subject(subject), new CurrentLevel(level));
    }
}
