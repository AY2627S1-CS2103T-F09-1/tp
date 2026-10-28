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
}
