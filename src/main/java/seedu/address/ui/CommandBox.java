package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int MAX_HISTORY_SIZE = 100;
    private static final int PAGE_UP_COUNT = -1;
    private static final int PAGE_DOWN_COUNT = 1;
    /** A Windows line break (CRLF), or one control character such as a tab, line feed or carriage return */
    private static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile("\r\n|\\p{Cntrl}");
    private static final String CONTROL_CHARACTER_REPLACEMENT = " ";

    private final Logger logger = LogsCenter.getLogger(getClass());
    private final CommandExecutor commandExecutor;
    private final PageScroller pageScroller;
    private final List<String> commandHistory = new ArrayList<>();

    private int historyIndex;
    private String draftText = "";

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        this(commandExecutor, pageCount -> { });
    }

    /**
     * Creates a {@code CommandBox} that sends PageUp and PageDown presses to {@code pageScroller}.
     */
    public CommandBox(CommandExecutor commandExecutor, PageScroller pageScroller) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.pageScroller = requireNonNull(pageScroller);
        // TextField silently deletes tabs and line breaks, joining the words around them, so turn them into
        // spaces before it sees them.
        commandTextField.setTextFormatter(new TextFormatter<String>(this::replaceControlCharacters));
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryNavigation);
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handlePageScroll);
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        if (!commandText.isBlank()) {
            addToHistory(commandText);
        }
        resetHistoryNavigation();

        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Returns {@code text} with each control character, such as a tab, line feed or carriage return, replaced by
     * one space. A Windows line break (CRLF) counts as one line break, so it also becomes one space.
     */
    static String replaceControlCharactersWithSpaces(String text) {
        requireNonNull(text);
        String result = CONTROL_CHARACTER_PATTERN.matcher(text).replaceAll(CONTROL_CHARACTER_REPLACEMENT);
        assert !CONTROL_CHARACTER_PATTERN.matcher(result).find() : "Control character left in command text";
        return result;
    }

    private TextFormatter.Change replaceControlCharacters(TextFormatter.Change change) {
        String insertedText = change.getText();
        String replacedText = replaceControlCharactersWithSpaces(insertedText);
        if (replacedText.equals(insertedText)) {
            return change;
        }

        logger.fine("Replaced control characters in command box input with spaces");
        // A CRLF shrinks to one space, and Change#setText does not move the caret or anchor to match.
        int insertedEnd = change.getRangeStart() + insertedText.length();
        int lengthChange = replacedText.length() - insertedText.length();
        int anchor = adjustForReplacement(change.getAnchor(), insertedEnd, lengthChange);
        int caretPosition = adjustForReplacement(change.getCaretPosition(), insertedEnd, lengthChange);
        change.setText(replacedText);
        change.selectRange(anchor, caretPosition);
        return change;
    }

    /**
     * Returns where {@code position} lies after the inserted text ending at {@code insertedEnd} changes length by
     * {@code lengthChange}. A position inside the inserted text is kept within the shorter text.
     */
    private static int adjustForReplacement(int position, int insertedEnd, int lengthChange) {
        if (position >= insertedEnd) {
            return position + lengthChange;
        }
        return Math.min(position, insertedEnd + lengthChange);
    }

    private void handleHistoryNavigation(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            showPreviousCommand();
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            showNextCommand();
            event.consume();
        }
    }

    private void handlePageScroll(KeyEvent event) {
        if (event.getCode() == KeyCode.PAGE_UP) {
            pageScroller.scrollPages(PAGE_UP_COUNT);
            event.consume();
        } else if (event.getCode() == KeyCode.PAGE_DOWN) {
            pageScroller.scrollPages(PAGE_DOWN_COUNT);
            event.consume();
        }
    }

    private void addToHistory(String commandText) {
        if (commandHistory.size() == MAX_HISTORY_SIZE) {
            commandHistory.remove(0);
        }
        commandHistory.add(commandText);
    }

    private void resetHistoryNavigation() {
        historyIndex = commandHistory.size();
        draftText = "";
    }

    private void showPreviousCommand() {
        if (historyIndex == 0) {
            return;
        }
        if (historyIndex == commandHistory.size()) {
            draftText = commandTextField.getText();
        }

        historyIndex--;
        setCommandText(commandHistory.get(historyIndex));
    }

    private void showNextCommand() {
        if (historyIndex == commandHistory.size()) {
            return;
        }

        historyIndex++;
        String commandText = historyIndex == commandHistory.size()
                ? draftText
                : commandHistory.get(historyIndex);
        setCommandText(commandText);
    }

    private void setCommandText(String commandText) {
        commandTextField.setText(commandText);
        commandTextField.positionCaret(commandText.length());
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Represents a view that can scroll by whole pages.
     */
    @FunctionalInterface
    public interface PageScroller {
        /**
         * Scrolls by {@code pageCount} pages; a negative count scrolls upward.
         */
        void scrollPages(int pageCount);
    }

}
