package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Replaces the text of one session note while preserving its recorded date and time.
 */
public class EditNoteCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String NOTE_TARGET = "note";
    public static final String MESSAGE_USAGE = COMMAND_WORD + " " + NOTE_TARGET
            + ": Edits one session note without changing when it was recorded.\n"
            + "Parameters: STUDENT_INDEX NOTE_NUMBER nt/NEW_NOTE\n"
            + "Example: " + COMMAND_WORD + " " + NOTE_TARGET + " 1 2 nt/Corrected lesson summary.";
    public static final String MESSAGE_SUCCESS = "Successfully edited session note [%1$d] for %2$s.";
    public static final String MESSAGE_NO_SESSION_NOTES = "%1$s has no session notes to edit.";
    public static final String MESSAGE_NO_STUDENTS =
            "There are no students in the roster. Add a student before editing a note.";
    public static final String MESSAGE_INVALID_STUDENT_INDEX =
            "Student index [%1$d] is outside the roster. Enter a student index from 1 to %2$d.";
    public static final String MESSAGE_INVALID_NOTE_NUMBER =
            "Session note [%1$d] does not exist for %2$s. Enter a note number from 1 to %3$d.";

    private final Index studentIndex;
    private final Index noteNumber;
    private final String text;

    /**
     * Creates a request to replace the text of {@code noteNumber} for the student at {@code studentIndex}.
     */
    public EditNoteCommand(Index studentIndex, Index noteNumber, String text) {
        this.studentIndex = requireNonNull(studentIndex);
        this.noteNumber = requireNonNull(noteNumber);
        this.text = requireNonNull(text);
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
     * Returns the replacement note text.
     */
    public String getText() {
        return text;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<StudentRosterEntry> rosterEntries = model.getStudentRoster().getEntries();
        if (rosterEntries.isEmpty()) {
            throw new CommandException(MESSAGE_NO_STUDENTS);
        }
        if (studentIndex.getZeroBased() >= rosterEntries.size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_STUDENT_INDEX,
                    studentIndex.getOneBased(), rosterEntries.size()));
        }

        StudentRosterEntry student = rosterEntries.get(studentIndex.getZeroBased());
        SessionHistory history = model.getSessionHistory(student.getStudentId());
        if (history.isEmpty()) {
            throw new CommandException(String.format(MESSAGE_NO_SESSION_NOTES, student.getName()));
        }
        if (noteNumber.getOneBased() > history.size()) {
            throw new CommandException(String.format(MESSAGE_INVALID_NOTE_NUMBER,
                    noteNumber.getOneBased(), student.getName(), history.size()));
        }

        model.editSessionNote(student.getStudentId(), noteNumber, text);
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                noteNumber.getOneBased(), student.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof EditNoteCommand otherCommand
                && studentIndex.equals(otherCommand.studentIndex)
                && noteNumber.equals(otherCommand.noteNumber)
                && text.equals(otherCommand.text);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentIndex", studentIndex)
                .add("noteNumber", noteNumber)
                .add("text", "[REDACTED]")
                .toString();
    }
}
