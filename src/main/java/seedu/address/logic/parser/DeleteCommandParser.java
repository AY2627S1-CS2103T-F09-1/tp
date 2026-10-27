package seedu.address.logic.parser;

import static seedu.address.logic.Messages.withFormat;
import static seedu.address.logic.parser.ParserUtil.INDEX_LABEL;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_TOO_MANY_INDICES;
import static seedu.address.logic.parser.ParserUtil.STUDENT_INDEX_LABEL;

import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a student or session-note deletion command.
 */
public class DeleteCommandParser implements Parser<Command> {

    static final String MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS = withFormat(
            "Missing STUDENT_INDEX and NOTE_NUMBER.", DeleteSessionNoteCommand.COMMAND_FORMAT);
    static final String MESSAGE_MISSING_NOTE_NUMBER = withFormat(
            "Missing NOTE_NUMBER. Enter the number shown beside the note in the profile.",
            DeleteSessionNoteCommand.COMMAND_FORMAT);
    static final String MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS = withFormat(
            "Unexpected extra arguments after the session-note deletion request.",
            DeleteSessionNoteCommand.COMMAND_FORMAT);
    static final String MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG = withFormat(
            DeleteCommand.CONFIRMATION_FLAG + " must appear at most once and at the end of the command.",
            DeleteSessionNoteCommand.COMMAND_FORMAT);
    static final String MESSAGE_INVALID_STUDENT_CONFIRMATION_FLAG = withFormat(
            DeleteCommand.CONFIRMATION_FLAG + " must appear at most once and at the end of the command.",
            DeleteCommand.COMMAND_FORMAT);
    static final String MESSAGE_MISSING_STUDENT_INDEX = withFormat(
            String.format(MESSAGE_MISSING_INDEX, INDEX_LABEL), DeleteCommand.COMMAND_FORMAT);
    static final String MESSAGE_TOO_MANY_STUDENT_INDICES = withFormat(
            String.format(MESSAGE_TOO_MANY_INDICES, DeleteCommand.COMMAND_WORD, INDEX_LABEL),
            DeleteCommand.COMMAND_FORMAT);

    private static final String WHITESPACE_PATTERN = "\s+";

    /**
     * Parses the given {@code String} of arguments in the context of the delete command.
     *
     * @throws ParseException If an index is missing, repeated, or invalid, or if {@code /confirm} is misplaced.
     */
    public Command parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        List<String> arguments = trimmedArgs.isEmpty()
                ? List.of()
                : Arrays.asList(trimmedArgs.split(WHITESPACE_PATTERN));
        if (!arguments.isEmpty() && DeleteSessionNoteCommand.NOTE_TARGET.equals(arguments.get(0))) {
            return parseSessionNoteDeletion(arguments);
        }
        return parseStudentDeletion(arguments);
    }

    private DeleteCommand parseStudentDeletion(List<String> arguments) throws ParseException {
        boolean isConfirmed = !arguments.isEmpty()
                && DeleteCommand.CONFIRMATION_FLAG.equals(arguments.get(arguments.size() - 1));
        List<String> indices = isConfirmed ? arguments.subList(0, arguments.size() - 1) : arguments;

        if (indices.contains(DeleteCommand.CONFIRMATION_FLAG)) {
            throw new ParseException(MESSAGE_INVALID_STUDENT_CONFIRMATION_FLAG);
        }
        if (indices.isEmpty()) {
            throw new ParseException(MESSAGE_MISSING_STUDENT_INDEX);
        }
        if (indices.size() > 1) {
            throw new ParseException(MESSAGE_TOO_MANY_STUDENT_INDICES);
        }

        Index index = ParserUtil.parseStudentIndex(indices.get(0), INDEX_LABEL);
        return new DeleteCommand(index, isConfirmed);
    }

    /**
     * Parses {@code arguments}, which start with the {@code note} target, as a session-note deletion.
     */
    private DeleteSessionNoteCommand parseSessionNoteDeletion(List<String> arguments) throws ParseException {
        if (arguments.size() == 1) {
            throw new ParseException(MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS);
        }
        if (arguments.size() == 2) {
            throw new ParseException(DeleteCommand.CONFIRMATION_FLAG.equals(arguments.get(1))
                    ? MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG
                    : MESSAGE_MISSING_NOTE_NUMBER);
        }
        if (arguments.size() > 4) {
            throw new ParseException(MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS);
        }
        boolean isFlagMisplaced = DeleteCommand.CONFIRMATION_FLAG.equals(arguments.get(1))
                || DeleteCommand.CONFIRMATION_FLAG.equals(arguments.get(2))
                || arguments.size() == 4 && !DeleteCommand.CONFIRMATION_FLAG.equals(arguments.get(3));
        if (isFlagMisplaced) {
            throw new ParseException(MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG);
        }

        Index studentIndex = ParserUtil.parseStudentIndex(arguments.get(1), STUDENT_INDEX_LABEL);
        Index noteNumber = ParserUtil.parseNoteNumber(arguments.get(2));
        return new DeleteSessionNoteCommand(studentIndex, noteNumber, arguments.size() == 4);
    }
}
