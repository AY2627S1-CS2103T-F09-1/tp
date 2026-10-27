package seedu.address.logic.parser;

import static seedu.address.logic.Messages.withFormat;
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
    public static final String MESSAGE_UNEXPECTED_PREAMBLE = "Unexpected text before the first parameter: %1$s";
    public static final String MESSAGE_MISSING_VALUE = "Value missing for parameter: %1$s";

    private static final List<Prefix> SUPPORTED_PREFIXES = List.of(
            PREFIX_NAME, PREFIX_PHONE, PREFIX_SUBJECT, PREFIX_LEVEL, PREFIX_EMAIL);
    public static final String MESSAGE_SUPPORTED_PARAMETERS = SUPPORTED_PREFIXES.stream()
            .map(Prefix::toString)
            .collect(Collectors.joining(" "));

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * Errors about the shape of the command end with the add format; errors about one value do not.
     *
     * @throws ParseException If the user input does not conform to the expected format or has an invalid value.
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ParserUtil.tokenizeStudentFields(args);

        Optional<String> unknownPrefix = ParserUtil.findUnknownPrefix(args, SUPPORTED_PREFIXES);
        if (unknownPrefix.isPresent()) {
            throw new ParseException(withAddFormat(
                    String.format(MESSAGE_UNKNOWN_PARAMETER, unknownPrefix.get(), MESSAGE_SUPPORTED_PARAMETERS)));
        }

        if (!argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(withAddFormat(
                    String.format(MESSAGE_UNEXPECTED_PREAMBLE, argMultimap.getPreamble())));
        }

        Optional<Prefix> missingPrefix = findMissingRequiredPrefix(argMultimap);
        if (missingPrefix.isPresent()) {
            throw new ParseException(withAddFormat(String.format(MESSAGE_MISSING_VALUE, missingPrefix.get())));
        }

        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT,
                    PREFIX_LEVEL);
        } catch (ParseException exception) {
            throw new ParseException(withAddFormat(exception.getMessage()), exception);
        }

        try {
            Student student = new Student(new StudentName(argMultimap.getValue(PREFIX_NAME).get()),
                    new ParentGuardianContact(argMultimap.getValue(PREFIX_PHONE).get(),
                            argMultimap.getValue(PREFIX_EMAIL)),
                    new Subject(argMultimap.getValue(PREFIX_SUBJECT).get()),
                    new CurrentLevel(argMultimap.getValue(PREFIX_LEVEL).get()));
            return new AddCommand(student);
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
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
     * Appends the one-line add-command format to an error message.
     */
    private static String withAddFormat(String message) {
        return withFormat(message, AddCommand.COMMAND_FORMAT);
    }
}
