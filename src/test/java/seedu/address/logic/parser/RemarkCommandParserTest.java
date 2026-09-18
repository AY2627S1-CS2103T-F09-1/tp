package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.REMARK_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validRemark_success() {
        assertParseSuccess(parser, INDEX_FIRST_PERSON.getOneBased() + REMARK_DESC_AMY,
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY)));
    }

    @Test
    public void parse_emptyRemark_success() {
        assertParseSuccess(parser, INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK,
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_invalidArguments_failure() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", message);
        assertParseFailure(parser, "zero" + REMARK_DESC_AMY, message);
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " unrelated", message);
    }

    @Test
    public void parse_repeatedRemark_failure() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + REMARK_DESC_AMY + REMARK_DESC_AMY;
        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }
}
