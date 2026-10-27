package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Requests display of the student profile at a given roster index.
 */
public class ViewCommand extends Command {

    public static final String COMMAND_WORD = "view";

    public static final String COMMAND_FORMAT = COMMAND_WORD + " INDEX";

    public static final String MESSAGE_SUCCESS = "Displaying profile for %1$s.";

    private final Index targetIndex;

    /**
     * Creates a command that displays the student at {@code targetIndex}.
     *
     * @param targetIndex The one-based roster index of the student.
     */
    public ViewCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        StudentRosterEntry targetEntry = getRosterEntry(model, targetIndex);
        return new CommandResult(String.format(MESSAGE_SUCCESS, targetEntry.getName()), targetEntry.getStudentId());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof ViewCommand otherViewCommand
                && targetIndex.equals(otherViewCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
