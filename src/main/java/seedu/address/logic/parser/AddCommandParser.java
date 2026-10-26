package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    public static final String MESSAGE_UNKNOWN_PARAMETER = "Unknown parameter: %1$s. Supported parameters: %2$s.";
    private static final List<Prefix> SUPPORTED_PREFIXES = List.of(
            PREFIX_NAME, PREFIX_PHONE, PREFIX_SUBJECT, PREFIX_LEVEL, PREFIX_EMAIL);
    public static final String MESSAGE_SUPPORTED_PARAMETERS = SUPPORTED_PREFIXES.stream()
            .map(Prefix::toString)
            .collect(Collectors.joining(" "));

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_SUBJECT, PREFIX_LEVEL);

        Optional<Prefix> missingPrefix = findMissingRequiredPrefix(argMultimap);
        if (missingPrefix.isPresent()) {
            throw new ParseException(getMissingValueMessage(missingPrefix.get()));
        }

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT,
                    PREFIX_LEVEL);
        } catch (ParseException exception) {
            throw new ParseException(withUsage(exception.getMessage()), exception);
        }

        try {
            Student student = new Student(new StudentName(argMultimap.getValue(PREFIX_NAME).get()),
                    new ParentGuardianContact(argMultimap.getValue(PREFIX_PHONE).get(),
                            argMultimap.getValue(PREFIX_EMAIL)),
                    new Subject(argMultimap.getValue(PREFIX_SUBJECT).get()),
                    new CurrentLevel(argMultimap.getValue(PREFIX_LEVEL).get()));
            return new AddCommand(student);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(withUsage(exception.getMessage()), exception);
        }
    }

    /**
     * Returns the first required prefix that has no value, if present.
     */
    private static Optional<Prefix> findMissingRequiredPrefix(ArgumentMultimap argumentMultimap) {
        return List.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_SUBJECT, PREFIX_LEVEL).stream()
                .filter(prefix -> argumentMultimap.getValue(prefix).filter(value -> !value.isEmpty()).isEmpty())
                .findFirst();
    }

    /**
     * Returns an error message for a required prefix without a value.
     */
    private static String getMissingValueMessage(Prefix prefix) {
        return withUsage("Value missing for parameter: " + prefix);
    }

    /**
     * Appends the add-command usage to an error message.
     */
    private static String withUsage(String message) {
        return message + "\n" + AddCommand.MESSAGE_USAGE;
    }
}
