package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a student or session-note deletion command.
 */
public class DeleteCommandParser implements Parser<Command> {

    static final String MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS =
            "Both STUDENT_INDEX and NOTE_NUMBER are required.\n" + DeleteSessionNoteCommand.MESSAGE_USAGE;
    static final String MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS =
            "Unexpected extra arguments after the session-note deletion request.\n"
            + DeleteSessionNoteCommand.MESSAGE_USAGE;
    static final String MESSAGE_INVALID_CONFIRMATION_FLAG =
            DeleteCommand.CONFIRMATION_FLAG + " must appear at most once and at the end of the command.\n"
            + DeleteSessionNoteCommand.MESSAGE_USAGE;
    static final String MESSAGE_INVALID_STUDENT_INDEX =
            "STUDENT_INDEX must be a positive integer.\n" + DeleteSessionNoteCommand.MESSAGE_USAGE;
    static final String MESSAGE_INVALID_NOTE_NUMBER =
            "NOTE_NUMBER must be a positive integer.\n" + DeleteSessionNoteCommand.MESSAGE_USAGE;

    /**
     * Parses the given {@code String} of arguments in the context of the delete command.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parse(String args) throws ParseException {
        String[] arguments = args.trim().split("\\s+");
        if (arguments.length > 0 && DeleteSessionNoteCommand.NOTE_TARGET.equals(arguments[0])) {
            return parseSessionNoteDeletion(arguments);
        }
        return parseStudentDeletion(arguments);
    }

    private DeleteCommand parseStudentDeletion(String[] arguments) throws ParseException {
        try {
            boolean isConfirmed = arguments.length == 2
                    && DeleteCommand.CONFIRMATION_FLAG.equals(arguments[1]);

            if (arguments.length > 2 || arguments.length == 2 && !isConfirmed) {
                throw new ParseException("Unexpected delete command argument.");
            }

            Index index = ParserUtil.parseIndex(arguments[0]);
            return new DeleteCommand(index, isConfirmed);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }

    private DeleteSessionNoteCommand parseSessionNoteDeletion(String[] arguments) throws ParseException {
        if (arguments.length < 3) {
            throw new ParseException(MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS);
        }
        if (arguments.length > 4) {
            throw new ParseException(MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS);
        }
        if (DeleteCommand.CONFIRMATION_FLAG.equals(arguments[1])
                || DeleteCommand.CONFIRMATION_FLAG.equals(arguments[2])
                || arguments.length == 4 && !DeleteCommand.CONFIRMATION_FLAG.equals(arguments[3])) {
            throw new ParseException(MESSAGE_INVALID_CONFIRMATION_FLAG);
        }

        Index studentIndex = parseSessionNoteIndex(arguments[1], MESSAGE_INVALID_STUDENT_INDEX);
        Index noteNumber = parseSessionNoteIndex(arguments[2], MESSAGE_INVALID_NOTE_NUMBER);
        return new DeleteSessionNoteCommand(studentIndex, noteNumber, arguments.length == 4);
    }

    private Index parseSessionNoteIndex(String argument, String errorMessage) throws ParseException {
        try {
            return ParserUtil.parseIndex(argument);
        } catch (ParseException pe) {
            throw new ParseException(errorMessage, pe);
        }
    }
}
