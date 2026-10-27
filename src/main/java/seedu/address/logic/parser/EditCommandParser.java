package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
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
        ArgumentMultimap argMultimap = ParserUtil.tokenizeStudentFields(args);

        Optional<String> unknownPrefix = ParserUtil.findUnknownPrefix(args, SUPPORTED_PREFIXES);
        if (unknownPrefix.isPresent()) {
            throw new ParseException(withEditFormat(String.format(AddCommandParser.MESSAGE_UNKNOWN_PARAMETER,
                    unknownPrefix.get(), MESSAGE_SUPPORTED_PARAMETERS)));
        }

        Index index = ParserUtil.parseOnlyStudentIndex(argMultimap.getPreamble(), EditCommand.COMMAND_WORD,
                EditCommand.COMMAND_FORMAT);
        verifyNoDuplicatePrefixes(argMultimap);
        EditStudentDescriptor descriptor = parseDescriptor(argMultimap);
        if (!descriptor.isAnyFieldEdited()) {
            throw new ParseException(withEditFormat(EditCommand.MESSAGE_NOT_EDITED));
        }
        return new EditCommand(index, descriptor);
    }

    private static void verifyNoDuplicatePrefixes(ArgumentMultimap argMultimap) throws ParseException {
        try {
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT,
                    PREFIX_LEVEL);
        } catch (ParseException exception) {
            throw new ParseException(withEditFormat(exception.getMessage()), exception);
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
            throw new ParseException(exception.getMessage(), exception);
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

    /**
     * Appends the one-line student edit format to an error about the shape of the command.
     */
    private static String withEditFormat(String message) {
        return Messages.withFormat(message, EditCommand.COMMAND_FORMAT);
    }
}
