package seedu.address.ui;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.StudentId;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final double SCREEN_SIZE_TOLERANCE = 1;

    private final Logger logger = LogsCenter.getLogger(getClass());
    private final ListChangeListener<Screen> screenConfigurationListener = change -> updateWindowMaximumSize();

    private Stage primaryStage;
    private Logic logic;
    private Path dataFilePath;
    private Optional<StudentId> displayedProfileStudentId = Optional.empty();

    // Independent Ui parts residing in this Ui container
    private StudentRosterPanel studentRosterPanel;
    private StudentProfilePanel studentProfilePanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private StackPane studentRosterPanelPlaceholder;

    @FXML
    private StackPane studentProfilePanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage}, {@code Logic},
     * and the data file path to show in the status bar.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());
        configureWindowMaximumSizeUpdates();

        setAccelerators();

        helpWindow = new HelpWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in a later version of the SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField and
         * ResultDisplay contains a TextArea, some accelerators (e.g., F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add the following event filter to capture such key events and open
         * the help window purposely so as to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        studentRosterPanel = new StudentRosterPanel(logic.getStudentRoster());
        studentRosterPanelPlaceholder.getChildren().add(studentRosterPanel.getRoot());

        studentProfilePanel = new StudentProfilePanel();
        studentProfilePanelPlaceholder.getChildren().add(studentProfilePanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        Rectangle2D visualBounds = getTargetScreenVisualBounds(guiSettings);
        setWindowMaximumSize(visualBounds);
        primaryStage.setHeight(Math.min(guiSettings.getWindowHeight(), visualBounds.getHeight()));
        primaryStage.setWidth(Math.min(guiSettings.getWindowWidth(), visualBounds.getWidth()));
        Point windowCoordinates = guiSettings.getWindowCoordinates();
        if (windowCoordinates != null) {
            double maximumX = visualBounds.getMaxX() - primaryStage.getWidth();
            double maximumY = visualBounds.getMaxY() - primaryStage.getHeight();
            primaryStage.setX(clampCoordinate(windowCoordinates.getX(), visualBounds.getMinX(), maximumX));
            primaryStage.setY(clampCoordinate(windowCoordinates.getY(), visualBounds.getMinY(), maximumY));
        }
    }

    private void configureWindowMaximumSizeUpdates() {
        primaryStage.xProperty().addListener((observable, oldX, newX) -> updateWindowMaximumSize());
        primaryStage.yProperty().addListener((observable, oldY, newY) -> updateWindowMaximumSize());
        primaryStage.showingProperty().addListener((observable, wasShowing, isShowing) -> {
            if (isShowing) {
                Screen.getScreens().addListener(screenConfigurationListener);
                updateWindowMaximumSize();
            } else {
                Screen.getScreens().removeListener(screenConfigurationListener);
            }
        });
    }

    private void updateWindowMaximumSize() {
        setWindowMaximumSize(getScaleAdjustedVisualBounds(getCurrentScreen()));
    }

    private void setWindowMaximumSize(Rectangle2D visualBounds) {
        primaryStage.setMaxWidth(visualBounds.getWidth());
        primaryStage.setMaxHeight(visualBounds.getHeight());
    }

    private Screen getCurrentScreen() {
        Rectangle2D windowBounds = new Rectangle2D(primaryStage.getX(), primaryStage.getY(),
                primaryStage.getWidth(), primaryStage.getHeight());
        List<Screen> screens = Screen.getScreensForRectangle(windowBounds);
        if (screens.isEmpty()) {
            return Screen.getPrimary();
        }
        List<Rectangle2D> screenBounds = screens.stream().map(Screen::getBounds).toList();
        return screens.get(findLargestOverlapIndex(windowBounds, screenBounds));
    }

    /**
     * Returns the index of the screen bounds that overlap {@code windowBounds} the most.
     * Ties keep the earliest screen.
     */
    static int findLargestOverlapIndex(Rectangle2D windowBounds, List<Rectangle2D> screenBounds) {
        assert !screenBounds.isEmpty() : "At least one screen must be available";
        int largestOverlapIndex = 0;
        double largestOverlapArea = calculateIntersectionArea(windowBounds, screenBounds.get(0));
        for (int i = 1; i < screenBounds.size(); i++) {
            double overlapArea = calculateIntersectionArea(windowBounds, screenBounds.get(i));
            if (overlapArea > largestOverlapArea) {
                largestOverlapIndex = i;
                largestOverlapArea = overlapArea;
            }
        }
        return largestOverlapIndex;
    }

    private static double calculateIntersectionArea(Rectangle2D firstBounds, Rectangle2D secondBounds) {
        double intersectionWidth = Math.max(0,
                Math.min(firstBounds.getMaxX(), secondBounds.getMaxX())
                        - Math.max(firstBounds.getMinX(), secondBounds.getMinX()));
        double intersectionHeight = Math.max(0,
                Math.min(firstBounds.getMaxY(), secondBounds.getMaxY())
                        - Math.max(firstBounds.getMinY(), secondBounds.getMinY()));
        return intersectionWidth * intersectionHeight;
    }

    private double clampCoordinate(double savedCoordinate, double minimumCoordinate, double maximumCoordinate) {
        double usableMaximum = Math.max(minimumCoordinate, maximumCoordinate);
        return Math.max(minimumCoordinate, Math.min(savedCoordinate, usableMaximum));
    }

    /**
     * Returns the usable bounds of the screen containing the saved window origin.
     * Falls back to the primary screen when no saved origin is available there.
     */
    private Rectangle2D getTargetScreenVisualBounds(GuiSettings guiSettings) {
        Point windowCoordinates = guiSettings.getWindowCoordinates();
        Screen targetScreen;
        if (windowCoordinates == null) {
            targetScreen = Screen.getPrimary();
        } else {
            targetScreen = Screen.getScreensForRectangle(windowCoordinates.getX(), windowCoordinates.getY(), 1, 1)
                    .stream()
                    .findFirst()
                    .orElse(Screen.getPrimary());
        }
        return getScaleAdjustedVisualBounds(targetScreen);
    }

    private Rectangle2D getScaleAdjustedVisualBounds(Screen targetScreen) {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (Screen.getScreens().size() != 1 || graphicsDevices.length != 1) {
            return targetScreen.getVisualBounds();
        }
        DisplayMode displayMode = graphicsDevices[0].getDisplayMode();
        return adjustVisualBoundsForOutputScale(targetScreen.getBounds(), targetScreen.getVisualBounds(),
                targetScreen.getOutputScaleX(), targetScreen.getOutputScaleY(),
                displayMode.getWidth(), displayMode.getHeight());
    }

    static Rectangle2D adjustVisualBoundsForOutputScale(Rectangle2D screenBounds, Rectangle2D visualBounds,
            double outputScaleX, double outputScaleY, int physicalWidth, int physicalHeight) {
        assert outputScaleX > 0 : "Output scale must be positive";
        assert outputScaleY > 0 : "Output scale must be positive";
        assert physicalWidth > 0 : "Physical display width must be positive";
        assert physicalHeight > 0 : "Physical display height must be positive";

        boolean widthExceedsPhysicalDisplay = screenBounds.getWidth() * outputScaleX
                > physicalWidth + SCREEN_SIZE_TOLERANCE;
        boolean heightExceedsPhysicalDisplay = screenBounds.getHeight() * outputScaleY
                > physicalHeight + SCREEN_SIZE_TOLERANCE;
        if (!widthExceedsPhysicalDisplay && !heightExceedsPhysicalDisplay) {
            return visualBounds;
        }
        return new Rectangle2D(visualBounds.getMinX() / outputScaleX, visualBounds.getMinY() / outputScaleY,
                visualBounds.getWidth() / outputScaleX, visualBounds.getHeight() / outputScaleY);
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        primaryStage.hide();
    }

    public StudentRosterPanel getStudentRosterPanel() {
        return studentRosterPanel;
    }

    StudentProfilePanel getStudentProfilePanel() {
        return studentProfilePanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            studentRosterPanel.setRoster(logic.getStudentRoster());
            commandResult.getProfileStudentId()
                    .ifPresent(studentId -> displayedProfileStudentId = Optional.of(studentId));
            refreshDisplayedProfile();
            logger.info("Command executed successfully: " + AddressBookParser.getLoggableCommandWord(commandText));
            resultDisplay.setFeedbackToUser(commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: "
                    + AddressBookParser.getLoggableCommandWord(commandText));
            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    private void refreshDisplayedProfile() {
        displayedProfileStudentId.ifPresent(studentId -> logic.getStudentProfile(studentId)
                .ifPresentOrElse(studentProfile -> {
                    studentProfilePanel.setProfile(studentProfile);
                    studentRosterPanel.selectStudent(studentId);
                }, () -> {
                    studentProfilePanel.clearProfile();
                    studentRosterPanel.clearSelection();
                    displayedProfileStudentId = Optional.empty();
                }));
    }
}
