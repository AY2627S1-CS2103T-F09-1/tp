package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

public class AddStudentCommandParserTest {

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_studentArguments_success() {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("+65 9123 4567", Optional.of("mrs.lim@example.com")),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"));
        assertParseSuccess(parser, " n/Alicia Lim p/+65 9123 4567 e/mrs.lim@example.com sub/Mathematics l/Secondary 3",
                new AddCommand(student));
    }

    @Test
    public void parse_missingLevel_failure() {
        assertParseFailure(parser, " n/Alicia Lim p/91234567 sub/Mathematics",
                String.format(seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
