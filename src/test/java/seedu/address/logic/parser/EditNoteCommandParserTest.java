package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.model.session.SessionNote;

public class EditNoteCommandParserTest {

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
    public void parse_tabSeparatedSelectors_returnsEditNoteCommand() {
        assertParseSuccess(parser, "note\t1\t2 nt/Corrected summary.",
                new EditNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2), "Corrected summary."));
    }

    @Test
    public void parse_missingTarget_throwsMissingTargetWithFormat() {
        String[] invalidArguments = {"", "1 2 nt/text", "notes 1 2 nt/text"};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, EditNoteCommandParser.MESSAGE_MISSING_NOTE_TARGET);
        }
    }

    @Test
    public void parse_missingNewNote_throwsMissingNewNoteWithFormat() {
        String[] invalidArguments = {"note", "note 1", "note 1 2", "note 1 2 Corrected summary."};

        for (String invalidArgument : invalidArguments) {
            assertParseFailure(parser, invalidArgument, EditNoteCommandParser.MESSAGE_MISSING_NEW_NOTE);
        }
    }

    @Test
    public void parse_missingIndices_throwsMissingIndicesWithFormat() {
        assertParseFailure(parser, "note nt/Corrected summary.", Messages.withFormat(
                "Missing STUDENT_INDEX and NOTE_NUMBER.",
                        EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_missingNoteNumber_throwsSpecificParseException() {
        assertParseFailure(parser, "note 1 nt/Corrected summary.", Messages.withFormat(
                "Missing NOTE_NUMBER. Enter the number shown beside the note in the profile.",
                EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_extraIndices_throwsOnlyOneOfEachWithFormat() {
        assertParseFailure(parser, "note 1 2 extra nt/text", Messages.withFormat(
                "edit note takes only one STUDENT_INDEX and one NOTE_NUMBER.",
                        EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_invalidIndices_throwsSpecificParseException() {
        String[] invalidStudentIndices = {"0", "-1", "x"};
        for (String invalidIndex : invalidStudentIndices) {
            assertParseFailure(parser, "note " + invalidIndex + " 1 nt/text",
                    "STUDENT_INDEX must be a positive whole number, e.g. 1.");
        }

        String[] invalidNoteNumbers = {"0", "-1", "x"};
        for (String invalidNumber : invalidNoteNumbers) {
            assertParseFailure(parser, "note 1 " + invalidNumber + " nt/text",
                    "NOTE_NUMBER must be a positive whole number, e.g. 1.");
        }
    }

    @Test
    public void parse_oversizedIndices_throwsTooLargeParseException() {
        assertParseFailure(parser, "note 2147483648 1 nt/text",
                "STUDENT_INDEX is too large. Enter a student index shown in the roster.");
        assertParseFailure(parser, "note 1 2147483648 nt/text",
                "NOTE_NUMBER is too large. Enter a note number shown in the profile.");
    }

    @Test
    public void parse_maximumIndices_areAccepted() {
        assertParseSuccess(parser, "note 2147483647 1 nt/text",
                new EditNoteCommand(Index.fromOneBased(Integer.MAX_VALUE), Index.fromOneBased(1), "text"));
        assertParseSuccess(parser, "note 1 2147483647 nt/text",
                new EditNoteCommand(Index.fromOneBased(1), Index.fromOneBased(Integer.MAX_VALUE), "text"));
    }

    @Test
    public void parse_invalidText_throwsSpecificRuleMessage() {
        int overLimit = SessionNote.MAX_TEXT_LENGTH + 1;

        assertParseFailure(parser, "note 1 1 nt/", ParserUtil.MESSAGE_EMPTY_NOTE);
        assertParseFailure(parser, "note 1 1 nt/" + "a".repeat(overLimit),
                String.format(ParserUtil.MESSAGE_NOTE_TOO_LONG, overLimit));
        assertParseFailure(parser, "note 1 1 nt/Column\tvalue", ParserUtil.MESSAGE_NOTE_NON_PRINTABLE);
        assertParseFailure(parser, "note 1 1 nt/   ", ParserUtil.MESSAGE_EMPTY_NOTE);
    }

    @Test
    public void parse_symbolsInText_preservesValidNoteText() {
        assertParseSuccess(parser, "note 1 1 nt/Review Chapter #2: John's progress.",
                new EditNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(1),
                        "Review Chapter #2: John's progress."));
    }

    @Test
    public void parse_duplicateNotePrefix_throwsParseException() {
        assertParseFailure(parser, "note 1 1 nt/First nt/Second",
                Messages.withFormat(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NOTE),
                        EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_notePrefixWithoutLeadingSpace_keptAsText() {
        assertParseSuccess(parser, "note 1 1 nt/Remember (nt/) notation",
                new EditNoteCommand(INDEX_FIRST_PERSON, INDEX_FIRST_PERSON, "Remember (nt/) notation"));
    }

    // Error precedence: target, then new note, then index count, then indices, then duplicate prefix.
    @Test
    public void parse_wrongTargetAndMissingNewNote_reportsMissingTargetFirst() {
        assertParseFailure(parser, "1 2", EditNoteCommandParser.MESSAGE_MISSING_NOTE_TARGET);
    }

    @Test
    public void parse_missingNewNoteAndIndices_reportsMissingNewNoteFirst() {
        assertParseFailure(parser, "note", EditNoteCommandParser.MESSAGE_MISSING_NEW_NOTE);
        assertParseFailure(parser, "note 1 2 3", EditNoteCommandParser.MESSAGE_MISSING_NEW_NOTE);
    }

    @Test
    public void parse_invalidIndexAndDuplicateNotePrefix_reportsInvalidIndexFirst() {
        assertParseFailure(parser, "note x 1 nt/first nt/second",
                String.format(ParserUtil.MESSAGE_INVALID_INDEX, ParserUtil.STUDENT_INDEX_LABEL));
    }

    // Boundary values of the preamble word count: 1 and 2 are too few, 3 is exact, 4 is too many.
    @Test
    public void parse_preambleWordCountAroundExactCount_acceptsOnlyExactCount() {
        assertParseFailure(parser, "note nt/x", EditNoteCommandParser.MESSAGE_MISSING_INDICES);
        assertParseFailure(parser, "note 1 nt/x", EditNoteCommandParser.MESSAGE_MISSING_NOTE_NUMBER);
        assertParseSuccess(parser, "note 1 2 nt/x",
                new EditNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2), "x"));
        assertParseFailure(parser, "note 1 2 3 nt/x", EditNoteCommandParser.MESSAGE_TOO_MANY_INDICES);
    }
}
