package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
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
    public void parse_prefixLikeTextInSubjectAndLevel_success() {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("+65 9123 4567", Optional.empty()),
                new Subject("Mathematics nt/Science"), new CurrentLevel("Secondary nt/4"));

        assertParseSuccess(parser, " n/Alicia Lim p/+65 9123 4567 sub/Mathematics nt/Science l/Secondary nt/4",
                new AddCommand(student));
    }

    @Test
    public void parse_missingLevel_failure() {
        assertParseFailure(parser, " n/Alicia Lim p/91234567 sub/Mathematics",
                withAddFormat("Missing l/CURRENT_LEVEL."));
    }

    @Test
    public void parse_emptyRequiredValue_failure() {
        assertParseFailure(parser, " n/Alicia Lim p/91234567 sub/Mathematics l/",
                withAddFormat("Missing l/CURRENT_LEVEL."));
    }

    @Test
    public void parse_nonEmptyPreamble_failure() {
        assertParseFailure(parser, " unexpected" + VALID_REQUIRED_ARGUMENTS,
                withAddFormat("Unexpected text before the first parameter: unexpected."));
    }

    @Test
    public void parse_noPrefixes_reportsUnexpectedTextNotMissingValue() {
        assertParseFailure(parser, " aaaaaa", withAddFormat("Unexpected text before the first parameter: aaaaaa."));
    }

    @Test
    public void parse_missingName_failure() {
        assertParseFailure(parser, " p/91234567 sub/Mathematics l/Secondary 3",
                withAddFormat("Missing n/NAME."));
    }

    @Test
    public void parse_missingPhone_failure() {
        assertParseFailure(parser, " n/Alicia Lim sub/Mathematics l/Secondary 3",
                withAddFormat("Missing p/PARENT_PHONE."));
    }

    @Test
    public void parse_missingSubject_failure() {
        assertParseFailure(parser, " n/Alicia Lim p/91234567 l/Secondary 3",
                withAddFormat("Missing sub/SUBJECT."));
    }

    @Test
    public void parse_invalidValue_failureWithoutFormat() {
        assertParseFailure(parser, " n/Alicia Lim p/12 sub/Mathematics l/Secondary 3",
                ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidName_failureWithoutFormat() {
        assertParseFailure(parser, " n/@lex p/91234567 sub/Mathematics l/Secondary 3",
                StudentName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidEmail_failureWithoutFormat() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " e/not-an-email",
                ParentGuardianContact.EMAIL_MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_repeatedName_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " n/Bea Lim",
                withAddFormat("n/ must appear at most once."));
    }

    @Test
    public void parse_repeatedPhone_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " p/98765432",
                withAddFormat(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE)));
    }

    @Test
    public void parse_repeatedSubject_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " sub/English",
                withAddFormat(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_SUBJECT)));
    }

    @Test
    public void parse_repeatedLevel_failure() {
        assertParseFailure(parser, VALID_REQUIRED_ARGUMENTS + " l/JC 1",
                withAddFormat(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_LEVEL)));
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
    public void parse_prefixLikeTextAfterLevel_success() {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("+65 9123 4567", Optional.empty()),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3 x/value"));

        assertParseSuccess(parser, VALID_REQUIRED_ARGUMENTS + " x/value", new AddCommand(student));
    }

    private static String getUnknownParameterMessage(String prefix) {
        return withAddFormat(String.format(AddCommandParser.MESSAGE_UNKNOWN_PARAMETER, prefix,
                AddCommandParser.MESSAGE_SUPPORTED_PARAMETERS));
    }

    private static String withAddFormat(String message) {
        return Messages.withFormat(message, AddCommand.COMMAND_FORMAT, AddCommand.COMMAND_EXAMPLE);
    }
}
