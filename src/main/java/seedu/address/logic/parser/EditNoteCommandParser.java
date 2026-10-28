package seedu.address.logic.parser;

import static seedu.address.logic.Messages.withFormat;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.session.SessionNote;

/**
 * Parses input arguments and creates an {@link EditNoteCommand} object.
 */
public class EditNoteCommandParser implements Parser<EditNoteCommand> {

    static final String MESSAGE_MISSING_NOTE_TARGET = withEditNoteFormat(
            "Start the command with " + EditNoteCommand.COMMAND_WORD + " " + EditNoteCommand.NOTE_TARGET + ".");
    static final String MESSAGE_MISSING_NEW_NOTE = withEditNoteFormat("Missing " + PREFIX_NOTE + "NEW_NOTE.");
    static final String MESSAGE_MISSING_INDICES = withEditNoteFormat("Missing STUDENT_INDEX and NOTE_NUMBER.");
    static final String MESSAGE_MISSING_NOTE_NUMBER = withEditNoteFormat(
            "Missing NOTE_NUMBER. Enter the number shown beside the note in the profile.");
    static final String MESSAGE_TOO_MANY_INDICES = withEditNoteFormat(
            EditNoteCommand.COMMAND_WORD + " " + EditNoteCommand.NOTE_TARGET
                    + " takes only one STUDENT_INDEX and one NOTE_NUMBER.");

    private static final String WHITESPACE_PATTERN = "\\s+";
    private static final int TARGET_AND_INDEX_COUNT = 3;

    /**
     * Parses the given {@code String} of arguments in the context of the edit note command.
     * Everything after the first {@code nt/} is the new note, so a later {@code nt/} is kept as part of its text.
     *
     * @throws ParseException If a part of the command is missing, an index is invalid, or the note text is
     *         invalid.
     */
    @Override
    public EditNoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenizeWithTrailingText(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        String[] preamble = argMultimap.getPreamble().split(WHITESPACE_PATTERN);

        if (!EditNoteCommand.NOTE_TARGET.equals(preamble[0])) {
            throw new ParseException(MESSAGE_MISSING_NOTE_TARGET);
        }
        if (noteText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_NEW_NOTE);
        }
        verifyIndexCount(preamble.length);

        Index studentIndex = ParserUtil.parseStudentIndex(preamble[1], ParserUtil.STUDENT_INDEX_LABEL);
        Index noteNumber = ParserUtil.parseNoteNumber(preamble[2]);
        if (!SessionNote.isValidText(noteText.get())) {
            throw new ParseException(SessionNote.MESSAGE_CONSTRAINTS);
        }
        return new EditNoteCommand(studentIndex, noteNumber, noteText.get());
    }

    /**
     * Checks that the preamble holds the {@code note} target followed by exactly two indices.
     *
     * @throws ParseException If an index is missing or extra values are present.
     */
    private static void verifyIndexCount(int preambleWordCount) throws ParseException {
        if (preambleWordCount == 1) {
            throw new ParseException(MESSAGE_MISSING_INDICES);
        }
        if (preambleWordCount == 2) {
            throw new ParseException(MESSAGE_MISSING_NOTE_NUMBER);
        }
        if (preambleWordCount > TARGET_AND_INDEX_COUNT) {
            throw new ParseException(MESSAGE_TOO_MANY_INDICES);
        }
    }

    private static String withEditNoteFormat(String message) {
        return withFormat(message, EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE);
    }
}
