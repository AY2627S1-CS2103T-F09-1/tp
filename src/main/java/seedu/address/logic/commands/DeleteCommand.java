package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Requests deletion of a student identified using the displayed roster index.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String CONFIRMATION_FLAG = "/confirm";
    public static final String COMMAND_FORMAT = COMMAND_WORD + " INDEX [" + CONFIRMATION_FLAG + "]";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Previews or confirms deletion of the student identified by the index number used in the student "
            + "roster.\n"
            + "Parameters: INDEX (must be a positive integer) [" + CONFIRMATION_FLAG + "]\n"
            + "Preview: " + COMMAND_WORD + " 1\n"
            + "Confirm: " + COMMAND_WORD + " 1 " + CONFIRMATION_FLAG;

    public static final String MESSAGE_CONFIRM_DELETE =
            "Are you sure you want to delete %1$s and %2$d %3$s?\n"
            + "If yes, type: " + COMMAND_WORD + " %4$d " + CONFIRMATION_FLAG + "\n"
            + "To cancel, enter any other command.";
    public static final String MESSAGE_DELETE_STUDENT_SUCCESS =
            "Successfully deleted %1$s.";
    public static final String MESSAGE_NO_PENDING_DELETION =
            "No matching deletion is awaiting confirmation.\n"
            + "Enter delete INDEX without /confirm first.";
    public static final String MESSAGE_CONFIRMATION_MISMATCH =
            "This confirmation does not match the pending deletion.\n"
            + "No data was deleted. Enter the delete command again to start over.";

    private final Index targetIndex;
    private final boolean isConfirmed;

    /**
     * Creates an unconfirmed deletion request for the student at {@code targetIndex}.
     *
     * @param targetIndex The one-based roster index of the student.
     */
    public DeleteCommand(Index targetIndex) {
        this(targetIndex, false);
    }

    /**
     * Creates a deletion request for the student at {@code targetIndex}.
     *
     * @param targetIndex The one-based roster index of the student.
     * @param isConfirmed Whether the user supplied the confirmation flag.
     */
    public DeleteCommand(Index targetIndex, boolean isConfirmed) {
        this.targetIndex = requireNonNull(targetIndex);
        this.isConfirmed = isConfirmed;
    }

    /**
     * Returns the one-based roster index targeted by this request.
     */
    public Index getTargetIndex() {
        return targetIndex;
    }

    /**
     * Returns whether the request includes the confirmation flag.
     */
    public boolean isConfirmed() {
        return isConfirmed;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (isConfirmed) {
            throw new CommandException(MESSAGE_NO_PENDING_DELETION);
        }

        StudentRosterEntry studentToDelete = getRosterEntry(model, targetIndex);
        int noteCount = studentToDelete.getNoteCount();
        String noteLabel = noteCount == 1 ? "session note" : "session notes";
        return new CommandResult(String.format(MESSAGE_CONFIRM_DELETE,
                studentToDelete.getName(), noteCount, noteLabel, targetIndex.getOneBased()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex)
                && isConfirmed == otherDeleteCommand.isConfirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("isConfirmed", isConfirmed)
                .toString();
    }
}
