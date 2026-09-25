package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validArgs_returnsRemarkCommand() {
        assertParseSuccess(parser, "1 r/Likes baseball",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Likes baseball")));
    }

    @Test
    public void parse_emptyRemark_returnsRemarkCommand() {
        assertParseSuccess(parser, "1 r/", new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        assertParseFailure(parser, "one r/Likes baseball",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicateRemark_throwsParseException() {
        assertParseFailure(parser, "1 r/First r/Second", "Multiple values specified for the following single-valued "
                + "field(s): r/");
    }
}
