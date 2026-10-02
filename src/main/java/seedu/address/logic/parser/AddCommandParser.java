package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_SUBJECT, PREFIX_LEVEL,
                PREFIX_ADDRESS, CliSyntax.PREFIX_TAG);

        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            return parseLegacyPerson(argMultimap);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_PHONE, PREFIX_SUBJECT, PREFIX_LEVEL)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT, PREFIX_LEVEL);
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

    private AddCommand parseLegacyPerson(ArgumentMultimap arguments) throws ParseException {
        if (!arePrefixesPresent(arguments, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL)
                || !arguments.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);
        Name name = ParserUtil.parseName(arguments.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(arguments.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(arguments.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(arguments.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tags = ParserUtil.parseTags(arguments.getAllValues(CliSyntax.PREFIX_TAG));
        return new AddCommand(new Person(name, phone, email, address, tags));
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
