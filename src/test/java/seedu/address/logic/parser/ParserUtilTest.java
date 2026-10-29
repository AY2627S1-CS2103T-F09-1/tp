package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.ParserUtil.INDEX_LABEL;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_EMPTY_NOTE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_NOTE_NON_PRINTABLE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_NOTE_NUMBER_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_NOTE_TOO_LONG;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_STUDENT_INDEX_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_TOO_MANY_INDICES;
import static seedu.address.logic.parser.ParserUtil.NOTE_NUMBER_LABEL;
import static seedu.address.logic.parser.ParserUtil.STUDENT_INDEX_LABEL;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.session.SessionNote;

public class ParserUtilTest {
    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseStudentIndex_notPositiveWholeNumber_throwsInvalidIndexMessage() {
        String expectedMessage = String.format(MESSAGE_INVALID_INDEX, INDEX_LABEL);
        String[] invalidIndices = {"", "0", "00", "-1", "+1", "1.5", "abc", "10 a", "-99999999999"};

        for (String invalidIndex : invalidIndices) {
            assertThrows(ParseException.class, expectedMessage, ()
                -> ParserUtil.parseStudentIndex(invalidIndex, INDEX_LABEL));
        }
    }

    @Test
    public void parseStudentIndex_aboveIntegerRange_throwsTooLargeMessage() {
        String expectedMessage = String.format(MESSAGE_STUDENT_INDEX_TOO_LARGE, STUDENT_INDEX_LABEL);

        assertThrows(ParseException.class, expectedMessage, ()
            -> ParserUtil.parseStudentIndex(Long.toString(Integer.MAX_VALUE + 1L), STUDENT_INDEX_LABEL));
        assertThrows(ParseException.class, expectedMessage, ()
            -> ParserUtil.parseStudentIndex("99999999999", STUDENT_INDEX_LABEL));
    }

    @Test
    public void parseStudentIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseStudentIndex("1", INDEX_LABEL));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseStudentIndex("  1  ", INDEX_LABEL));

        // Largest supported index
        assertEquals(Index.fromOneBased(Integer.MAX_VALUE),
                ParserUtil.parseStudentIndex(Integer.toString(Integer.MAX_VALUE), INDEX_LABEL));
    }

    @Test
    public void parseNoteNumber_invalidInput_throwsNoteNumberMessages() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_INDEX, NOTE_NUMBER_LABEL), ()
            -> ParserUtil.parseNoteNumber("0"));
        assertThrows(ParseException.class, String.format(MESSAGE_NOTE_NUMBER_TOO_LARGE, NOTE_NUMBER_LABEL), ()
            -> ParserUtil.parseNoteNumber("2147483648"));
    }

    @Test
    public void parseOnlyStudentIndex_missingOrRepeatedIndex_throwsMessageWithFormat() {
        String missingIndexMessage = Messages.withFormat(
                String.format(MESSAGE_MISSING_INDEX, INDEX_LABEL), "view INDEX", "view 1");
        String repeatedIndexMessage = Messages.withFormat(
                String.format(MESSAGE_TOO_MANY_INDICES, "view", INDEX_LABEL), "view INDEX", "view 1");

        assertThrows(ParseException.class, missingIndexMessage, ()
            -> ParserUtil.parseOnlyStudentIndex(WHITESPACE, "view", "view INDEX", "view 1"));
        assertThrows(ParseException.class, repeatedIndexMessage, ()
            -> ParserUtil.parseOnlyStudentIndex(" 1 2 ", "view", "view INDEX", "view 1"));
    }

    @Test
    public void parseOnlyStudentIndex_singleIndex_success() throws Exception {
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseOnlyStudentIndex(" 1 ", "view", "view INDEX", "view 1"));
    }

    @Test
    public void parseNoteText_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseNoteText(null));
    }

    @Test
    public void parseNoteText_validText_returnsTextUnchanged() throws Exception {
        String minimumLengthNote = "a".repeat(SessionNote.MIN_TEXT_LENGTH);
        String maximumLengthNote = "a".repeat(SessionNote.MAX_TEXT_LENGTH);

        assertEquals("  Reviewed  indices.  ", ParserUtil.parseNoteText("  Reviewed  indices.  "));
        assertEquals(minimumLengthNote, ParserUtil.parseNoteText(minimumLengthNote));
        assertEquals(maximumLengthNote, ParserUtil.parseNoteText(maximumLengthNote));
    }

    @Test
    public void parseNoteText_blankText_throwsEmptyNoteMessage() {
        assertThrows(ParseException.class, MESSAGE_EMPTY_NOTE, () -> ParserUtil.parseNoteText(""));
        assertThrows(ParseException.class, MESSAGE_EMPTY_NOTE, () -> ParserUtil.parseNoteText(WHITESPACE));
    }

    @Test
    public void parseNoteText_aboveMaximumLength_throwsMessageWithEnteredLength() {
        int overLimit = SessionNote.MAX_TEXT_LENGTH + 1;

        assertThrows(ParseException.class, String.format(MESSAGE_NOTE_TOO_LONG, overLimit), ()
                -> ParserUtil.parseNoteText("a".repeat(overLimit)));
        assertThrows(ParseException.class, String.format(MESSAGE_NOTE_TOO_LONG, 800), ()
                -> ParserUtil.parseNoteText("😀".repeat(800)));
    }

    @Test
    public void parseNoteText_aboveMaximumLengthWithTab_reportsLengthFirst() {
        String overlongNoteWithTab = "a\t" + "a".repeat(SessionNote.MAX_TEXT_LENGTH);
        String expectedMessage = String.format(MESSAGE_NOTE_TOO_LONG, SessionNote.MAX_TEXT_LENGTH + 2);

        assertThrows(ParseException.class, expectedMessage, () -> ParserUtil.parseNoteText(overlongNoteWithTab));
    }

    @Test
    public void parseNoteText_nonPrintableCharacter_throwsNonPrintableMessage() {
        assertThrows(ParseException.class, MESSAGE_NOTE_NON_PRINTABLE, ()
                -> ParserUtil.parseNoteText("Column\tvalue"));
        assertThrows(ParseException.class, MESSAGE_NOTE_NON_PRINTABLE, ()
                -> ParserUtil.parseNoteText("Line one\nLine two"));
    }

    @Test
    public void findLegacyPersonPrefix_legacyPrefixPresent_returnsFirstLegacyPrefix() {
        ArgumentMultimap arguments = ParserUtil.tokenizeStudentFields(" n/Alex a/Address t/friend");

        assertEquals(Optional.of(CliSyntax.PREFIX_ADDRESS), ParserUtil.findLegacyPersonPrefix(arguments));
    }

    @Test
    public void findLegacyPersonPrefix_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.findLegacyPersonPrefix(null));
    }

    @Test
    public void tokenizeStudentFields_studentAndLegacyPrefixes_tokenizesEveryKnownField() {
        ArgumentMultimap arguments = ParserUtil.tokenizeStudentFields(
                "1 n/Alex p/9123 e/a@b.com sub/Math l/Sec 3 a/Legacy t/old");

        assertEquals("1", arguments.getPreamble());
        assertEquals(Optional.of("Alex"), arguments.getValue(CliSyntax.PREFIX_NAME));
        assertEquals(Optional.of("9123"), arguments.getValue(CliSyntax.PREFIX_PHONE));
        assertEquals(Optional.of("a@b.com"), arguments.getValue(CliSyntax.PREFIX_EMAIL));
        assertEquals(Optional.of("Math"), arguments.getValue(CliSyntax.PREFIX_SUBJECT));
        assertEquals(Optional.of("Sec 3"), arguments.getValue(CliSyntax.PREFIX_LEVEL));
        assertEquals(Optional.of("Legacy"), arguments.getValue(CliSyntax.PREFIX_ADDRESS));
        assertEquals(Optional.of("old"), arguments.getValue(CliSyntax.PREFIX_TAG));
    }

    @Test
    public void tokenizeStudentFields_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.tokenizeStudentFields(null));
    }

    // Equivalence partition: leading zeros are a plausible typing mistake, not a different number.
    @Test
    public void parseStudentIndex_leadingZeros_parsedAsPlainNumber() throws Exception {
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseStudentIndex("01", INDEX_LABEL));
        assertEquals(Index.fromOneBased(Integer.MAX_VALUE),
                ParserUtil.parseStudentIndex("0002147483647", INDEX_LABEL));
    }

    // Boundary: a number just above the integer range stays "too large" even when padded with zeros.
    @Test
    public void parseNoteNumber_paddedAboveIntegerRange_throwsTooLargeMessage() {
        String expectedMessage = String.format(MESSAGE_NOTE_NUMBER_TOO_LARGE, NOTE_NUMBER_LABEL);

        assertThrows(ParseException.class, expectedMessage, ()
            -> ParserUtil.parseNoteNumber("0002147483648"));
    }

    @Test
    public void parseOnlyStudentIndex_tabSeparatedIndices_throwsMessageWithFormat() {
        String repeatedIndexMessage = Messages.withFormat(
                String.format(MESSAGE_TOO_MANY_INDICES, "view", INDEX_LABEL), "view INDEX", "view 1");

        assertThrows(ParseException.class, repeatedIndexMessage, ()
            -> ParserUtil.parseOnlyStudentIndex("1\t2", "view", "view INDEX", "view 1"));
    }

}
