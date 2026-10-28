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
            DeleteCommand.COMMAND_WORD + " " + DeleteSessionNoteCommand.NOTE_TARGET
                    + " takes only one STUDENT_INDEX and one NOTE_NUMBER.",
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
    private static final int ARGUMENT_COUNT_TARGET_ONLY = 1;
    private static final int ARGUMENT_COUNT_WITHOUT_NOTE_NUMBER = 2;

    private static final String WHITESPACE_PATTERN = "\\s+";
    /** Number of words before an optional {@code /confirm}: the {@code note} target and two indices */
    private static final int SESSION_NOTE_VALUE_COUNT = 3;

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
        verifyIndicesPresent(arguments);
        verifyConfirmationFlagPlacement(arguments);
        verifyNoExtraValues(arguments);

        Index studentIndex = ParserUtil.parseStudentIndex(
                arguments.get(STUDENT_INDEX_POSITION), STUDENT_INDEX_LABEL);
        Index noteNumber = ParserUtil.parseNoteNumber(arguments.get(NOTE_NUMBER_POSITION));
        return new DeleteSessionNoteCommand(studentIndex, noteNumber, isConfirmed(arguments));
    }

    /**
     * Checks that {@code arguments} hold the {@code note} target and both indices.
     *
     * @throws ParseException If an index is missing.
     */
    private static void verifyIndicesPresent(List<String> arguments) throws ParseException {
        if (arguments.size() == ARGUMENT_COUNT_TARGET_ONLY) {
            throw new ParseException(MESSAGE_MISSING_SESSION_NOTE_ARGUMENTS);
        }
        if (arguments.size() == ARGUMENT_COUNT_WITHOUT_NOTE_NUMBER) {
            boolean isFlagInStudentIndexPosition = isConfirmationFlag(arguments.get(STUDENT_INDEX_POSITION));
            throw new ParseException(isFlagInStudentIndexPosition
                    ? MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG
                    : MESSAGE_MISSING_NOTE_NUMBER);
        }
    }

    /**
     * Checks that a misplaced or mistyped confirmation flag is not present.
     *
     * @throws ParseException If the flag is not the last word, takes the place of an index, or is a mistyped
     *         flag such as {@code /Confirm} after both indices.
     */
    private static void verifyConfirmationFlagPlacement(List<String> arguments) throws ParseException {
        int lastPosition = arguments.size() - 1;
        boolean isFlagBeforeLast = arguments.subList(0, lastPosition).contains(DeleteCommand.CONFIRMATION_FLAG);
        boolean isFlagInNoteNumberPosition = isConfirmationFlag(arguments.get(NOTE_NUMBER_POSITION));
        String lastArgument = arguments.get(lastPosition);
        boolean isLastArgumentMistypedFlag = arguments.size() == SESSION_NOTE_VALUE_COUNT + 1
                && !isConfirmationFlag(lastArgument)
                && DeleteCommand.CONFIRMATION_FLAG.equalsIgnoreCase(lastArgument);
        if (isFlagBeforeLast || isFlagInNoteNumberPosition || isLastArgumentMistypedFlag) {
            throw new ParseException(MESSAGE_INVALID_SESSION_NOTE_CONFIRMATION_FLAG);
        }
    }

    /**
     * Checks that nothing follows the note number apart from an optional confirmation flag.
     *
     * @throws ParseException If more than the target and two indices are present.
     */
    private static void verifyNoExtraValues(List<String> arguments) throws ParseException {
        int valueCount = isConfirmed(arguments) ? arguments.size() - 1 : arguments.size();
        if (valueCount > SESSION_NOTE_VALUE_COUNT) {
            throw new ParseException(MESSAGE_EXTRA_SESSION_NOTE_ARGUMENTS);
        }
    }

    private static boolean isConfirmed(List<String> arguments) {
        return isConfirmationFlag(arguments.get(arguments.size() - 1));
    }

    private static boolean isConfirmationFlag(String argument) {
        return DeleteCommand.CONFIRMATION_FLAG.equals(argument);
    }
}
