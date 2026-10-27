package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Adds a timestamped session note to the student at a given roster index.
 */
public class NoteCommand extends Command {

    public static final String COMMAND_WORD = "note";
    public static final String COMMAND_FORMAT = COMMAND_WORD + " INDEX " + PREFIX_NOTE + "NOTE";

    public static final String MESSAGE_SUCCESS = "Added session note for %1$s.";

    private final Index targetIndex;
    private final String noteText;

    /**
     * Creates a NoteCommand that adds {@code noteText} to the student at {@code targetIndex}.
     *
     * @param targetIndex The one-based roster index of the student.
     * @param noteText Note text that satisfies {@link seedu.address.model.session.SessionNote#isValidText}.
     */
    public NoteCommand(Index targetIndex, String noteText) {
        requireAllNonNull(targetIndex, noteText);
        this.targetIndex = targetIndex;
        this.noteText = noteText;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        StudentRosterEntry targetEntry = getRosterEntry(model, targetIndex);
        model.addSessionNote(targetEntry.getStudentId(), noteText);
        return new CommandResult(String.format(MESSAGE_SUCCESS, targetEntry.getName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof NoteCommand otherNoteCommand
                && targetIndex.equals(otherNoteCommand.targetIndex)
                && noteText.equals(otherNoteCommand.noteText);
    }

    /**
     * Returns a description of this command that leaves out the note text, which may contain private lesson
     * details.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
