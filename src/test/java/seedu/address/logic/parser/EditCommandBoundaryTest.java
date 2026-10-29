package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.StudentName;

/**
 * Characterizes edit parsing at input boundaries and when invalid inputs compete.
 */
public class EditCommandBoundaryTest {
    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_phoneAtMinimumLength_acceptsThreeDigitsAndRejectsTwo() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setParentPhone("123");

        assertParseSuccess(parser, " 1 p/123", new EditCommand(INDEX_FIRST_PERSON, descriptor));
        assertParseFailure(parser, " 1 p/12", ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicatePrefixAndInvalidValue_reportsDuplicateFirst() {
        String message = Messages.withFormat("p/ must appear at most once.",
                EditCommand.COMMAND_FORMAT, EditCommand.COMMAND_EXAMPLE);

        assertParseFailure(parser, " 1 p/12 p/123", message);
    }

    @Test
    public void parse_invalidNameAndPhone_reportsNameFirst() {
        assertParseFailure(parser, " 1 n/ p/12", StudentName.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void constructor_descriptorChangedLater_preservesOriginalEmailEdit() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setParentEmail(Optional.empty());
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        EditStudentDescriptor original = new EditStudentDescriptor(descriptor);
        descriptor.setParentEmail(Optional.of("guardian@example.com"));

        assertEquals(new EditCommand(INDEX_FIRST_PERSON, original), command);
    }
}
