package seedu.address.logic.parser;

import static seedu.address.logic.Messages.withFormat;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new NoteCommand object.
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    static final String MESSAGE_MISSING_NOTE = withFormat("Missing " + PREFIX_NOTE + "NOTE.",
            NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_MISSING_SPACE_BEFORE_NOTE_PREFIX = withFormat(
            "Put a space before " + PREFIX_NOTE + ", after the student INDEX.",
            NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);

    private static final String WHITESPACE_PATTERN = "\\s+";
    private static final String WHOLE_NUMBER_PATTERN = "\\d+";
    /** Matches an index with the note prefix typed straight after it, as in {@code 1nt/hello}. */
    private static final Pattern NOTE_PREFIX_GLUED_TO_INDEX =
            Pattern.compile("^\\s*\\d+" + Pattern.quote(PREFIX_NOTE.getPrefix()));

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * Only {@code nt/} is recognized, so other prefixes in the note are kept as part of its text.
     *
     * @throws ParseException If the index or {@code nt/} is missing or repeated, the index is invalid, or the
     *         note text is invalid.
     */
    @Override
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        if (noteText.isEmpty()) {
            throw new ParseException(NOTE_PREFIX_GLUED_TO_INDEX.matcher(args).find()
                    ? MESSAGE_MISSING_SPACE_BEFORE_NOTE_PREFIX
                    : MESSAGE_MISSING_NOTE);
        }

        verifyNoStrayText(argMultimap.getPreamble());
        Index index = ParserUtil.parseOnlyStudentIndex(argMultimap.getPreamble(), NoteCommand.COMMAND_WORD,
                NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE);
        } catch (ParseException exception) {
            throw new ParseException(withFormat(exception.getMessage(),
                    NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE), exception);
        }
        return new NoteCommand(index, ParserUtil.parseNoteText(noteText.get()));
    }

    /**
     * Verifies that the first word of {@code preamble} is a valid index and that every word after it is a
     * number, so that stray text is reported as such instead of as a second index.
     *
     * @throws ParseException If the first word is not a valid index when more words follow it, or a later word
     *         is not a number.
     */
    private static void verifyNoStrayText(String preamble) throws ParseException {
        String[] words = preamble.trim().split(WHITESPACE_PATTERN);
        if (words.length > 1) {
            ParserUtil.parseStudentIndex(words[0], ParserUtil.INDEX_LABEL);
        }
        for (int i = 1; i < words.length; i++) {
            if (!words[i].matches(WHOLE_NUMBER_PATTERN)) {
                String strayText = String.join(" ", Arrays.copyOfRange(words, i, words.length));
                throw new ParseException(withFormat(
                        String.format(AddCommandParser.MESSAGE_UNEXPECTED_PREAMBLE, strayText),
                        NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE));
            }
        }
    }
}
