package seedu.address.logic.parser;

import static seedu.address.logic.Messages.withFormat;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new NoteCommand object.
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    static final String MESSAGE_MISSING_NOTE = withFormat("Missing " + PREFIX_NOTE + "NOTE.",
            NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_WRONG_CASE_NOTE_PREFIX =
            "Parameter prefixes must be typed in lowercase: %1$s should be " + PREFIX_NOTE + ".";

    private static final String WHITESPACE_PATTERN = "\\s+";

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * Only {@code nt/} is recognized, so other prefixes in the note are kept as part of its text.
     * An {@code nt/} typed in another case, such as {@code NT/}, is reported as such rather than as missing.
     *
     * @throws ParseException If the index or {@code nt/} is missing or repeated, the index is invalid, or the
     *         note text is invalid.
     */
    @Override
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        if (noteText.isEmpty()) {
            verifyNoWrongCaseNotePrefix(args);
            throw new ParseException(MESSAGE_MISSING_NOTE);
        }

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
     * Verifies that no word of {@code args} starts with {@code nt/} typed in another case, such as {@code NT/},
     * so that a note whose prefix is in the wrong case is not reported as missing.
     *
     * @throws ParseException If a word starts with {@code nt/} in another case, naming the prefix as typed.
     */
    private static void verifyNoWrongCaseNotePrefix(String args) throws ParseException {
        String notePrefix = PREFIX_NOTE.getPrefix();
        for (String word : args.trim().split(WHITESPACE_PATTERN)) {
            boolean startsWithNotePrefixIgnoringCase = word.regionMatches(true, 0, notePrefix, 0,
                    notePrefix.length());
            if (startsWithNotePrefixIgnoringCase && !word.startsWith(notePrefix)) {
                String typedPrefix = word.substring(0, notePrefix.length());
                throw new ParseException(withFormat(String.format(MESSAGE_WRONG_CASE_NOTE_PREFIX, typedPrefix),
                        NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE));
            }
        }
    }
}
