package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments for the list command.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the list command arguments and returns a list command.
     *
     * @param args The arguments supplied after the list command word.
     * @return A list command when no arguments are supplied.
     * @throws ParseException if extra arguments are supplied.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        if (!args.trim().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        }
        return new ListCommand();
    }
}
