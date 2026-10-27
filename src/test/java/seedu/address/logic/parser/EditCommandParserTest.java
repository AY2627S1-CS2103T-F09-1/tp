package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

public class EditCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_allFieldsSpecified_success() {
        EditStudentDescriptor descriptor = descriptor("Alexis Tan", "8000 0001",
                Optional.of("guardian@example.com"), "Physics", "Secondary 4");

        assertParseSuccess(parser,
                " 2 n/Alexis Tan p/8000 0001 e/guardian@example.com sub/Physics l/Secondary 4",
                new EditCommand(INDEX_SECOND_PERSON, descriptor));
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setParentPhone("8000 0001");
        descriptor.setCurrentLevel(new CurrentLevel("Secondary 4"));

        assertParseSuccess(parser, " 1 p/8000 0001 l/Secondary 4",
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_eachSingleField_success() {
        EditStudentDescriptor name = new EditStudentDescriptor();
        name.setName(new StudentName("Alexis Tan"));
        assertParseSuccess(parser, " 1 n/Alexis Tan", new EditCommand(INDEX_FIRST_PERSON, name));

        EditStudentDescriptor phone = new EditStudentDescriptor();
        phone.setParentPhone("8000 0001");
        assertParseSuccess(parser, " 1 p/8000 0001", new EditCommand(INDEX_FIRST_PERSON, phone));

        EditStudentDescriptor email = new EditStudentDescriptor();
        email.setParentEmail(Optional.of("guardian@example.com"));
        assertParseSuccess(parser, " 1 e/guardian@example.com", new EditCommand(INDEX_FIRST_PERSON, email));

        EditStudentDescriptor subject = new EditStudentDescriptor();
        subject.setSubject(new Subject("E Math/A Math"));
        assertParseSuccess(parser, " 1 sub/E Math/A Math", new EditCommand(INDEX_FIRST_PERSON, subject));

        EditStudentDescriptor level = new EditStudentDescriptor();
        level.setCurrentLevel(new CurrentLevel("Secondary 4"));
        assertParseSuccess(parser, " 1 l/Secondary 4", new EditCommand(INDEX_FIRST_PERSON, level));
    }

    @Test
    public void parse_prefixLikeTextInSubjectAndLevel_success() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setSubject(new Subject("Mathematics nt/Science"));
        descriptor.setCurrentLevel(new CurrentLevel("Secondary nt/4"));

        assertParseSuccess(parser, " 1 sub/Mathematics nt/Science l/Secondary nt/4",
                new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_emptyEmail_success() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setParentEmail(Optional.empty());

        assertParseSuccess(parser, " 1 e/", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_missingIndexOrFields_failure() {
        assertParseFailure(parser, " n/Alexis Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1", withUsage(EditCommand.MESSAGE_NOT_EDITED));
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndexOrPreamble_failure() {
        assertParseFailure(parser, " 0 n/Alexis Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " -1 n/Alexis Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " x n/Alexis Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 extra n/Alexis Tan", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 2147483648 n/Alexis Tan", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, " 1 n/", withUsage(StudentName.MESSAGE_CONSTRAINTS));
        assertParseFailure(parser, " 1 p/12", withUsage(ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS));
        assertParseFailure(parser, " 1 e/example.com",
                withUsage(ParentGuardianContact.EMAIL_MESSAGE_CONSTRAINTS));
        assertParseFailure(parser, " 1 sub/", withUsage(Subject.MESSAGE_CONSTRAINTS));
        assertParseFailure(parser, " 1 l/", withUsage(CurrentLevel.MESSAGE_CONSTRAINTS));
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        String expectedMessage = withUsage(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_EMAIL));

        assertParseFailure(parser, " 1 n/Alexis Tan n/Blair Tan e/a@example.com e/b@example.com",
                expectedMessage);
    }

    @Test
    public void parse_legacyPrefix_failure() {
        assertParseFailure(parser, " 1 a/Block 1", unknownParameterMessage("a/"));
        assertParseFailure(parser, " 1 t/friend", unknownParameterMessage("t/"));
    }

    private static String withUsage(String message) {
        return message + "\n" + EditCommand.MESSAGE_USAGE;
    }

    private static String unknownParameterMessage(String prefix) {
        return withUsage(String.format(ParserUtil.MESSAGE_UNKNOWN_STUDENT_PARAMETER,
                prefix, ParserUtil.STUDENT_SUPPORTED_PARAMETERS));
    }

    private static EditStudentDescriptor descriptor(String name, String phone, Optional<String> email,
            String subject, String currentLevel) {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setName(new StudentName(name));
        descriptor.setParentPhone(phone);
        descriptor.setParentEmail(email);
        descriptor.setSubject(new Subject(subject));
        descriptor.setCurrentLevel(new CurrentLevel(currentLevel));
        return descriptor;
    }
}
