package seedu.address.logic.parser;

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

    private static final String MESSAGE_SUPPORTED_PARAMETERS = "n/ p/ e/ sub/ l/";

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
    public void parse_emptyEmail_success() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setParentEmail(Optional.empty());

        assertParseSuccess(parser, " 1 e/", new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_missingIndexOrFields_failure() {
        String missingIndexMessage = Messages.withFormat("Missing INDEX.", EditCommand.COMMAND_FORMAT);

        assertParseFailure(parser, " n/Alexis Tan", missingIndexMessage);
        assertParseFailure(parser, " 1", withEditFormat(EditCommand.MESSAGE_NOT_EDITED));
        assertParseFailure(parser, "", missingIndexMessage);
    }

    @Test
    public void parse_invalidIndexOrPreamble_failure() {
        String invalidIndexMessage = "INDEX must be a positive whole number, e.g. 1.";

        assertParseFailure(parser, " 0 n/Alexis Tan", invalidIndexMessage);
        assertParseFailure(parser, " -1 n/Alexis Tan", invalidIndexMessage);
        assertParseFailure(parser, " x n/Alexis Tan", invalidIndexMessage);
        assertParseFailure(parser, " 1 extra n/Alexis Tan",
                Messages.withFormat("edit takes only one INDEX.", EditCommand.COMMAND_FORMAT));
        assertParseFailure(parser, " 2147483648 n/Alexis Tan",
                "INDEX is too large. Enter a student index shown in the roster.");
    }

    @Test
    public void parse_invalidValues_failure() {
        assertParseFailure(parser, " 1 n/", StudentName.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 p/12", ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 e/example.com",
                ParentGuardianContact.EMAIL_MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 sub/", Subject.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 l/", CurrentLevel.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefix_failure() {
        String expectedMessage =
                withEditFormat(Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_EMAIL));

        assertParseFailure(parser, " 1 n/Alexis Tan n/Blair Tan e/a@example.com e/b@example.com",
                expectedMessage);
    }

    @Test
    public void parse_unknownOrLegacyPrefix_failure() {
        assertParseFailure(parser, " 1 n/Alexis Tan x/value", unknownParameterMessage("x/"));
        assertParseFailure(parser, " 1 a/Block 1", unknownParameterMessage("a/"));
        assertParseFailure(parser, " 1 t/friend", unknownParameterMessage("t/"));
    }

    private static String withEditFormat(String message) {
        return Messages.withFormat(message, EditCommand.COMMAND_FORMAT);
    }

    private static String unknownParameterMessage(String prefix) {
        return withEditFormat(String.format(AddCommandParser.MESSAGE_UNKNOWN_PARAMETER,
                prefix, MESSAGE_SUPPORTED_PARAMETERS));
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
