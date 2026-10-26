package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.model.session.SessionNote;

public class EditNoteCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE);

    private final EditNoteCommandParser parser = new EditNoteCommandParser();

    @Test
    public void parse_validArgs_returnsEditNoteCommand() {
        assertParseSuccess(parser, "note 1 2 nt/Corrected summary.",
                new EditNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2), "Corrected summary."));
    }

    @Test
    public void parse_surroundingAndInternalSpaces_trimsSurroundingSpacesOnly() {
        assertParseSuccess(parser, "  note   1  2   nt/  Corrected  summary.  ",
                new EditNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2), "Corrected  summary."));
    }

    @Test
    public void parse_missingOrExtraArguments_throwsParseException() {
        String[] invalidArguments = {"", "note", "note 1", "note 1 2", "1 2 nt/text", "note 1 2 extra nt/text"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_invalidIndices_throwsSpecificParseException() {
        String[] invalidStudentIndices = {"0", "-1", "x"};
        for (String invalidIndex : invalidStudentIndices) {
            assertParseFailure(parser, "note " + invalidIndex + " 1 nt/text",
                    "STUDENT_INDEX must be a positive integer.\n" + EditNoteCommand.MESSAGE_USAGE);
        }

        String[] invalidNoteNumbers = {"0", "-1", "x"};
        for (String invalidNumber : invalidNoteNumbers) {
            assertParseFailure(parser, "note 1 " + invalidNumber + " nt/text",
                    "NOTE_NUMBER must be a positive integer.\n" + EditNoteCommand.MESSAGE_USAGE);
        }
    }

    @Test
    public void parse_oversizedIndices_throwsTooLargeParseException() {
        assertParseFailure(parser, "note 99999999999 1 nt/text",
                "STUDENT_INDEX is too large. Enter a smaller positive integer.\n"
                        + EditNoteCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "note 1 99999999999 nt/text",
                "NOTE_NUMBER is too large. Enter a smaller positive integer.\n"
                        + EditNoteCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidText_throwsParseException() {
        assertParseFailure(parser, "note 1 1 nt/", SessionNote.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "note 1 1 nt/" + "a".repeat(SessionNote.MAX_TEXT_LENGTH + 1),
                SessionNote.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateNotePrefix_throwsParseException() {
        assertParseFailure(parser, "note 1 1 nt/First nt/Second",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NOTE));
    }
}
