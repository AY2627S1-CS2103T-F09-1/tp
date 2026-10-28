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
            "Missing STUDENT_INDEX and NOTE_NUMBER.",
                    DeleteSessionNoteCommand.COMMAND_FORMAT, DeleteSessionNoteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_MISSING_NOTE_NUMBER = withFormat(
            "Missing NOTE_NUMBER. Enter the number shown beside the note in the profile.",
            DeleteSessionNoteCommand.COMMAND_FORMAT, DeleteSessionNoteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS = withFormat(
            "Unexpected extra arguments after the session-note deletion request.",
            DeleteSessionNoteCommand.COMMAND_FORMAT, DeleteSessionNoteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG = withFormat(
            DeleteCommand.CONFIRMATION_FLAG + " must appear at most once and at the end of the command.",
            DeleteSessionNoteCommand.COMMAND_FORMAT, DeleteSessionNoteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_INVALID_STUDENT_CONFIRMATION_FLAG = withFormat(
            DeleteCommand.CONFIRMATION_FLAG + " must appear at most once and at the end of the command.",
            DeleteCommand.COMMAND_FORMAT, DeleteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_MISSING_STUDENT_INDEX = withFormat(
            String.format(MESSAGE_MISSING_INDEX, INDEX_LABEL),
                    DeleteCommand.COMMAND_FORMAT, DeleteCommand.COMMAND_EXAMPLE);
    static final String MESSAGE_TOO_MANY_STUDENT_INDICES = withFormat(
            String.format(MESSAGE_TOO_MANY_INDICES, DeleteCommand.COMMAND_WORD, INDEX_LABEL),
            DeleteCommand.COMMAND_FORMAT, DeleteCommand.COMMAND_EXAMPLE);

    // Positions and sizes of "note STUDENT_INDEX NOTE_NUMBER [/confirm]" once split on whitespace.
    private static final int NOTE_TARGET_POSITION = 0;
    private static final int STUDENT_INDEX_POSITION = 1;
    private static final int NOTE_NUMBER_POSITION = 2;
    private static final int CONFIRMATION_FLAG_POSITION = 3;
    private static final int ARGUMENT_COUNT_TARGET_ONLY = 1;
    private static final int ARGUMENT_COUNT_WITHOUT_NOTE_NUMBER = 2;
    private static final int ARGUMENT_COUNT_CONFIRMED = 4;

    private static final String WHITESPACE_PATTERN = "\\s+";

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
        if (isSessionNoteDeletion(arguments)) {
            return parseSessionNoteDeletion(arguments);
        }
        return parseStudentDeletion(arguments);
    }

    private boolean isSessionNoteDeletion(List<String> arguments) {
        return !arguments.isEmpty()
                && DeleteSessionNoteCommand.NOTE_TARGET.equals(arguments.get(NOTE_TARGET_POSITION));
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
        verifyArgumentCount(arguments);
        verifyConfirmationFlagPlacement(arguments);

        Index studentIndex = ParserUtil.parseStudentIndex(
                arguments.get(STUDENT_INDEX_POSITION), STUDENT_INDEX_LABEL);
        Index noteNumber = ParserUtil.parseNoteNumber(arguments.get(NOTE_NUMBER_POSITION));
        boolean isConfirmed = arguments.size() == ARGUMENT_COUNT_CONFIRMED;
        return new DeleteSessionNoteCommand(studentIndex, noteNumber, isConfirmed);
    }

    /**
     * Checks that {@code arguments} hold the {@code note} target, both indices, and at most one flag.
     *
     * @throws ParseException If an index is missing or extra arguments are present.
     */
    private static void verifyArgumentCount(List<String> arguments) throws ParseException {
        if (arguments.size() == ARGUMENT_COUNT_TARGET_ONLY) {
            throw new ParseException(MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS);
        }
        if (arguments.size() == ARGUMENT_COUNT_WITHOUT_NOTE_NUMBER) {
            boolean isFlagInStudentIndexPosition = isConfirmationFlag(arguments.get(STUDENT_INDEX_POSITION));
            throw new ParseException(isFlagInStudentIndexPosition
                    ? MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG
                    : MESSAGE_MISSING_NOTE_NUMBER);
        }
        if (arguments.size() > ARGUMENT_COUNT_CONFIRMED) {
            throw new ParseException(MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS);
        }
    }

    /**
     * Checks that the confirmation flag, if any, is the last argument and not in place of an index.
     *
     * @throws ParseException If the flag is misplaced or the last of four arguments is not the flag.
     */
    private static void verifyConfirmationFlagPlacement(List<String> arguments) throws ParseException {
        boolean isFlagInIndexPosition = isConfirmationFlag(arguments.get(STUDENT_INDEX_POSITION))
                || isConfirmationFlag(arguments.get(NOTE_NUMBER_POSITION));
        boolean isLastArgumentNotFlag = arguments.size() == ARGUMENT_COUNT_CONFIRMED
                && !isConfirmationFlag(arguments.get(CONFIRMATION_FLAG_POSITION));
        if (isFlagInIndexPosition || isLastArgumentNotFlag) {
            throw new ParseException(MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG);
        }
    }

    private static boolean isConfirmationFlag(String argument) {
        return DeleteCommand.CONFIRMATION_FLAG.equals(argument);
    }
}
