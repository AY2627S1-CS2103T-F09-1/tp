package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;

/** Tests parsing of student and session-note deletion commands. */
public class DeleteCommandParserTest {

    private static final String MESSAGE_INVALID_INDEX = "INDEX must be a positive whole number, e.g. 1.";

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
    public void parse_tabSeparatedArgs_returnsDeleteCommands() {
        assertParseSuccess(parser, "1\t/confirm", new DeleteCommand(INDEX_FIRST_PERSON, true));
        assertParseSuccess(parser, "note\t1\t2",
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        assertParseSuccess(parser, " note \t1\t 2\t/confirm ",
                new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, true));
    }

    @Test
    public void parse_missingStudentIndex_throwsMissingIndexWithFormat() {
        String[] invalidArguments = {"", "   ", "/confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_MISSING_STUDENT_INDEX);
        }
    }

    @Test
    public void parse_invalidStudentIndex_throwsInvalidIndex() {
        String[] invalidArguments = {"0", "-1", "1.5", "a", "1-2", "1/confirm", "0 /confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_oversizedStudentIndex_throwsTooLarge() {
        assertParseFailure(parser, "2147483648",
                "INDEX is too large. Enter a student index shown in the roster.");
    }

    @Test
    public void parse_severalStudentValues_throwsOnlyOneIndexWithFormat() {
        String[] invalidArguments = {"1 2", "1 2 /confirm", "1 /Confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_TOO_MANY_STUDENT_INDICES);
        }
    }

    @Test
    public void parse_misplacedStudentConfirmationFlag_throwsSpecificParseException() {
        String[] invalidArguments = {"/confirm 1", "1 /confirm extra", "1 /confirm /confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, DeleteCommandParser.MESSAGE_INVALID_STUDENT_CONFIRMATION_FLAG);
        }
    }

    @Test
    public void parse_missingSessionNoteArgs_throwsSpecificParseException() {
        assertParseFailure(parser, "note", DeleteCommandParser.MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS);
        assertParseFailure(parser, "note 1", DeleteCommandParser.MESSAGE_MISSING_NOTE_NUMBER);
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
        String[] invalidArguments = {"note /confirm", "note /confirm 2", "note 1 /confirm", "note 1 2 extra",
            "note 1 2 /Confirm"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument,
                    DeleteCommandParser.MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG);
        }
    }

    @Test
    public void parse_invalidSessionNoteStudentIndex_throwsSpecificParseException() {
        String[] invalidArguments = {"note 0 1", "note -1 1", "note one 1"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, "STUDENT_INDEX must be a positive whole number, e.g. 1.");
        }
        assertParseFailure(parser, "note 2147483648 1",
                "STUDENT_INDEX is too large. Enter a student index shown in the roster.");
    }

    @Test
    public void parse_invalidSessionNoteNumber_throwsSpecificParseException() {
        String[] invalidArguments = {"note 1 0", "note 1 -1", "note 1 1.5"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, "NOTE_NUMBER must be a positive whole number, e.g. 1.");
        }
        assertParseFailure(parser, "note 1 2147483648",
                "NOTE_NUMBER is too large. Enter a note number shown in the profile.");
    }

    // Boundary value analysis on argument count (1 to 5) crossed with the position of the confirmation flag.
    @Test
    public void parse_flagInEachPositionOfFourArguments_throwsInvalidFlag() {
        String[] misplacedFlags = {"note /confirm 1 2", "note 1 /confirm 2"};

        for (String misplacedFlag : misplacedFlags) {
            assertParseFailure(parser, misplacedFlag,
                    DeleteCommandParser.MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG);
        }
    }

    @Test
    public void parse_fiveArgumentsWithMisplacedFlag_reportsExtraArgumentsFirst() {
        assertParseFailure(parser, "note /confirm 1 2 extra",
                DeleteCommandParser.MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS);
    }

    // Error precedence: the argument count is checked before any index is parsed.
    @Test
    public void parse_missingNoteNumberAndInvalidStudentIndex_reportsMissingNoteNumber() {
        assertParseFailure(parser, "note abc", DeleteCommandParser.MESSAGE_MISSING_NOTE_NUMBER);
    }

    @Test
    public void parse_confirmationFlagAloneOrRepeatedForStudent_throwsSpecificParseException() {
        assertParseFailure(parser, "/confirm", DeleteCommandParser.MESSAGE_MISSING_STUDENT_INDEX);
        assertParseFailure(parser, "/confirm /confirm",
                DeleteCommandParser.MESSAGE_INVALID_STUDENT_CONFIRMATION_FLAG);
    }

    @Test
    public void parse_noteTargetAfterStudentIndex_isTreatedAsSecondStudentIndex() {
        assertParseFailure(parser, "1 note", DeleteCommandParser.MESSAGE_TOO_MANY_STUDENT_INDICES);
    }

}
