package seedu.address.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.HomeFolder;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    /** Sentence ending every save error, because a command whose data cannot be saved is rolled back */
    public static final String MESSAGE_COMMAND_CANCELLED = " The command was cancelled and no data was changed.";
    public static final String FILE_OPS_ERROR_FORMAT =
            "Could not save data to file %s (%s)." + MESSAGE_COMMAND_CANCELLED;

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s because TutorTrack cannot write to the file or its folder."
            + MESSAGE_COMMAND_CANCELLED;

    /** Format of the log line that marks each command, given the command word only */
    static final String USER_COMMAND_LOG_FORMAT = "----------------[USER COMMAND][%s]";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;
    private PendingDeletion pendingDeletion;

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
        logger.info(String.format(USER_COMMAND_LOG_FORMAT, commandWord));

        Command command = addressBookParser.parseCommand(commandText);
        PendingDeletion deletionAwaitingConfirmation = pendingDeletion;
        pendingDeletion = null;

        if (command instanceof DeleteCommand deleteCommand) {
            return executeDeleteStudentCommand(deleteCommand, deletionAwaitingConfirmation);
        }
        if (command instanceof DeleteSessionNoteCommand deleteSessionNoteCommand) {
            return executeDeleteSessionNoteCommand(deleteSessionNoteCommand, deletionAwaitingConfirmation);
        }

        return executeAndSave(command);
    }

    /**
     * Executes a non-delete command and persists its model changes.
     * A command that leaves the data unchanged is not saved, so it still succeeds when saving would fail.
     */
    private CommandResult executeAndSave(Command command) throws CommandException {
        AddressBook dataBeforeCommand = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute(model);

        boolean hasDataChanged = !dataBeforeCommand.equals(model.getAddressBook());
        if (!hasDataChanged) {
            logger.fine("Command left the data unchanged; skipping save.");
            return commandResult;
        }

        saveOrRestore(dataBeforeCommand);
        return commandResult;
    }

    /**
     * Returns a preview for an unconfirmed deletion, or completes a matching confirmed deletion.
     */
    private CommandResult executeDeleteStudentCommand(DeleteCommand command,
            PendingDeletion deletionAwaitingConfirmation) throws CommandException {
        if (command.isConfirmed()) {
            return executeConfirmedStudentDelete(command, deletionAwaitingConfirmation);
        }

        CommandResult commandResult = command.execute(model);
        StudentRosterEntry targetStudent = getRosterEntry(command.getTargetIndex()).orElseThrow();
        pendingDeletion = new PendingStudentDeletion(command.getTargetIndex(), targetStudent.getStudentId());
        return commandResult;
    }

    /**
     * Completes a confirmed deletion when its index and stable student ID match the pending request.
     */
    private CommandResult executeConfirmedStudentDelete(DeleteCommand command,
            PendingDeletion deletionAwaitingConfirmation) throws CommandException {
        if (deletionAwaitingConfirmation == null) {
            throw new CommandException(DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        }

        Optional<StudentRosterEntry> targetStudent = getRosterEntry(command.getTargetIndex());
        if (!(deletionAwaitingConfirmation instanceof PendingStudentDeletion pendingStudentDeletion)
                || !matchesStudentDeletion(pendingStudentDeletion, command, targetStudent)) {
            throw new CommandException(DeleteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        }

        AddressBook dataBeforeCommand = new AddressBook(model.getAddressBook());
        model.deleteStudent(pendingStudentDeletion.studentId());
        CommandResult commandResult = new CommandResult(String.format(DeleteCommand.MESSAGE_DELETE_STUDENT_SUCCESS,
                targetStudent.get().getName()));
        saveOrRestore(dataBeforeCommand);
        return commandResult;
    }

    /**
     * Returns whether the pending student deletion still identifies the requested roster entry.
     */
    private static boolean matchesStudentDeletion(PendingStudentDeletion pending, DeleteCommand command,
            Optional<StudentRosterEntry> targetStudent) {
        return pending.targetIndex().equals(command.getTargetIndex())
                && targetStudent.isPresent()
                && pending.studentId().equals(targetStudent.get().getStudentId());
    }

    /**
     * Returns a preview for an unconfirmed session-note deletion, or completes a matching confirmed deletion.
     */
    private CommandResult executeDeleteSessionNoteCommand(DeleteSessionNoteCommand command,
            PendingDeletion deletionAwaitingConfirmation) throws CommandException {
        if (command.isConfirmed()) {
            return executeConfirmedSessionNoteDelete(command, deletionAwaitingConfirmation);
        }

        CommandResult commandResult = command.execute(model);
        StudentRosterEntry targetStudent = getRosterEntry(command.getStudentIndex()).orElseThrow();
        SessionHistory targetHistory = model.getSessionHistory(targetStudent.getStudentId());
        pendingDeletion = new PendingSessionNoteDeletion(command.getStudentIndex(), targetStudent.getStudentId(),
                command.getNoteNumber(), targetHistory);
        return commandResult;
    }

    /**
     * Completes a session-note deletion when its indices, stable student ID, and history match the pending request.
     */
    private CommandResult executeConfirmedSessionNoteDelete(DeleteSessionNoteCommand command,
            PendingDeletion deletionAwaitingConfirmation) throws CommandException {
        if (deletionAwaitingConfirmation == null) {
            throw new CommandException(DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION);
        }

        Optional<StudentRosterEntry> targetStudent = getRosterEntry(command.getStudentIndex());
        if (!(deletionAwaitingConfirmation instanceof PendingSessionNoteDeletion pendingSessionNoteDeletion)
                || !matchesSessionNoteDeletion(pendingSessionNoteDeletion, command, targetStudent)) {
            throw new CommandException(DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        }

        AddressBook dataBeforeCommand = new AddressBook(model.getAddressBook());
        model.deleteSessionNote(pendingSessionNoteDeletion.studentId(), pendingSessionNoteDeletion.noteNumber());
        CommandResult commandResult = new CommandResult(String.format(
                DeleteSessionNoteCommand.MESSAGE_DELETE_SESSION_NOTE_SUCCESS,
                command.getNoteNumber().getOneBased(), targetStudent.get().getName()));
        saveOrRestore(dataBeforeCommand);
        return commandResult;
    }

    /**
     * Returns whether the requested student and note still match the pending history snapshot.
     */
    private boolean matchesSessionNoteDeletion(PendingSessionNoteDeletion pending, DeleteSessionNoteCommand command,
            Optional<StudentRosterEntry> targetStudent) {
        return pending.studentIndex().equals(command.getStudentIndex())
                && pending.noteNumber().equals(command.getNoteNumber())
                && targetStudent.isPresent()
                && pending.studentId().equals(targetStudent.get().getStudentId())
                && pending.history() == model.getSessionHistory(pending.studentId());
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
        // The exception may name a temporary file, so error messages name the data file instead, relative to
        // the TutorTrack folder so that the user's home folder is not shown.
        Path dataFile = HomeFolder.toDisplayPath(storage.getAddressBookFilePath());
        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            restore(dataBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, dataFile), e);
        } catch (IOException ioe) {
            restore(dataBeforeCommand);
            throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, dataFile, ioe.getMessage()), ioe);
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

    private interface PendingDeletion {
    }

    private record PendingStudentDeletion(Index targetIndex, StudentId studentId) implements PendingDeletion {
    }

    private record PendingSessionNoteDeletion(Index studentIndex, StudentId studentId, Index noteNumber,
            SessionHistory history) implements PendingDeletion {
    }
}
