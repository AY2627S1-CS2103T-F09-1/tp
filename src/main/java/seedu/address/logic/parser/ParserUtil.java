package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.session.SessionNote;

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
    public static final String MESSAGE_EMPTY_NOTE = "Session note is empty. Enter the note text after " + PREFIX_NOTE
            + ".";
    public static final String MESSAGE_NOTE_TOO_LONG = "Session note has %1$d characters, more than the "
            + SessionNote.MAX_TEXT_LENGTH + "-character limit. Shorten it to " + SessionNote.MAX_TEXT_LENGTH
            + " characters or fewer.";
    public static final String MESSAGE_NOTE_NON_PRINTABLE = "Session note contains a tab, line break or other"
            + " non-printable character. Enter the note on one line without them.";
    private static final Pattern WHOLE_NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final String WHITESPACE_PATTERN = "\\s+";
    private static final List<Prefix> LEGACY_PERSON_PREFIXES = List.of(PREFIX_ADDRESS, PREFIX_TAG);

    /**
     * Returns the first legacy person prefix present in {@code arguments}.
     */
    public static Optional<Prefix> findLegacyPersonPrefix(ArgumentMultimap arguments) {
        requireNonNull(arguments);
        return LEGACY_PERSON_PREFIXES.stream()
                .filter(prefix -> arguments.getValue(prefix).isPresent())
                .findFirst();
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
     * @param commandExample One-line example shown below {@code commandFormat}.
     * @throws ParseException If the index is missing, repeated, not a positive whole number, or too large.
     */
    public static Index parseOnlyStudentIndex(String args, String commandWord, String commandFormat,
            String commandExample) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(Messages.withFormat(
                    String.format(MESSAGE_MISSING_INDEX, INDEX_LABEL), commandFormat, commandExample));
        }
        if (trimmedArgs.split(WHITESPACE_PATTERN).length > 1) {
            throw new ParseException(Messages.withFormat(
                    String.format(MESSAGE_TOO_MANY_INDICES, commandWord, INDEX_LABEL), commandFormat, commandExample));
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

    /**
     * Returns {@code noteText} unchanged after checking that it is valid session-note text.
     * Surrounding whitespace is kept because {@code SessionNote} removes it when the note is created.
     * The rules are checked in the order the User Guide states them (empty, too long, then not on one line), and
     * only the first broken rule is reported, so an overlong note with a tab reports its length.
     *
     * @throws ParseException If the text is empty, longer than {@link SessionNote#MAX_TEXT_LENGTH} characters, or
     *         contains a non-printable character; the message names the rule that was broken.
     */
    public static String parseNoteText(String noteText) throws ParseException {
        requireNonNull(noteText);
        int length = SessionNote.countTextLength(noteText);
        if (length == 0) {
            throw new ParseException(MESSAGE_EMPTY_NOTE);
        }
        if (length > SessionNote.MAX_TEXT_LENGTH) {
            throw new ParseException(String.format(MESSAGE_NOTE_TOO_LONG, length));
        }
        if (SessionNote.hasNonPrintableCharacter(noteText)) {
            throw new ParseException(MESSAGE_NOTE_NON_PRINTABLE);
        }
        assert SessionNote.isValidText(noteText) : "Note text passed every rule but is still invalid";
        return noteText;
    }

    private static boolean isWholeNumberAboveIntegerRange(String value) {
        return WHOLE_NUMBER_PATTERN.matcher(value).matches()
                && new BigInteger(value).compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0;
    }

}
