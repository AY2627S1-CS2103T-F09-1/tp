package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new {@link ViewCommand} object.
 */
public class ViewCommandParser implements Parser<ViewCommand> {

    /**
     * Parses {@code args} as the roster index of the student profile to display.
     *
     * @throws ParseException If the index is missing, repeated, not a positive whole number, or too large.
     */
    @Override
    public ViewCommand parse(String args) throws ParseException {
        Index index = ParserUtil.parseOnlyStudentIndex(args, ViewCommand.COMMAND_WORD,
                ViewCommand.COMMAND_FORMAT, ViewCommand.COMMAND_EXAMPLE);
        return new ViewCommand(index);
    }
}
