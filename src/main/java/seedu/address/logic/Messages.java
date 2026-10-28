package seedu.address.logic;

import java.util.List;
import java.util.stream.Stream;

import seedu.address.logic.parser.Prefix;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command: %1$s. Available commands: %2$s.";
    public static final String MESSAGE_EMPTY_COMMAND = "Enter a command. Available commands: %1$s.";
    public static final String MESSAGE_EMPTY_ROSTER =
            "There are no students in the roster yet. Add a student with add first.";
    public static final String MESSAGE_STUDENT_INDEX_OUT_OF_RANGE =
            "Student index [%1$d] is outside the roster. Enter a student index from 1 to %2$d.";
    public static final String MESSAGE_FORMAT = "Format: %1$s";
    public static final String MESSAGE_EXAMPLE = "Example: %1$s";
    public static final String MESSAGE_DUPLICATE_FIELD = "%1$s must appear at most once.";
    public static final String MESSAGE_DUPLICATE_FIELDS = "%1$s and %2$s must each appear at most once.";

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        List<String> duplicateFields = Stream.of(duplicatePrefixes).map(Prefix::toString).distinct().toList();
        if (duplicateFields.size() == 1) {
            return String.format(MESSAGE_DUPLICATE_FIELD, duplicateFields.get(0));
        }

        int lastPosition = duplicateFields.size() - 1;
        String leadingFields = String.join(", ", duplicateFields.subList(0, lastPosition));
        return String.format(MESSAGE_DUPLICATE_FIELDS, leadingFields, duplicateFields.get(lastPosition));
    }

    /**
     * Returns {@code message} followed by one line showing {@code commandFormat} and another showing
     * {@code commandExample}.
     * Structure errors use it because retyping the command in that format fixes them.
     */
    public static String withFormat(String message, String commandFormat, String commandExample) {
        return message + "\n" + String.format(MESSAGE_FORMAT, commandFormat)
                + "\n" + String.format(MESSAGE_EXAMPLE, commandExample);
    }

}
