package seedu.address.logic;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_EMPTY_ROSTER =
            "There are no students in the roster yet. Add a student with add first.";
    public static final String MESSAGE_STUDENT_INDEX_OUT_OF_RANGE =
            "Student index [%1$d] is outside the roster. Enter a student index from 1 to %2$d.";
    public static final String MESSAGE_FORMAT = "Format: %1$s";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Returns {@code message} followed by a line showing the one-line {@code commandFormat}.
     * Structure errors use it because retyping the command in that format fixes them.
     */
    public static String withFormat(String message, String commandFormat) {
        return message + "\n" + String.format(MESSAGE_FORMAT, commandFormat);
    }

}
