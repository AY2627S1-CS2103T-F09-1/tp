package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_EMPTY_COMMAND;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.Messages.withFormat;

import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /** Placeholder logged in place of a command word that does not name a known command */
    static final String UNKNOWN_COMMAND_WORD = "unknown";
    static final String MESSAGE_MISTYPED_NOTE_TARGET_FORMAT =
            "Unknown edit target: %s. Did you mean %s?";

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");
    /** Largest number of single-character edits from {@code note}, as in {@code nte}, still treated as a typo */
    private static final int MAX_NOTE_TARGET_TYPO_DISTANCE = 2;
    private static final Logger logger = LogsCenter.getLogger(AddressBookParser.class);

    private static final Set<String> COMMAND_WORDS = Set.of(AddCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD, NoteCommand.COMMAND_WORD, ViewCommand.COMMAND_WORD,
            ExitCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD);
    private static final String AVAILABLE_COMMANDS = COMMAND_WORDS.stream()
            .sorted()
            .collect(Collectors.joining(", "));

    /**
     * Returns the error shown when {@code commandWord} does not name a command, listing the available ones.
     */
    public static String getUnknownCommandMessage(String commandWord) {
        return String.format(MESSAGE_UNKNOWN_COMMAND, commandWord, AVAILABLE_COMMANDS);
    }

    /**
     * Returns the error shown when no command is entered, listing the available commands.
     */
    public static String getEmptyCommandMessage() {
        return String.format(MESSAGE_EMPTY_COMMAND, AVAILABLE_COMMANDS);
    }

    /**
     * Returns the command word of {@code userInput} if it names a known command, or {@code "unknown"}
     * otherwise. The result never contains the command's arguments, so it can be logged without exposing
     * student data.
     *
     * @param userInput Full user input string.
     * @return The known command word, or {@code "unknown"}.
     */
    public static String getLoggableCommandWord(String userInput) {
        Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return UNKNOWN_COMMAND_WORD;
        }

        String commandWord = matcher.group("commandWord");
        return COMMAND_WORDS.contains(commandWord) ? commandWord : UNKNOWN_COMMAND_WORD;
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(getEmptyCommandMessage());
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + getLoggableCommandWord(userInput));

        return switch (commandWord) {
            case AddCommand.COMMAND_WORD -> new AddCommandParser().parse(arguments);
            case DeleteCommand.COMMAND_WORD -> new DeleteCommandParser().parse(arguments);
            case EditCommand.COMMAND_WORD -> parseEditCommand(arguments);
            case NoteCommand.COMMAND_WORD -> new NoteCommandParser().parse(arguments);
            case ViewCommand.COMMAND_WORD -> new ViewCommandParser().parse(arguments);
            case ExitCommand.COMMAND_WORD -> new ExitCommand();
            case HelpCommand.COMMAND_WORD -> new HelpCommand();
            default -> {
                logger.finer("An unknown command word caused a ParseException.");
                throw new ParseException(getUnknownCommandMessage(commandWord));
            }
        };
    }

    private static Command parseEditCommand(String arguments) throws ParseException {
        String editTarget = arguments.stripLeading().split("\\s+", 2)[0];
        if (EditNoteCommand.NOTE_TARGET.equals(editTarget)) {
            return new EditNoteCommandParser().parse(arguments);
        }
        if (isMistypedNoteTarget(editTarget)) {
            String suggestion = EditNoteCommand.COMMAND_WORD + " " + EditNoteCommand.NOTE_TARGET;
            throw new ParseException(withFormat(
                    String.format(MESSAGE_MISTYPED_NOTE_TARGET_FORMAT, editTarget, suggestion),
                    EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE));
        }
        return new EditCommandParser().parse(arguments);
    }

    /**
     * Returns true if {@code editTarget} looks like a typo of {@code note} rather than a student index or a
     * parameter such as {@code n/NAME}.
     */
    private static boolean isMistypedNoteTarget(String editTarget) {
        boolean isParameter = editTarget.contains("/");
        return !editTarget.isEmpty() && !isParameter
                && getEditDistance(editTarget, EditNoteCommand.NOTE_TARGET) <= MAX_NOTE_TARGET_TYPO_DISTANCE;
    }

    /**
     * Returns the number of single-character insertions, deletions, or substitutions that turn {@code source}
     * into {@code target}.
     */
    private static int getEditDistance(String source, String target) {
        int[] previousRow = new int[target.length() + 1];
        for (int j = 0; j <= target.length(); j++) {
            previousRow[j] = j;
        }
        for (int i = 1; i <= source.length(); i++) {
            int[] currentRow = new int[target.length() + 1];
            currentRow[0] = i;
            for (int j = 1; j <= target.length(); j++) {
                int substitutionCost = source.charAt(i - 1) == target.charAt(j - 1) ? 0 : 1;
                currentRow[j] = Math.min(previousRow[j - 1] + substitutionCost,
                        Math.min(previousRow[j], currentRow[j - 1]) + 1);
            }
            previousRow = currentRow;
        }
        return previousRow[target.length()];
    }

}
