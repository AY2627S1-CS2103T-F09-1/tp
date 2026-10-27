package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.ViewCommand;

public class ViewCommandParserTest {

    private final ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validIndex_returnsViewCommand() {
        assertParseSuccess(parser, " 1 ", new ViewCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, " 2147483647", new ViewCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_missingIndex_throwsMissingIndexWithFormat() {
        assertParseFailure(parser, "", Messages.withFormat("Missing INDEX.",
                ViewCommand.COMMAND_FORMAT, ViewCommand.COMMAND_EXAMPLE));
        assertParseFailure(parser, "   ", Messages.withFormat("Missing INDEX.",
                ViewCommand.COMMAND_FORMAT, ViewCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_notPositiveWholeNumber_throwsInvalidIndex() {
        String[] invalidIndices = {"0", "-1", "abc", "1.5", "+1"};

        for (String invalidIndex : invalidIndices) {
            assertParseFailure(parser, invalidIndex, "INDEX must be a positive whole number, e.g. 1.");
        }
    }

    @Test
    public void parse_severalValues_throwsOnlyOneIndexWithFormat() {
        assertParseFailure(parser, "1 2",
                Messages.withFormat("view takes only one INDEX.",
                        ViewCommand.COMMAND_FORMAT, ViewCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void parse_aboveIntegerRange_throwsTooLarge() {
        String expectedMessage = "INDEX is too large. Enter a student index shown in the roster.";

        assertParseFailure(parser, "2147483648", expectedMessage);
        assertParseFailure(parser, "99999999999", expectedMessage);
    }
}
