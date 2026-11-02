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

    static final String MESSAGE_MISSING_NOTE = withNoteFormat("Missing " + PREFIX_NOTE + "NOTE.");
    static final String MESSAGE_MISSING_SPACE_BEFORE_NOTE_PREFIX = withNoteFormat(
            "Put a space before " + PREFIX_NOTE + ", after the student INDEX.");

    private static final String WHITESPACE_PATTERN = "\\s+";
    private static final String WHOLE_NUMBER_PATTERN = "\\d+";
    /** Matches an index with the note prefix typed straight after it, as in {@code 1nt/hello}. */
    private static final Pattern NOTE_PREFIX_GLUED_TO_INDEX =
            Pattern.compile("^\\s*\\d+" + Pattern.quote(PREFIX_NOTE.getPrefix()));

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * Only {@code nt/} is recognized, so other prefixes in the note are kept as part of its text.
     * An {@code nt/} typed in another case, such as {@code NT/}, is reported as such rather than as missing or as
     * stray text before a later {@code nt/}.
     *
     * @throws ParseException If the index or {@code nt/} is missing or repeated, the index is invalid, or the
     *         note text is invalid.
     */
    @Override
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        if (noteText.isEmpty()) {
            ParserUtil.verifyNoWrongCaseNotePrefix(args, NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
            throw new ParseException(NOTE_PREFIX_GLUED_TO_INDEX.matcher(args).find()
                    ? MESSAGE_MISSING_SPACE_BEFORE_NOTE_PREFIX
                    : MESSAGE_MISSING_NOTE);
        }

        ParserUtil.verifyNoWrongCaseNotePrefix(argMultimap.getPreamble(), NoteCommand.COMMAND_FORMAT,
                NoteCommand.COMMAND_EXAMPLE);
        verifyNoStrayText(argMultimap.getPreamble());
        Index index = ParserUtil.parseOnlyStudentIndex(argMultimap.getPreamble(), NoteCommand.COMMAND_WORD,
                NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE);
        } catch (ParseException exception) {
            throw new ParseException(withNoteFormat(exception.getMessage()), exception);
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
                throw new ParseException(withNoteFormat(
                        String.format(AddCommandParser.MESSAGE_UNEXPECTED_PREAMBLE, strayText)));
            }
        }
    }

    /**
     * Returns {@code message} followed by the note command's format and example.
     */
    private static String withNoteFormat(String message) {
        return withFormat(message, NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
    }
}
