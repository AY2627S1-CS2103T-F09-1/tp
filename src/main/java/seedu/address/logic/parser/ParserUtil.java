package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String INDEX_LABEL = "INDEX";
    public static final String STUDENT_INDEX_LABEL = "STUDENT_INDEX";
    public static final String NOTE_NUMBER_LABEL = "NOTE_NUMBER";

    public static final String MESSAGE_INVALID_INDEX = "%1$s must be a positive whole number, e.g. 1.";
    public static final String MESSAGE_STUDENT_INDEX_TOO_LARGE =
            "%1$s is too large. Enter a student index shown in the roster.";
    public static final String MESSAGE_NOTE_NUMBER_TOO_LARGE =
            "%1$s is too large. Enter a note number shown in the profile.";
    public static final String MESSAGE_MISSING_INDEX = "Missing %1$s.";
    public static final String MESSAGE_TOO_MANY_INDICES = "%1$s takes only one %2$s.";

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?:^|\\s)([a-z][a-z0-9_-]{0,2})/");
    private static final Pattern WHOLE_NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final String WHITESPACE_PATTERN = "\\s+";

    /**
     * Returns the first prefix-like token in {@code args} that is not in {@code supportedPrefixes}.
     */
    public static Optional<String> findUnknownPrefix(String args, Collection<Prefix> supportedPrefixes) {
        requireNonNull(args);
        requireNonNull(supportedPrefixes);
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group(1) + "/";
            boolean isSupported = supportedPrefixes.stream()
                    .anyMatch(supportedPrefix -> supportedPrefix.toString().equals(prefix));
            if (!isSupported) {
                return Optional.of(prefix);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns student command arguments tokenized with supported and legacy person prefixes.
     */
    public static ArgumentMultimap tokenizeStudentFields(String args) {
        requireNonNull(args);
        return ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_SUBJECT, PREFIX_LEVEL,
                PREFIX_ADDRESS, PREFIX_TAG);
    }

    /**
     * Returns the single student index that makes up {@code args}, the whole argument string of
     * {@code commandWord}.
     *
     * @param args Arguments that should contain exactly one {@code INDEX}.
     * @param commandWord Command word named in the error when more than one value is given.
     * @param commandFormat One-line command format shown when the index is missing or repeated.
     * @throws ParseException If the index is missing, repeated, not a positive whole number, or too large.
     */
    public static Index parseOnlyStudentIndex(String args, String commandWord, String commandFormat)
            throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(Messages.withFormat(
                    String.format(MESSAGE_MISSING_INDEX, INDEX_LABEL), commandFormat));
        }
        if (trimmedArgs.split(WHITESPACE_PATTERN).length > 1) {
            throw new ParseException(Messages.withFormat(
                    String.format(MESSAGE_TOO_MANY_INDICES, commandWord, INDEX_LABEL), commandFormat));
        }
        return parseStudentIndex(trimmedArgs, INDEX_LABEL);
    }

    /**
     * Parses {@code studentIndex} into an {@code Index}. Leading and trailing whitespaces will be trimmed.
     *
     * @param studentIndex One-based roster index entered by the user.
     * @param label Placeholder that names the index in the command format, such as {@code INDEX}.
     * @throws ParseException If the value is not a positive whole number, or is too large to be an index.
     */
    public static Index parseStudentIndex(String studentIndex, String label) throws ParseException {
        return parseIndex(studentIndex, label, MESSAGE_STUDENT_INDEX_TOO_LARGE);
    }

    /**
     * Parses {@code noteNumber} into an {@code Index}. Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException If the value is not a positive whole number, or is too large to be a note number.
     */
    public static Index parseNoteNumber(String noteNumber) throws ParseException {
        return parseIndex(noteNumber, NOTE_NUMBER_LABEL, MESSAGE_NOTE_NUMBER_TOO_LARGE);
    }

    /**
     * Parses {@code oneBasedIndex}, reporting a value above {@code Integer.MAX_VALUE} with
     * {@code tooLargeMessage} and any other invalid value as not a positive whole number.
     */
    private static Index parseIndex(String oneBasedIndex, String label, String tooLargeMessage)
            throws ParseException {
        requireNonNull(oneBasedIndex);
        String trimmedIndex = oneBasedIndex.trim();
        if (StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            return Index.fromOneBased(Integer.parseInt(trimmedIndex));
        }
        if (isWholeNumberAboveIntegerRange(trimmedIndex)) {
            throw new ParseException(String.format(tooLargeMessage, label));
        }
        throw new ParseException(String.format(MESSAGE_INVALID_INDEX, label));
    }

    private static boolean isWholeNumberAboveIntegerRange(String value) {
        return WHOLE_NUMBER_PATTERN.matcher(value).matches()
                && new BigInteger(value).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0;
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
