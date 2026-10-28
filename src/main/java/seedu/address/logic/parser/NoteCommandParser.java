package seedu.address.logic.parser;

import static seedu.address.logic.Messages.withFormat;
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

    static final String MESSAGE_MISSING_NOTE = withFormat("Missing " + PREFIX_NOTE + "NOTE.",
            NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * Everything after the first {@code nt/} is the note, so other prefixes in the note, including a later
     * {@code nt/}, are kept as part of its text.
     *
     * @throws ParseException If the index or {@code nt/} is missing, the index is invalid, or the note text is
     *         invalid.
     */
    @Override
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenizeWithTrailingText(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        if (noteText.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_NOTE);
        }

        Index index = ParserUtil.parseOnlyStudentIndex(argMultimap.getPreamble(), NoteCommand.COMMAND_WORD,
                NoteCommand.COMMAND_FORMAT, NoteCommand.COMMAND_EXAMPLE);
        if (!SessionNote.isValidText(noteText.get())) {
            throw new ParseException(SessionNote.MESSAGE_CONSTRAINTS);
        }
        return new NoteCommand(index, noteText.get());
    }
}
