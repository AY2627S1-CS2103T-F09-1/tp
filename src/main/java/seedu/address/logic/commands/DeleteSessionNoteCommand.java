package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Requests deletion of one session note identified using its student and chronological note numbers.
 */
public class DeleteSessionNoteCommand extends Command {

    public static final String NOTE_TARGET = "note";
    public static final String COMMAND_FORMAT = DeleteCommand.COMMAND_WORD + " " + NOTE_TARGET
            + " STUDENT_INDEX NOTE_NUMBER [" + DeleteCommand.CONFIRMATION_FLAG + "]";
    public static final String COMMAND_EXAMPLE = DeleteCommand.COMMAND_WORD + " " + NOTE_TARGET + " 1 2";

    public static final String MESSAGE_CONFIRM_DELETE =
            "Are you sure you want to delete session note [%1$d] for %2$s?\n"
            + "If yes, type: " + DeleteCommand.COMMAND_WORD + " " + NOTE_TARGET + " %3$d %1$d "
            + DeleteCommand.CONFIRMATION_FLAG + "\n"
            + "To cancel, enter any other command.";
    public static final String MESSAGE_DELETE_SESSION_NOTE_SUCCESS =
            "Successfully deleted session note [%1$d] for %2$s.";
    public static final String MESSAGE_NO_PENDING_DELETION =
            "No matching session-note deletion is awaiting confirmation.\n"
            + "Enter delete note STUDENT_INDEX NOTE_NUMBER without /confirm first.";
    public static final String MESSAGE_CONFIRMATION_MISMATCH =
            "This confirmation does not match the pending session-note deletion.\n"
            + "No data was deleted. Enter the delete command again to start over.";
    public static final String MESSAGE_NO_SESSION_NOTES = "%1$s has no session notes to delete.";
    public static final String MESSAGE_INVALID_NOTE_NUMBER =
            "Session note [%1$d] does not exist for %2$s. Enter a note number from 1 to %3$d.";

    private final Index studentIndex;
    private final Index noteNumber;
    private final boolean isConfirmed;

    /**
     * Creates an unconfirmed request to delete {@code noteNumber} from the student at {@code studentIndex}.
     */
    public DeleteSessionNoteCommand(Index studentIndex, Index noteNumber) {
        this(studentIndex, noteNumber, false);
    }

    /**
     * Creates a request to delete {@code noteNumber} from the student at {@code studentIndex}.
     */
    public DeleteSessionNoteCommand(Index studentIndex, Index noteNumber, boolean isConfirmed) {
        this.studentIndex = requireNonNull(studentIndex);
        this.noteNumber = requireNonNull(noteNumber);
        this.isConfirmed = isConfirmed;
    }

    /**
     * Returns the one-based roster index of the student whose note is targeted.
     */
    public Index getStudentIndex() {
        return studentIndex;
    }

    /**
     * Returns the chronological note number targeted by this request.
     */
    public Index getNoteNumber() {
        return noteNumber;
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

        StudentRosterEntry student = getRosterEntry(model, studentIndex);
        SessionHistory history = model.getSessionHistory(student.getStudentId());
        if (history.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_SESSION_NOTES, student.getName()));
        }
        if (noteNumber.getOneBased() > history.size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_NOTE_NUMBER,
                    noteNumber.getOneBased(), student.getName(), history.size()));
        }

        return new CommandResult(String.format(MESSAGE_CONFIRM_DELETE,
                noteNumber.getOneBased(), student.getName(), studentIndex.getOneBased()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof DeleteSessionNoteCommand otherCommand
                && studentIndex.equals(otherCommand.studentIndex)
                && noteNumber.equals(otherCommand.noteNumber)
                && isConfirmed == otherCommand.isConfirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentIndex", studentIndex)
                .add("noteNumber", noteNumber)
                .add("isConfirmed", isConfirmed)
                .toString();
    }
}
