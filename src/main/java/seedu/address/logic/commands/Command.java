package seedu.address.logic.commands;

import static seedu.address.logic.Messages.MESSAGE_EMPTY_ROSTER;
import static seedu.address.logic.Messages.MESSAGE_STUDENT_INDEX_OUT_OF_RANGE;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

    /**
     * Returns the roster entry displayed at {@code index}.
     *
     * @throws CommandException If the roster is empty, or if {@code index} is outside it; the message gives the
     *         valid range.
     */
    protected static StudentRosterEntry getRosterEntry(Model model, Index index) throws CommandException {
        List<StudentRosterEntry> rosterEntries = model.getStudentRoster().getEntries();
        if (rosterEntries.isEmpty()) {
            throw new CommandException(MESSAGE_EMPTY_ROSTER);
        }
        if (index.getZeroBased() >= rosterEntries.size()) {
            throw new CommandException(String.format(MESSAGE_STUDENT_INDEX_OUT_OF_RANGE,
                    index.getOneBased(), rosterEntries.size()));
        }
        return rosterEntries.get(index.getZeroBased());
    }

}
