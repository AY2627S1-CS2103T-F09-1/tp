package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

/**
 * Parses arguments for editing a student.
 */
public class EditCommandParser implements Parser<EditCommand> {

    private static final List<Prefix> SUPPORTED_PREFIXES = List.of(
            PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT, PREFIX_LEVEL);
    private static final String MESSAGE_SUPPORTED_PARAMETERS = SUPPORTED_PREFIXES.stream()
            .map(Prefix::toString)
            .collect(Collectors.joining(" "));

    /**
     * Parses {@code args} as a student roster index and one or more replacement fields.
     *
     * @throws ParseException If the index, prefixes, or replacement values are invalid.
     */
    @Override
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_SUBJECT, PREFIX_LEVEL, PREFIX_ADDRESS, PREFIX_TAG);

        Optional<String> unknownPrefix = ParserUtil.findUnknownPrefix(args, SUPPORTED_PREFIXES);
        if (unknownPrefix.isPresent()) {
            throw new ParseException(withUsage(String.format(AddCommandParser.MESSAGE_UNKNOWN_PARAMETER,
                    unknownPrefix.get(), MESSAGE_SUPPORTED_PARAMETERS)));
        }

        Index index = parseIndex(argMultimap.getPreamble());
        verifyNoDuplicatePrefixes(argMultimap);
        EditStudentDescriptor descriptor = parseDescriptor(argMultimap);
        if (!descriptor.isAnyFieldEdited()) {
            throw new ParseException(withUsage(EditCommand.MESSAGE_NOT_EDITED));
        }
        return new EditCommand(index, descriptor);
    }

    private static Index parseIndex(String preamble) throws ParseException {
        try {
            return ParserUtil.parseIndex(preamble);
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE),
                    exception);
        }
    }

    private static void verifyNoDuplicatePrefixes(ArgumentMultimap argMultimap) throws ParseException {
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT,
                    PREFIX_LEVEL);
        } catch (ParseException exception) {
            throw new ParseException(withUsage(exception.getMessage()), exception);
        }
    }

    private static EditStudentDescriptor parseDescriptor(ArgumentMultimap argMultimap) throws ParseException {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        try {
            argMultimap.getValue(PREFIX_NAME).ifPresent(value -> descriptor.setName(new StudentName(value)));
            argMultimap.getValue(PREFIX_PHONE).ifPresent(value -> descriptor.setParentPhone(parsePhone(value)));
            argMultimap.getValue(PREFIX_EMAIL).ifPresent(value -> descriptor.setParentEmail(parseEmail(value)));
            argMultimap.getValue(PREFIX_SUBJECT).ifPresent(value -> descriptor.setSubject(new Subject(value)));
            argMultimap.getValue(PREFIX_LEVEL)
                    .ifPresent(value -> descriptor.setCurrentLevel(new CurrentLevel(value)));
            return descriptor;
        } catch (IllegalArgumentException exception) {
            throw new ParseException(withUsage(exception.getMessage()), exception);
        }
    }

    private static String parsePhone(String phone) {
        if (!ParentGuardianContact.isValidPhone(phone)) {
            throw new IllegalArgumentException(ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
        }
        return phone.trim();
    }

    private static Optional<String> parseEmail(String email) {
        if (email.isEmpty()) {
            return Optional.empty();
        }
        if (!ParentGuardianContact.isValidEmail(email)) {
            throw new IllegalArgumentException(ParentGuardianContact.EMAIL_MESSAGE_CONSTRAINTS);
        }
        return Optional.of(email.trim());
    }

    private static String withUsage(String message) {
        return message + "\n" + EditCommand.MESSAGE_USAGE;
    }
}
