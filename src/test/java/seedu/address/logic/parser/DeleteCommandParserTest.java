package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;

/** Tests parsing of student and session-note deletion commands. */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_validConfirmation_returnsConfirmedDeleteCommand() {
        assertParseSuccess(parser, "1 /confirm", new DeleteCommand(INDEX_FIRST_PERSON, true));
    }

    @Test
    public void parse_validSessionNoteArgs_returnsDeleteSessionNoteCommand() {
        assertParseSuccess(parser, "note 1 2",
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        assertParseSuccess(parser, "note 1 2 /confirm",
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, true));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        String[] invalidArguments = {"", "0", "-1", "1.5", "a", "1 2", "1-2", "2147483648",
            "1/confirm", "/confirm 1", "1 /confirm extra", "1 /confirm /confirm", "1 /Confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, expectedMessage);
        }
    }

    @Test
    public void parse_missingSessionNoteArgs_throwsSpecificParseException() {
        String[] invalidArguments = {"note", "note 1"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS);
        }
    }

    @Test
    public void parse_extraSessionNoteArgs_throwsSpecificParseException() {
        String[] invalidArguments = {"note 1 2 extra extra", "note 1 2 /confirm /confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS);
        }
    }

    @Test
    public void parse_misplacedOrInvalidConfirmationFlag_throwsSpecificParseException() {
        String[] invalidArguments = {"note /confirm 2", "note 1 /confirm", "note 1 2 extra", "note 1 2 /Confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_INVALID_CONFIRMATION_FLAG);
        }
    }

    @Test
    public void parse_invalidSessionNoteStudentIndex_throwsSpecificParseException() {
        String[] invalidArguments = {"note 0 1", "note -1 1", "note one 1", "note 2147483648 1"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_INVALID_STUDENT_INDEX);
        }
    }

    @Test
    public void parse_invalidSessionNoteNumber_throwsSpecificParseException() {
        String[] invalidArguments = {"note 1 0", "note 1 -1", "note 1 1.5", "note 1 2147483648"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_INVALID_NOTE_NUMBER);
        }
    }
}
