package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;
import seedu.address.testutil.LogRecorder;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_add() throws Exception {
        Student student = new Student(new StudentName("Alicia Lim"),
                new ParentGuardianContact("9123 4567", Optional.empty()),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"));
        AddCommand command = (AddCommand) parser.parseCommand(
                "add n/Alicia Lim p/9123 4567 sub/Mathematics l/Secondary 3");
        assertEquals(new AddCommand(student), command);
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_confirmedDelete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased() + " /confirm");
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON, true), command);
    }

    @Test
    public void parseCommand_deleteSessionNote() throws Exception {
        DeleteSessionNoteCommand command = (DeleteSessionNoteCommand) parser.parseCommand("delete note 1 2");
        assertEquals(new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2)), command);
    }

    @Test
    public void parseCommand_confirmedDeleteSessionNote() throws Exception {
        DeleteSessionNoteCommand command = (DeleteSessionNoteCommand) parser.parseCommand(
                "delete note 1 2 /confirm");
        assertEquals(new DeleteSessionNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2), true), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setSubject(new Subject("Physics"));
        EditCommand command = (EditCommand) parser.parseCommand("edit 1 sub/Physics");

        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_editNote() throws Exception {
        EditNoteCommand command = (EditNoteCommand) parser.parseCommand("edit note 1 2 nt/Corrected summary.");
        assertEquals(new EditNoteCommand(INDEX_FIRST_PERSON, Index.fromOneBased(2), "Corrected summary."), command);
    }

    @Test
    public void parseCommand_mistypedEditNoteTarget_suggestsCorrectCommand() {
        List<String> mistypedTargets = List.of("notes", "nte", "nots", "no");
        for (String mistypedTarget : mistypedTargets) {
            String expectedMessage = Messages.withFormat(
                    String.format(AddressBookParser.MESSAGE_MISTYPED_NOTE_TARGET_FORMAT,
                            mistypedTarget, "edit note"),
                    EditNoteCommand.COMMAND_FORMAT, EditNoteCommand.COMMAND_EXAMPLE);
            Executable parseMistypedCommand = () -> parser.parseCommand(
                    "edit " + mistypedTarget + " 1 2 nt/Corrected summary.");
            assertThrows(ParseException.class, expectedMessage, parseMistypedCommand);
        }
    }

    @Test
    public void parseCommand_editWithoutTarget_usesStudentEditParser() {
        String expectedMessage = Messages.withFormat("Missing INDEX.", EditCommand.COMMAND_FORMAT,
                EditCommand.COMMAND_EXAMPLE);

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("edit"));
    }

    @Test
    public void parseCommand_editStudentWithoutIndex_reportsMissingIndex() {
        String expectedMessage = Messages.withFormat("Missing INDEX.", EditCommand.COMMAND_FORMAT,
                EditCommand.COMMAND_EXAMPLE);

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("edit n/Bob"));
    }

    @Test
    public void parseCommand_editStudentWithMistypedIndex_reportsInvalidIndex() {
        List<String> mistypedIndices = List.of("a", "abc", "-1", "+1", "1x");
        for (String mistypedIndex : mistypedIndices) {
            assertThrows(ParseException.class, "INDEX must be a positive whole number, e.g. 1.", ()
                -> parser.parseCommand("edit " + mistypedIndex + " n/Bob"));
        }
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
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
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(
                ViewCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_removedListCommand_throwsParseException() {
        String expectedMessage = AddressBookParser.getUnknownCommandMessage("list");

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("list"));
        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("list 3"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        String expectedMessage = "Enter a command. Available commands: add, delete, edit, exit, help, note, view.";

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("   "));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        String expectedMessage =
                "Unknown command: unknownCommand. Available commands: add, delete, edit, exit, help, note, view.";

        assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_removedLegacyCommands_throwParseException() {
        List<String> legacyCommands = List.of("find Alicia", "clear");

        for (String legacyCommand : legacyCommands) {
            String expectedMessage = AddressBookParser.getUnknownCommandMessage(legacyCommand.split(" ")[0]);
            assertThrows(ParseException.class, expectedMessage, () -> parser.parseCommand(legacyCommand));
        }
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
        List<String> commandWords = List.of(AddCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
                EditCommand.COMMAND_WORD, NoteCommand.COMMAND_WORD, ViewCommand.COMMAND_WORD,
                ExitCommand.COMMAND_WORD, HelpCommand.COMMAND_WORD);

        for (String commandWord : commandWords) {
            assertEquals(commandWord, AddressBookParser.getLoggableCommandWord(commandWord + " extra text"));
        }
    }

    @Test
    public void getLoggableCommandWord_removedLegacyCommandWords_returnsUnknown() {
        List<String> removedCommandWords = List.of("find", "clear");

        for (String commandWord : removedCommandWords) {
            assertEquals(AddressBookParser.UNKNOWN_COMMAND_WORD,
                    AddressBookParser.getLoggableCommandWord(commandWord + " extra text"));
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

    // Boundary values of the typo threshold (edit distance 2): distances 3 and 4 are ordinary edit input.
    @Test
    public void parseCommand_editTargetBeyondTypoDistance_isNotTreatedAsNoteTypo() {
        List<String> distantTargets = List.of("n", "abcd", "0");
        for (String distantTarget : distantTargets) {
            Executable parseDistantCommand = () -> parser.parseCommand("edit " + distantTarget + " 1");
            ParseException exception = Assertions.assertThrows(
                    ParseException.class, parseDistantCommand);
            assertFalse(exception.getMessage().contains("Unknown edit target"));
        }
    }

    @Test
    public void parseCommand_editWithoutTarget_isNotTreatedAsNoteTypo() {
        ParseException exception = Assertions.assertThrows(
                ParseException.class, () -> parser.parseCommand("edit"));
        assertFalse(exception.getMessage().contains("Unknown edit target"));
    }

}
