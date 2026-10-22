package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

public class AddCommandParserTest {

    private static final String VALID_REQUIRED_ARGUMENTS =
            " n/Alicia Lim p/+65 9123 4567 sub/Mathematics l/Secondary 3";

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allStudentArguments_success() {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("+65 9123 4567", Optional.of("mrs.lim@example.com")),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"));

        assertParseSuccess(parser, VALID_REQUIRED_ARGUMENTS + " e/mrs.lim@example.com", new AddCommand(student));
    }

    @Test
    public void parse_optionalEmailMissing_success() {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("+65 9123 4567", Optional.empty()),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"));

        assertParseSuccess(parser, VALID_REQUIRED_ARGUMENTS, new AddCommand(student));
    }

    @Test
    public void parse_subjectWithSlash_success() {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("+65 9123 4567", Optional.empty()),
                new Subject("E Math/A Math"), new CurrentLevel("Secondary 3"));

        assertParseSuccess(parser, " n/Alicia Lim p/+65 9123 4567 sub/E Math/A Math l/Secondary 3",
                new AddCommand(student));
    }

    @Test
    public void parse_missingLevel_failure() {
        assertParseFailure(parser, " n/Alicia Lim p/91234567 sub/Mathematics",
                "Value missing for parameter: l/\n" + AddCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_emptyRequiredValue_failure() {
        assertParseFailure(parser, " n/Alicia Lim p/91234567 sub/Mathematics l/",
                "Value missing for parameter: l/\n" + AddCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, " unexpected" + VALID_REQUIRED_ARGUMENTS,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_repeatedName_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " n/Bea Lim",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME) + "\n" + AddCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_legacyPersonArguments_failure() {
        String legacyPersonArguments =
                " n/Amy Bee p/11111111 e/amy@example.com a/Block 312, Amy Street 1 t/friend";

        assertParseFailure(parser, legacyPersonArguments,
                getUnknownParameterMessage("a/"));
    }

    @Test
    public void parse_studentArgumentsWithLegacyPersonPrefix_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " a/Block 312, Amy Street 1 t/friend",
                getUnknownParameterMessage("a/"));
    }

    @Test
    public void parse_unknownPrefix_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " x/value",
                getUnknownParameterMessage("x/"));
    }

    private static String getUnknownParameterMessage(String prefix) {
        return String.format(AddCommandParser.MESSAGE_UNKNOWN_PARAMETER, prefix,
                AddCommandParser.MESSAGE_SUPPORTED_PARAMETERS)
                + "\n" + AddCommand.MESSAGE_USAGE;
    }
}
