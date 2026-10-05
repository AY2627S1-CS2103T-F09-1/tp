package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.model.session.SessionNote;

public class NoteCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE);

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
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " nt/Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, " abc nt/Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 0 nt/Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " -1 nt/Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 2 nt/Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 99999999999 nt/Reviewed factorisation.", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidNoteText_throwsParseException() {
        assertParseFailure(parser, " 1 nt/", SessionNote.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 nt/    ", SessionNote.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 nt/" + "a".repeat(SessionNote.MAX_TEXT_LENGTH + 1),
                SessionNote.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateNotePrefix_throwsParseException() {
        assertParseFailure(parser, " 1 nt/Reviewed indices. nt/Reviewed factorisation.",
                Messages.getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_NOTE));
    }
}
