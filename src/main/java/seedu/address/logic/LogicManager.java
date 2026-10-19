package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private PendingStudentDeletion pendingStudentDeletion;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        String commandWord = AddressBookParser.getLoggableCommandWord(commandText);
        logger.info("----------------[USER COMMAND][" + commandWord + "]");

        PendingStudentDeletion deletionAwaitingConfirmation = pendingStudentDeletion;
        pendingStudentDeletion = null;
        Command command = addressBookParser.parseCommand(commandText);

        if (command instanceof DeleteCommand deleteCommand) {
            return executeDeleteCommand(deleteCommand, deletionAwaitingConfirmation);
        }

        return executeAndSave(command);
    }

    /**
     * Executes a non-delete command and persists its model changes.
     */
    private CommandResult executeAndSave(Command command) throws CommandException {
        AddressBook dataBeforeCommand = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute(model);
        saveOrRestore(dataBeforeCommand);
        return commandResult;
    }

    /**
     * Returns a preview for an unconfirmed deletion, or completes a matching confirmed deletion.
     */
    private CommandResult executeDeleteCommand(DeleteCommand command,
            PendingStudentDeletion deletionAwaitingConfirmation) throws CommandException {
        if (command.isConfirmed()) {
            return executeConfirmedDelete(command, deletionAwaitingConfirmation);
        }

        CommandResult commandResult = command.execute(model);
        StudentRosterEntry targetStudent = getRosterEntry(command.getTargetIndex()).orElseThrow();
        pendingStudentDeletion = new PendingStudentDeletion(command.getTargetIndex(), targetStudent.getStudentId());
        return commandResult;
    }

    /**
     * Completes a confirmed deletion when its index and stable student ID match the pending request.
     */
    private CommandResult executeConfirmedDelete(DeleteCommand command,
            PendingStudentDeletion deletionAwaitingConfirmation) throws CommandException {
        if (deletionAwaitingConfirmation == null) {
            throw new CommandException(DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        }

        Optional<StudentRosterEntry> targetStudent = getRosterEntry(command.getTargetIndex());
        if (!deletionAwaitingConfirmation.targetIndex().equals(command.getTargetIndex())
                || targetStudent.isEmpty()
                || !deletionAwaitingConfirmation.studentId().equals(targetStudent.get().getStudentId())) {
            throw new CommandException(DeleteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        }

        AddressBook dataBeforeCommand = new AddressBook(model.getAddressBook());
        model.deleteStudent(deletionAwaitingConfirmation.studentId());
        CommandResult commandResult = new CommandResult(String.format(DeleteCommand.MESSAGE_DELETE_STUDENT_SUCCESS,
                targetStudent.get().getName()));
        saveOrRestore(dataBeforeCommand);
        return commandResult;
    }

    /**
     * Returns the roster entry at {@code targetIndex}, if one exists.
     */
    private Optional<StudentRosterEntry> getRosterEntry(Index targetIndex) {
        if (targetIndex.getZeroBased() >= model.getStudentRoster().size()) {
            return Optional.empty();
        }
        return Optional.of(model.getStudentRoster().getEntries().get(targetIndex.getZeroBased()));
    }

    /**
     * Saves the model's address book, restoring {@code dataBeforeCommand} into the model if saving fails,
     * so that a command whose changes cannot be saved leaves the model unchanged.
     *
     * @throws CommandException If the address book cannot be saved.
     */
    private void saveOrRestore(AddressBook dataBeforeCommand) throws CommandException {
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            restore(dataBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            restore(dataBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }
    }

    /**
     * Restores {@code dataBeforeCommand} into the model after a failed save, and logs the rollback.
     */
    private void restore(AddressBook dataBeforeCommand) {
        logger.warning("Could not save data; restoring the data from before the command.");
        model.setAddressBook(dataBeforeCommand);
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public StudentRoster getStudentRoster() {
        return model.getStudentRoster();
    }

    @Override
    public Optional<StudentProfile> getStudentProfile(StudentId studentId) {
        return model.getStudentProfile(studentId);
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }

    private record PendingStudentDeletion(Index targetIndex, StudentId studentId) {
    }
}
