package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
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

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditNoteCommand.MESSAGE_USAGE);
    private static final String MESSAGE_INVALID_STUDENT_INDEX =
            "STUDENT_INDEX must be a positive integer.\n" + EditNoteCommand.MESSAGE_USAGE;
    private static final String MESSAGE_INVALID_NOTE_NUMBER =
            "NOTE_NUMBER must be a positive integer.\n" + EditNoteCommand.MESSAGE_USAGE;
    private static final String MESSAGE_MISSING_NOTE_NUMBER =
            "Missing NOTE_NUMBER. Enter the number shown beside the note in the profile, "
                    + "e.g. edit note 1 2 nt/...\n" + EditNoteCommand.MESSAGE_USAGE;

    /**
     * Parses the given {@code String} of arguments in the context of the edit note command.
     *
     * @throws ParseException If the arguments do not conform to the expected format or contain invalid note text.
     */
    @Override
    public EditNoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NOTE);
        Optional<String> noteText = argMultimap.getValue(PREFIX_NOTE);
        String[] preamble = argMultimap.getPreamble().split("\\s+");

        if (noteText.isPresent() && preamble.length == 2
                && EditNoteCommand.NOTE_TARGET.equals(preamble[0])) {
            throw new ParseException(MESSAGE_MISSING_NOTE_NUMBER);
        }
        if (noteText.isEmpty() || preamble.length != 3
                || !EditNoteCommand.NOTE_TARGET.equals(preamble[0])) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        Index studentIndex = parseIndex(preamble[1], MESSAGE_INVALID_STUDENT_INDEX);
        Index noteNumber = parseIndex(preamble[2], MESSAGE_INVALID_NOTE_NUMBER);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NOTE);
        if (!SessionNote.isValidText(noteText.get())) {
            throw new ParseException(SessionNote.MESSAGE_CONSTRAINTS);
        }
        return new EditNoteCommand(studentIndex, noteNumber, noteText.get());
    }

    private Index parseIndex(String argument, String errorMessage) throws ParseException {
        try {
            return ParserUtil.parseIndex(argument);
        } catch (ParseException exception) {
            throw new ParseException(errorMessage, exception);
        }
    }
}
