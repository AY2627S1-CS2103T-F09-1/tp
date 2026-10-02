package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;

/**
 * Adds a student to TutorTrack.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a student to TutorTrack. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PARENT_PHONE "
            + PREFIX_SUBJECT + "SUBJECT "
            + PREFIX_LEVEL + "CURRENT_LEVEL "
            + "[" + PREFIX_EMAIL + "PARENT_EMAIL]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Alicia Lim "
            + PREFIX_PHONE + "+65 9123 4567 "
            + PREFIX_EMAIL + "mrs.lim@example.com "
            + PREFIX_SUBJECT + "Mathematics "
            + PREFIX_LEVEL + "Secondary 3";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_STUDENT_SUCCESS = "New student added: %1$s";
    public static final String MESSAGE_DUPLICATE_STUDENT = "A student with this name and parent phone already exists.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private final Student toAdd;
    private final Person legacyPerson;

    /**
     * Creates an AddCommand to add the specified student.
     */
    public AddCommand(Student student) {
        requireNonNull(student);
        toAdd = student;
        legacyPerson = null;
    }

    /**
     * Creates a legacy AddCommand for existing non-TutorTrack callers.
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = null;
        legacyPerson = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        if (legacyPerson != null) {
            return executeLegacyPersonAdd(model);
        }

        if (model.hasStudent(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_STUDENT);
        }

        model.addStudent(toAdd);
        return new CommandResult(String.format(MESSAGE_STUDENT_SUCCESS, toAdd.getName()));
    }

    private CommandResult executeLegacyPersonAdd(Model model) throws CommandException {
        if (model.hasPerson(legacyPerson)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
        model.addPerson(legacyPerson);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(legacyPerson)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return java.util.Objects.equals(toAdd, otherAddCommand.toAdd)
                && java.util.Objects.equals(legacyPerson, otherAddCommand.legacyPerson);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", legacyPerson == null ? toAdd : legacyPerson)
                .toString();
    }
}
