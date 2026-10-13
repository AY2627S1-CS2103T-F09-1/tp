package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.LogRecorder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_note() throws Exception {
        String userInput = NoteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased()
                + " nt/Reviewed factorisation.";
        NoteCommand command = (NoteCommand) parser.parseCommand(userInput);
        assertEquals(new NoteCommand(INDEX_FIRST_PERSON, "Reviewed factorisation."), command);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
    }

    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(
                ViewCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_listWithArguments_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(
                ListCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_commandWithArguments_logsCommandWordOnly() {
        List<String> logMessages = LogRecorder.captureMessages(AddressBookParser.class, () ->
                assertThrows(ParseException.class, () -> parser.parseCommand("note x nt/Reviewed indices.")));

        assertTrue(logMessages.contains("Command word: note"));
        assertTrue(logMessages.stream().noneMatch(message -> message.contains("Reviewed indices")));
    }

    @Test
    public void parseCommand_unknownCommandWord_logsNoUserInput() {
        List<String> logMessages = LogRecorder.captureMessages(AddressBookParser.class, () ->
                assertThrows(ParseException.class, () -> parser.parseCommand("Alex 9123 4567")));

        assertTrue(logMessages.contains("Command word: unknown"));
        assertTrue(logMessages.stream()
                .noneMatch(message -> message.contains("Alex") || message.contains("9123")));
    }

    @Test
    public void getLoggableCommandWord_knownCommandWithArguments_returnsCommandWordOnly() {
        assertEquals(NoteCommand.COMMAND_WORD,
                AddressBookParser.getLoggableCommandWord("  note 1 nt/Reviewed indices."));
    }

    @Test
    public void getLoggableCommandWord_everyCommandWord_returnsCommandWord() {
        List<String> commandWords = List.of(AddCommand.COMMAND_WORD, EditCommand.COMMAND_WORD,
                DeleteCommand.COMMAND_WORD, ClearCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
                ListCommand.COMMAND_WORD, NoteCommand.COMMAND_WORD, ViewCommand.COMMAND_WORD,
                ExitCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD);

        for (String commandWord : commandWords) {
            assertEquals(commandWord, AddressBookParser.getLoggableCommandWord(commandWord + " extra text"));
        }
    }

    @Test
    public void getLoggableCommandWord_unknownCommandWord_returnsUnknown() {
        assertEquals(AddressBookParser.UNKNOWN_COMMAND_WORD,
                AddressBookParser.getLoggableCommandWord("Alex 9123 4567"));
    }

    @Test
    public void getLoggableCommandWord_blankInput_returnsUnknown() {
        assertEquals(AddressBookParser.UNKNOWN_COMMAND_WORD, AddressBookParser.getLoggableCommandWord("   "));
    }
}
