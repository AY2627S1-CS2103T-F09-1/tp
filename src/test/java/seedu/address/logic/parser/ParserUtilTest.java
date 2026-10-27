package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.INDEX_LABEL;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_MISSING_INDEX;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_NOTE_NUMBER_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_STUDENT_INDEX_TOO_LARGE;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_TOO_MANY_INDICES;
import static seedu.address.logic.parser.ParserUtil.NOTE_NUMBER_LABEL;
import static seedu.address.logic.parser.ParserUtil.STUDENT_INDEX_LABEL;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

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
                String.format(MESSAGE_MISSING_INDEX, INDEX_LABEL), "view INDEX");
        String repeatedIndexMessage = Messages.withFormat(
                String.format(MESSAGE_TOO_MANY_INDICES, "view", INDEX_LABEL), "view INDEX");

        assertThrows(ParseException.class, missingIndexMessage, ()
            -> ParserUtil.parseOnlyStudentIndex(WHITESPACE, "view", "view INDEX"));
        assertThrows(ParseException.class, repeatedIndexMessage, ()
            -> ParserUtil.parseOnlyStudentIndex(" 1 2 ", "view", "view INDEX"));
    }

    @Test
    public void parseOnlyStudentIndex_singleIndex_success() throws Exception {
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseOnlyStudentIndex(" 1 ", "view", "view INDEX"));
    }

    @Test
    public void findUnknownPrefix_supportedAndOrdinarySlashes_returnsEmpty() {
        assertTrue(ParserUtil.findUnknownPrefix(" n/Alex p/9123 4567 sub/E Math/A Math",
                List.of(new Prefix("n/"), new Prefix("p/"), new Prefix("sub/"))).isEmpty());
    }

    @Test
    public void findUnknownPrefix_unsupportedPrefix_returnsFirstUnsupportedPrefix() {
        assertEquals(Optional.of("x/"), ParserUtil.findUnknownPrefix(" n/Alex x/value a/address",
                List.of(new Prefix("n/"))));
    }

    @Test
    public void findUnknownPrefix_nullArguments_throwNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.findUnknownPrefix(null, List.of()));
        assertThrows(NullPointerException.class, () -> ParserUtil.findUnknownPrefix("", null));
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

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
