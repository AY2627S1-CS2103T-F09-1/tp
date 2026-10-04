package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.student.StudentRoster;

/**
 * Lists all students in the TutorTrack roster to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    public static final String MESSAGE_USAGE = "Usage: list";
    public static final String MESSAGE_SUCCESS = "Listed %d students.";
    public static final String MESSAGE_EMPTY_ROSTER =
            "No students are in the roster. Add a student with the add command.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        StudentRoster roster = model.getStudentRoster();
        if (roster.size() == 0) {
            return new CommandResult(MESSAGE_EMPTY_ROSTER);
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, roster.size()));
    }
}
