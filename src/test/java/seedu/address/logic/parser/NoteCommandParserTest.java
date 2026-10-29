package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.model.session.SessionNote;

public class NoteCommandParserTest {

    private final NoteCommandParser parser = new NoteCommandParser();

    @Test
    public void parse_validArgs_returnsNoteCommand() {
        assertParseSuccess(parser, " 1 nt/Reviewed factorisation.",
                new NoteCommand(INDEX_FIRST_PERSON, "Reviewed factorisation."));
    }

    @Test
    public void parse_surroundingAndInternalSpaces_trimsSurroundingSpacesOnly() {
        assertParseSuccess(parser, "  1   nt/  Paper 2:  Q3 & Q5  ",
                new NoteCommand(INDEX_FIRST_PERSON, "Paper 2:  Q3 & Q5"));
    }

    @Test
    public void parse_otherPrefixesInNote_keptAsText() {
        assertParseSuccess(parser, " 2 nt/Next: 3 sub/topics, n/a for l/2",
                new NoteCommand(Index.fromOneBased(2), "Next: 3 sub/topics, n/a for l/2"));
    }

    @Test
    public void parse_maximumLengthNote_returnsNoteCommand() {
        String longNote = "a".repeat(SessionNote.MAX_TEXT_LENGTH);

        assertParseSuccess(parser, " 1 nt/" + longNote, new NoteCommand(INDEX_FIRST_PERSON, longNote));
    }

    @Test
    public void parse_missingParts_throwsParseException() {
        String missingNoteMessage = Messages.withFormat("Missing nt/NOTE.",
                NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);

        assertParseFailure(parser, "", missingNoteMessage);
        assertParseFailure(parser, " 1", missingNoteMessage);
        assertParseFailure(parser, " 1 Reviewed factorisation.", missingNoteMessage);
        assertParseFailure(parser, " nt/Reviewed factorisation.",
                Messages.withFormat("Missing INDEX.", NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_invalidIndexValue_throwsInvalidIndexMessage() {
        String[] invalidIndices = {"abc", "0", "-1"};

        for (String invalidIndex : invalidIndices) {
            assertParseFailure(parser, " " + invalidIndex + " nt/Reviewed factorisation.",
                    "INDEX must be a positive whole number, e.g. 1.");
        }
    }

    @Test
    public void parse_aboveIntegerRange_throwsTooLarge() {
        String[] oversizedIndices = {"2147483648", "99999999999"};

        for (String oversizedIndex : oversizedIndices) {
            assertParseFailure(parser, " " + oversizedIndex + " nt/Reviewed factorisation.",
                    "INDEX is too large. Enter a student index shown in the roster.");
        }
    }

    @Test
    public void parse_severalPreambleWords_throwsOnlyOneIndexWithFormat() {
        String expectedMessage = Messages.withFormat("note takes only one INDEX.",
                NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);

        assertParseFailure(parser, " 1 2 nt/Reviewed factorisation.", expectedMessage);
        assertParseFailure(parser, " 1 2 3 nt/Reviewed factorisation.", expectedMessage);
    }

    @Test
    public void parse_strayTextAfterIndex_reportsUnexpectedTextNotSecondIndex() {
        assertParseFailure(parser, " 1 hello nt/x",
                withNoteFormat("Unexpected text before the first parameter: hello."));
        assertParseFailure(parser, " 1 2 hello nt/x",
                withNoteFormat("Unexpected text before the first parameter: hello."));
        assertParseFailure(parser, " 1 hello world nt/x",
                withNoteFormat("Unexpected text before the first parameter: hello world."));
    }

    @Test
    public void parse_invalidIndexBeforeMoreWords_reportsInvalidIndex() {
        String invalidIndexMessage = "INDEX must be a positive whole number, e.g. 1.";

        assertParseFailure(parser, " abc 1 nt/Reviewed factorisation.", invalidIndexMessage);
        assertParseFailure(parser, " abc hello nt/x", invalidIndexMessage);
    }

    @Test
    public void parse_notePrefixGluedToIndex_reportsMissingSpace() {
        String expectedMessage = withNoteFormat("Put a space before nt/, after the student INDEX.");

        assertParseFailure(parser, " 1nt/hello", expectedMessage);
        assertParseFailure(parser, " 12nt/hello", expectedMessage);
    }

    @Test
    public void parse_noNotePrefixOrNotePrefixNotAfterIndex_stillReportsMissingNote() {
        assertParseFailure(parser, " 1 hello", NoteCommandParser.MESSAGE_MISSING_NOTE);
        assertParseFailure(parser, " 1 Remember (nt/)", NoteCommandParser.MESSAGE_MISSING_NOTE);
        assertParseFailure(parser, " 1 hellont/x", NoteCommandParser.MESSAGE_MISSING_NOTE);
    }

    private static String withNoteFormat(String message) {
        return Messages.withFormat(message, NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
    }

    @Test
    public void parse_invalidNoteText_throwsSpecificRuleMessage() {
        int overLimit = SessionNote.MAX_TEXT_LENGTH + 1;

        assertParseFailure(parser, " 1 nt/", ParserUtil.MESSAGE_EMPTY_NOTE);
        assertParseFailure(parser, " 1 nt/    ", ParserUtil.MESSAGE_EMPTY_NOTE);
        assertParseFailure(parser, " 1 nt/" + "a".repeat(overLimit),
                String.format(ParserUtil.MESSAGE_NOTE_TOO_LONG, overLimit));
        assertParseFailure(parser, " 1 nt/Column\tvalue", ParserUtil.MESSAGE_NOTE_NON_PRINTABLE);
    }

    @Test
    public void parse_duplicateNotePrefix_throwsParseException() {
        assertParseFailure(parser, " 1 nt/Reviewed indices. nt/Reviewed factorisation.",
                Messages.withFormat(Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NOTE),
                        NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_notePrefixWithoutLeadingSpace_keptAsText() {
        assertParseSuccess(parser, " 1 nt/Remember (nt/) and \"nt/\" notation",
                new NoteCommand(INDEX_FIRST_PERSON, "Remember (nt/) and \"nt/\" notation"));
    }
}
