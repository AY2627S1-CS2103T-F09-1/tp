package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.session.SessionNote;

/**
 * Parses input arguments and creates a new NoteCommand object.
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    static final String MESSAGE_INVALID_INDEX =
            "INDEX must be a positive integer.\n" + NoteCommand.MESSAGE_USAGE;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE);
    private static final String WHITESPACE_PATTERN = "\\s+";

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * Only {@code nt/} is recognized, so other prefixes in the note are kept as part of its text.
     *
     * @throws ParseException If the user input does not conform to the expected format, or the note text is
     *         invalid.
     */
    @Override
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        if (noteText.isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        Index index = parseIndex(argMultimap.getPreamble());
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE);
        if (!SessionNote.isValidText(noteText.get())) {
            throw new ParseException(SessionNote.MESSAGE_CONSTRAINTS);
        }
        return new NoteCommand(index, noteText.get());
    }

    /**
     * Returns the index given in {@code preamble}.
     *
     * @throws ParseException If the preamble is empty or has several words, or if its single word is not a
     *         positive integer.
     */
    private Index parseIndex(String preamble) throws ParseException {
        boolean isSingleWord = !preamble.isEmpty() && preamble.split(WHITESPACE_PATTERN).length == 1;
        if (!isSingleWord) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        try {
            return ParserUtil.parseIndex(preamble);
        } catch (ParseException exception) {
            throw new ParseException(MESSAGE_INVALID_INDEX, exception);
        }
    }
}
