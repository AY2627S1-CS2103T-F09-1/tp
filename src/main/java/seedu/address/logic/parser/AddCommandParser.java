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
    private static final List<Prefix> REQUIRED_PREFIXES = List.of(
            PREFIX_NAME, PREFIX_PHONE, PREFIX_SUBJECT, PREFIX_LEVEL);
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
        verifyCommandShape(argMultimap);
        return new AddCommand(createStudent(argMultimap));
    }

    /**
     * Verifies that {@code argMultimap} has no legacy prefix, no preamble, every required value,
     * and no duplicated prefix, in that order.
     *
     * @throws ParseException If any shape rule is violated, with the add format appended.
     */
    private static void verifyCommandShape(ArgumentMultimap argMultimap) throws ParseException {
        Optional<Prefix> legacyPrefix = ParserUtil.findLegacyPersonPrefix(argMultimap);
        if (legacyPrefix.isPresent()) {
            throw new ParseException(withAddFormat(
                    String.format(MESSAGE_UNKNOWN_PARAMETER, legacyPrefix.get(), MESSAGE_SUPPORTED_PARAMETERS)));
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
    }

    /**
     * Returns the student described by {@code argMultimap}, whose required values are present.
     *
     * @throws ParseException If a present value violates its domain constraints.
     */
    private static Student createStudent(ArgumentMultimap argMultimap) throws ParseException {
        try {
            return new Student(new StudentName(getVerifiedValue(argMultimap, PREFIX_NAME)),
                    new ParentGuardianContact(getVerifiedValue(argMultimap, PREFIX_PHONE),
                            argMultimap.getValue(PREFIX_EMAIL)),
                    new Subject(getVerifiedValue(argMultimap, PREFIX_SUBJECT)),
                    new CurrentLevel(getVerifiedValue(argMultimap, PREFIX_LEVEL)));
        } catch (IllegalArgumentException exception) {
            throw new ParseException(exception.getMessage(), exception);
        }
    }

    /**
     * Returns the value of a required {@code prefix} that {@link #verifyCommandShape} has already checked.
     * Fails fast with {@link java.util.NoSuchElementException} if that precondition is ever broken.
     */
    private static String getVerifiedValue(ArgumentMultimap argMultimap, Prefix prefix) {
        return argMultimap.getValue(prefix).orElseThrow();
    }

    /**
     * Returns the first required prefix that has no value, if present.
     */
    private static Optional<Prefix> findMissingRequiredPrefix(ArgumentMultimap argumentMultimap) {
        return REQUIRED_PREFIXES.stream()
                .filter(prefix -> argumentMultimap.getValue(prefix).filter(value -> !value.isEmpty()).isEmpty())
                .findFirst();
    }

    /**
     * Appends the one-line add-command format to an error message.
     */
    private static String withAddFormat(String message) {
        return withFormat(message, AddCommand.COMMAND_FORMAT, AddCommand.COMMAND_EXAMPLE);
    }
}
