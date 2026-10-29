package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_EMPTY_ROSTER;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditNoteCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.NoteCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddCommandParser;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.model.student.Subject;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.LogRecorder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");
    private static final String NOTE_TEXT = "Reviewed indices.";
    private static final String NOTE_FIRST_STUDENT_COMMAND = NoteCommand.COMMAND_WORD + " 1 nt/" + NOTE_TEXT;
    private static final String ADD_BEA_COMMAND = "add n/Bea Lim p/9876 5432 sub/English l/JC 1";

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private Path addressBookFile;

    @BeforeEach
    public void setUp() {
        addressBookFile = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(addressBookFile);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, AddressBookParser.getUnknownCommandMessage(invalidCommand));
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_EMPTY_ROSTER);
    }

    @Test
    public void execute_removedListCommand_throwsParseException() {
        assertParseException("list", AddressBookParser.getUnknownCommandMessage("list"));
    }

    @Test
    public void execute_deletePreview_returnsInstructionsWithoutDeletingOrSaving() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");

        CommandResult result = logic.execute("delete 1");

        assertEquals("Are you sure you want to delete " + ALEX.getName() + " and 1 session note?\n"
                + "If yes, type: delete 1 /confirm\n"
                + "To cancel, enter another valid command. Typos keep this preview.",
                result.getFeedbackToUser());
        assertEquals(Optional.of(ALEX.getId()), result.getProfileStudentId());
        assertEquals(List.of(ALEX.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
        assertFalse(Files.exists(addressBookFile));
    }

    @Test
    public void execute_matchingDeleteConfirmation_deletesStudentAndSaves() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        logic.execute("delete 1");

        CommandResult result = logic.execute("delete 1 /confirm");

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_STUDENT_SUCCESS, ALEX.getName()),
                result.getFeedbackToUser());
        assertEquals(0, logic.getStudentRoster().size());
        assertTrue(model.getSessionHistory(ALEX.getId()).isEmpty());
        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(addressBookFile)
                .readAddressBook().orElseThrow();
        assertTrue(savedData.getStudentList().isEmpty());
    }

    @Test
    public void execute_deleteConfirmationWithoutPreview_throwsCommandException() {
        model.addStudent(ALEX);

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
    }

    @Test
    public void execute_mismatchedDeleteConfirmation_cancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addStudent(BEA);
        logic.execute("delete 1");

        assertCommandException("delete 2 /confirm", DeleteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(List.of(ALEX.getId(), BEA.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void execute_targetRemovedBetweenPreviewAndConfirmation_rejectsConfirmation() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");
        model.deleteStudent(ALEX.getId());

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_CONFIRMATION_MISMATCH);
    }

    @Test
    public void execute_studentAtIndexChangesBetweenPreviewAndConfirmation_rejectsConfirmation() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");
        model.deleteStudent(ALEX.getId());
        model.addStudent(BEA);

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        assertEquals(List.of(BEA.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void execute_otherCommandAfterDeletePreview_cancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");

        logic.execute("help");

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(List.of(ALEX.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void execute_malformedConfirmationAfterDeletePreview_preservesPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");

        assertThrows(ParseException.class, () -> logic.execute("delete 1 /confir"));
        assertFalse(Files.exists(addressBookFile));

        CommandResult result = logic.execute("delete 1 /confirm");

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_STUDENT_SUCCESS, ALEX.getName()),
                result.getFeedbackToUser());
        assertEquals(0, logic.getStudentRoster().size());
    }

    @Test
    public void execute_malformedOtherCommandAfterDeletePreview_preservesPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");

        assertThrows(ParseException.class, () -> logic.execute("add 123"));

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_STUDENT_SUCCESS, ALEX.getName()),
                logic.execute("delete 1 /confirm").getFeedbackToUser());
    }

    @Test
    public void execute_parsedCommandErrorAfterDeletePreview_cancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");

        assertThrows(CommandException.class, () -> logic.execute("view 2"));

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(1, logic.getStudentRoster().size());
    }

    @Test
    public void execute_newDeletePreview_replacesPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addStudent(BEA);
        logic.execute("delete 1");

        logic.execute("delete 2");
        CommandResult result = logic.execute("delete 2 /confirm");

        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_STUDENT_SUCCESS, BEA.getName()),
                result.getFeedbackToUser());
        assertEquals(List.of(ALEX.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void execute_repeatedDeleteConfirmation_throwsCommandException() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");
        logic.execute("delete 1 /confirm");

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
    }

    @Test
    public void execute_deleteSessionNotePreview_returnsInstructionsWithoutDeletingOrSaving() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        model.addSessionNote(ALEX.getId(), "Reviewed factorisation.");

        CommandResult result = logic.execute("delete note 1 2");

        assertEquals("Are you sure you want to delete session note [2] for " + ALEX.getName() + "?\n"
                + "If yes, type: delete note 1 2 /confirm\n"
                + "To cancel, enter another valid command. Typos keep this preview.",
                result.getFeedbackToUser());
        assertEquals(ALEX.getId(), result.getProfileStudentId().orElseThrow());
        assertEquals(Index.fromOneBased(2), result.getPreviewNoteNumber().orElseThrow());
        assertEquals(2, model.getSessionHistory(ALEX.getId()).size());
        assertFalse(Files.exists(addressBookFile));
    }

    @Test
    public void execute_matchingDeleteSessionNoteConfirmation_deletesOnlyNoteAndSaves() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        model.addSessionNote(ALEX.getId(), "Reviewed factorisation.");
        SessionNote targetNote = model.getSessionHistory(ALEX.getId()).getNote(Index.fromOneBased(1));
        logic.execute("delete note 1 1");

        CommandResult result = logic.execute("delete note 1 1 /confirm");

        assertEquals(String.format(DeleteSessionNoteCommand.MESSAGE_DELETE_SESSION_NOTE_SUCCESS,
                1, ALEX.getName()), result.getFeedbackToUser());
        assertEquals(List.of(ALEX.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
        assertFalse(model.getSessionHistory(ALEX.getId()).getNotes().contains(targetNote));
        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(addressBookFile)
                .readAddressBook().orElseThrow();
        assertEquals(model.getSessionHistory(ALEX.getId()), savedData.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void execute_deleteSessionNoteConfirmationWithoutPreview_throwsCommandException() {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");

        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION);
    }

    @Test
    public void execute_editNote_replacesTextPreservesTimestampAndSaves() throws Exception {
        model.addStudent(ALEX);
        SessionNote originalNote = model.addSessionNote(ALEX.getId(), "Original summary.");

        CommandResult result = logic.execute("edit note 1 1 nt/Corrected summary.");

        assertEquals(String.format(EditNoteCommand.MESSAGE_SUCCESS, 1, ALEX.getName()), result.getFeedbackToUser());
        SessionNote editedNote = model.getSessionHistory(ALEX.getId()).getNote(Index.fromOneBased(1));
        assertEquals("Corrected summary.", editedNote.getText());
        assertEquals(originalNote.getRecordedAt(), editedNote.getRecordedAt());
        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(addressBookFile)
                .readAddressBook().orElseThrow();
        assertEquals(model.getSessionHistory(ALEX.getId()), savedData.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void execute_mismatchedDeleteSessionNoteConfirmation_cancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        model.addSessionNote(ALEX.getId(), "Reviewed factorisation.");
        SessionHistory originalHistory = model.getSessionHistory(ALEX.getId());
        logic.execute("delete note 1 1");

        assertCommandException("delete note 1 2 /confirm",
                DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(originalHistory, model.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void execute_sessionHistoryChangesBeforeConfirmation_rejectsConfirmation() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        logic.execute("delete note 1 1");

        model.addSessionNote(ALEX.getId(), "Reviewed factorisation.");

        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        assertEquals(2, model.getSessionHistory(ALEX.getId()).size());
    }

    @Test
    public void execute_noteStudentAtIndexChangesBeforeConfirmation_rejectsConfirmation() throws Exception {
        model.addStudent(ALEX);
        model.addStudent(BEA);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        model.addSessionNote(BEA.getId(), "Reviewed essay structure.");
        logic.execute("delete note 1 1");

        model.deleteStudent(ALEX.getId());

        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        assertEquals(1, model.getSessionHistory(BEA.getId()).size());
    }

    @Test
    public void execute_otherCommandAfterDeleteSessionNotePreview_cancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        logic.execute("delete note 1 1");

        logic.execute("help");

        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
    }

    @Test
    public void execute_malformedCommandAfterDeleteSessionNotePreview_preservesPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        logic.execute("delete note 1 1");

        assertThrows(ParseException.class, () -> logic.execute("delete note 1 1 /confir"));
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
        assertFalse(Files.exists(addressBookFile));

        CommandResult result = logic.execute("delete note 1 1 /confirm");

        assertEquals(String.format(DeleteSessionNoteCommand.MESSAGE_DELETE_SESSION_NOTE_SUCCESS,
                1, ALEX.getName()), result.getFeedbackToUser());
        assertTrue(model.getSessionHistory(ALEX.getId()).isEmpty());
    }

    @Test
    public void execute_crossTypeDeleteConfirmation_rejectsAndCancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        logic.execute("delete 1");

        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH);
        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
    }

    @Test
    public void execute_newDeleteSessionNotePreview_replacesPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        model.addSessionNote(ALEX.getId(), "Reviewed factorisation.");
        logic.execute("delete note 1 1");

        logic.execute("delete note 1 2");
        CommandResult result = logic.execute("delete note 1 2 /confirm");

        assertEquals(String.format(DeleteSessionNoteCommand.MESSAGE_DELETE_SESSION_NOTE_SUCCESS,
                2, ALEX.getName()), result.getFeedbackToUser());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
    }

    @Test
    public void execute_repeatedDeleteSessionNoteConfirmation_throwsCommandException() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        logic.execute("delete note 1 1");
        logic.execute("delete note 1 1 /confirm");

        assertCommandException("delete note 1 1 /confirm",
                DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION);
    }

    @Test
    public void execute_addStudent_success() throws Exception {
        String addCommand = "add n/Bea Lim p/9876 5432 sub/English l/JC 1";
        Model expectedModel = new ModelManager();
        expectedModel.addStudent(BEA);

        assertCommandSuccess(addCommand,
                String.format(AddCommand.MESSAGE_STUDENT_SUCCESS, BEA.getName(), BEA.getSubject(),
                        BEA.getCurrentLevel()), expectedModel);
    }

    @Test
    public void execute_editStudent_successSavesAndPreservesStableData() throws Exception {
        model.addStudent(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");

        CommandResult result = logic.execute("edit 1 n/Alexis Tan p/8000 0001 "
                + "e/guardian@example.com sub/Physics l/Secondary 4");

        assertEquals("Edited student: Alexis Tan (Physics, Secondary 4).", result.getFeedbackToUser());
        assertEquals(ALEX.getId(), model.getStudentProfile(ALEX.getId()).orElseThrow().getStudentId());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
        ReadOnlyAddressBook savedData = new JsonAddressBookStorage(addressBookFile)
                .readAddressBook().orElseThrow();
        assertEquals(ALEX.getId(), savedData.getStudentList().get(0).getId());
        assertEquals(new StudentName("Alexis Tan"), savedData.getStudentList().get(0).getName());
        assertEquals(new Subject("Physics"), savedData.getStudentList().get(0).getSubject());
        assertEquals(new CurrentLevel("Secondary 4"), savedData.getStudentList().get(0).getCurrentLevel());
        assertEquals(model.getSessionHistory(ALEX.getId()), savedData.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void execute_editStudentAfterDeletePreview_cancelsPendingDeletion() throws Exception {
        model.addStudent(ALEX);
        logic.execute("delete 1");

        logic.execute("edit 1 sub/Physics");

        assertCommandException("delete 1 /confirm", DeleteCommand.MESSAGE_NO_PENDING_DELETION);
        assertEquals(new Subject("Physics"), model.getStudentProfile(ALEX.getId()).orElseThrow().getSubject());
    }

    @Test
    public void execute_legacyPersonAdd_throwsParseException() {
        String legacyAddCommand =
                "add n/Amy Bee p/11111111 e/amy@example.com a/Block 312, Amy Street 1 t/friend";

        assertParseException(legacyAddCommand, Messages.withFormat(
                String.format(AddCommandParser.MESSAGE_UNKNOWN_PARAMETER, "a/",
                        AddCommandParser.MESSAGE_SUPPORTED_PARAMETERS),
                                AddCommand.COMMAND_FORMAT, AddCommand.COMMAND_EXAMPLE));
    }

    @Test
    public void execute_addStudentWithPrefixLikeText_success() throws Exception {
        String addCommand = "add n/Amy Bee p/11111111 e/amy@example.com "
                + "sub/Mathematics nt/Science l/Secondary nt/1";
        Student student = new Student(new StudentName("Amy Bee"),
                new ParentGuardianContact("11111111", Optional.of("amy@example.com")),
                new Subject("Mathematics nt/Science"), new CurrentLevel("Secondary nt/1"));
        Model expectedModel = new ModelManager();
        expectedModel.addStudent(student);

        assertCommandSuccess(addCommand,
                String.format(AddCommand.MESSAGE_STUDENT_SUCCESS, student.getName(), student.getSubject(),
                        student.getCurrentLevel()), expectedModel);
    }

    @Test
    public void execute_removedLegacyCommands_throwParseException() {
        model.addStudent(ALEX);
        List<String> legacyCommands = List.of("find Alicia", "clear");

        for (String legacyCommand : legacyCommands) {
            assertParseException(legacyCommand,
                    AddressBookParser.getUnknownCommandMessage(legacyCommand.split(" ")[0]));
        }

        assertEquals(List.of(ALEX.getId()), logic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, getExceptionDataFile(), DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, getExceptionDataFile()));
    }

    @Test
    public void execute_commandsWithoutDataChangesWhenSavesFail_succeed() throws Exception {
        Model studentModel = createModelWithAlex();
        Logic failingLogic = createLogicWithFailingSaves(studentModel, Integer.MAX_VALUE);

        CommandResult viewResult = failingLogic.execute(ViewCommand.COMMAND_WORD + " 1");
        CommandResult helpResult = failingLogic.execute(HelpCommand.COMMAND_WORD);
        CommandResult exitResult = failingLogic.execute(ExitCommand.COMMAND_WORD);

        assertEquals(Optional.of(ALEX.getId()), viewResult.getProfileStudentId());
        assertTrue(helpResult.isShowHelp());
        assertTrue(exitResult.isExit());
    }

    @Test
    public void execute_commandsWithoutDataChanges_leaveUnloadableDataFileUnchanged() throws Exception {
        String unloadableData = "{ \"students\" : [ { \"name\" : \"\" } ] }";
        Files.writeString(addressBookFile, unloadableData);

        logic.execute(HelpCommand.COMMAND_WORD);
        logic.execute(ExitCommand.COMMAND_WORD);

        assertEquals(unloadableData, Files.readString(addressBookFile));
    }

    @Test
    public void execute_noteWithStorageFailure_restoresSessionHistory() {
        Model studentModel = createModelWithAlex();
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);
        assertThrows(CommandException.class, getSaveFailureMessage(), () ->
                failingLogic.execute(NOTE_FIRST_STUDENT_COMMAND));

        assertTrue(studentModel.getSessionHistory(ALEX.getId()).isEmpty());
        assertEquals(0, failingLogic.getStudentRoster().getEntries().get(0).getNoteCount());
    }

    @Test
    public void execute_storageFailure_logsRollbackWithoutNoteText() throws Exception {
        Model studentModel = createModelWithAlex();
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);

        List<String> logMessages = LogRecorder.captureMessages(LogicManager.class, () ->
                assertThrows(CommandException.class, () -> failingLogic.execute(NOTE_FIRST_STUDENT_COMMAND)));

        assertTrue(logMessages.contains("Could not save data; restoring the data from before the command."));
        assertTrue(logMessages.stream().noneMatch(message -> message.contains(NOTE_TEXT)));
    }

    @Test
    public void execute_addStudentWithStorageFailure_restoresRoster() {
        Model studentModel = createModelWithAlex();
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);
        assertThrows(CommandException.class, getSaveFailureMessage(), () -> failingLogic.execute(ADD_BEA_COMMAND));

        assertEquals(List.of(ALEX.getId()), failingLogic.getStudentRoster().getEntries().stream()
                .map(StudentRosterEntry::getStudentId).toList());
    }

    @Test
    public void execute_editStudentWithStorageFailure_restoresStudentAndSessionHistory() {
        Model studentModel = createModelWithAlex();
        studentModel.addSessionNote(ALEX.getId(), "Reviewed indices.");
        SessionHistory originalHistory = studentModel.getSessionHistory(ALEX.getId());
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);

        assertThrows(CommandException.class, getSaveFailureMessage(), () ->
                failingLogic.execute("edit 1 n/Alexis Tan sub/Physics"));

        assertEquals(ALEX.getName(), studentModel.getStudentProfile(ALEX.getId()).orElseThrow().getName());
        assertEquals(ALEX.getSubject(), studentModel.getStudentProfile(ALEX.getId()).orElseThrow().getSubject());
        assertEquals(originalHistory, studentModel.getSessionHistory(ALEX.getId()));
    }

    @Test
    public void execute_editStudentWithUnchangedValueWhenStorageWouldFail_skipsSaveAndSucceeds() throws Exception {
        Model studentModel = createModelWithAlex();
        Logic failingLogic = createLogicWithFailingSaves(studentModel, Integer.MAX_VALUE);

        CommandResult result = failingLogic.execute("edit 1 n/Alex Tan");

        assertEquals(String.format(EditCommand.MESSAGE_SUCCESS, ALEX.getName(), ALEX.getSubject(),
                ALEX.getCurrentLevel()), result.getFeedbackToUser());
    }

    @Test
    public void execute_deleteWithStorageFailure_restoresStudentAndSessionHistory() throws Exception {
        Model studentModel = createModelWithAlex();
        studentModel.addSessionNote(ALEX.getId(), "Reviewed indices.");
        Path savedFile = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(savedFile).saveAddressBook(studentModel.getAddressBook());
        String savedData = Files.readString(savedFile);
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);

        failingLogic.execute("delete 1");
        assertThrows(CommandException.class, getSaveFailureMessage(), () ->
                failingLogic.execute("delete 1 /confirm"));

        StudentRosterEntry restoredEntry = failingLogic.getStudentRoster().getEntries().get(0);
        assertEquals(ALEX.getId(), restoredEntry.getStudentId());
        assertEquals(1, restoredEntry.getNoteCount());
        assertEquals(1, studentModel.getSessionHistory(ALEX.getId()).size());
        assertEquals(savedData, Files.readString(savedFile));
        assertThrows(CommandException.class, DeleteCommand.MESSAGE_NO_PENDING_DELETION, () ->
                failingLogic.execute("delete 1 /confirm"));
    }

    @Test
    public void execute_deleteSessionNoteWithStorageFailure_restoresHistory() throws Exception {
        Model studentModel = createModelWithAlex();
        studentModel.addSessionNote(ALEX.getId(), "Reviewed indices.");
        studentModel.addSessionNote(ALEX.getId(), "Reviewed factorisation.");
        SessionHistory originalHistory = studentModel.getSessionHistory(ALEX.getId());
        Path savedFile = temporaryFolder.resolve("addressBook.json");
        new JsonAddressBookStorage(savedFile).saveAddressBook(studentModel.getAddressBook());
        String savedData = Files.readString(savedFile);
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);

        failingLogic.execute("delete note 1 1");
        assertThrows(CommandException.class, getSaveFailureMessage(), () ->
                failingLogic.execute("delete note 1 1 /confirm"));

        assertEquals(originalHistory, studentModel.getSessionHistory(ALEX.getId()));
        assertEquals(2, failingLogic.getStudentRoster().getEntries().get(0).getNoteCount());
        assertEquals(savedData, Files.readString(savedFile));
        assertThrows(CommandException.class, DeleteSessionNoteCommand.MESSAGE_NO_PENDING_DELETION, () ->
                failingLogic.execute("delete note 1 1 /confirm"));
    }

    @Test
    public void execute_editNoteWithStorageFailure_restoresHistory() {
        Model studentModel = createModelWithAlex();
        studentModel.addSessionNote(ALEX.getId(), "Original summary.");
        SessionHistory originalHistory = studentModel.getSessionHistory(ALEX.getId());
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);

        assertThrows(CommandException.class, getSaveFailureMessage(), () -> failingLogic
                .execute("edit note 1 1 nt/Corrected summary."));

        assertEquals(originalHistory, studentModel.getSessionHistory(ALEX.getId()));
        assertEquals(1, failingLogic.getStudentRoster().getEntries().get(0).getNoteCount());
    }

    @Test
    public void execute_successAfterStorageFailure_savesOnlyLaterChange() throws Exception {
        Model studentModel = createModelWithAlex();
        Logic failingLogic = createLogicWithFailingSaves(studentModel, 1);
        assertThrows(CommandException.class, () -> failingLogic.execute(NOTE_FIRST_STUDENT_COMMAND));

        failingLogic.execute(ADD_BEA_COMMAND);

        Path savedFile = temporaryFolder.resolve("addressBook.json");
        JsonAddressBookStorage savedStorage = new JsonAddressBookStorage(savedFile);
        ReadOnlyAddressBook savedData = savedStorage.readAddressBook().orElseThrow();
        assertEquals(2, savedData.getStudentList().size());
        assertTrue(savedData.getSessionHistory(ALEX.getId()).isEmpty());
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    @Test
    public void getStudentRoster_returnsModelRoster() {
        assertEquals(model.getStudentRoster().getEntries(), logic.getStudentRoster().getEntries());
    }

    @Test
    public void getStudentProfile_returnsModelProfile() {
        model.addStudent(ALEX);

        assertEquals(model.getStudentProfile(ALEX.getId()), logic.getStudentProfile(ALEX.getId()));
    }

    @Test
    public void execute_commandWithArguments_logsCommandWordOnly() {
        List<String> logMessages = LogRecorder.captureMessages(LogicManager.class, () ->
                assertThrows(CommandException.class, () -> logic.execute(NOTE_FIRST_STUDENT_COMMAND)));

        assertTrue(logMessages.contains("----------------[USER COMMAND][note]"));
        assertTrue(logMessages.stream().noneMatch(message -> message.contains("Reviewed indices")));
    }

    @Test
    public void execute_editCommand_logsCommandWordOnly() {
        model.addStudent(ALEX);
        String commandText = "edit 1 n/Alexis Tan p/8000 0001";

        List<String> logMessages = LogRecorder.captureMessages(LogicManager.class, () ->
                assertDoesNotThrow(() -> logic.execute(commandText)));

        assertTrue(logMessages.contains("----------------[USER COMMAND][edit]"));
        assertTrue(logMessages.stream()
                .noneMatch(message -> message.contains("Alexis") || message.contains("8000")));
    }

    @Test
    public void execute_unknownCommandWord_logsUnknown() {
        String commandText = "Alex 9123 4567";

        List<String> logMessages = LogRecorder.captureMessages(LogicManager.class, () ->
                assertThrows(ParseException.class, () -> logic.execute(commandText)));

        assertTrue(logMessages.contains("----------------[USER COMMAND][unknown]"));
        assertTrue(logMessages.stream()
                .noneMatch(message -> message.contains("Alex") || message.contains("9123")));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(getExceptionDataFile()) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing a student add command.
        // Saving fails, so the student added by the command is removed again.
        String addCommand = "add n/Bea Lim p/9876 5432 sub/English l/JC 1";
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }

    /**
     * Returns a model whose address book holds only {@code ALEX}.
     */
    private Model createModelWithAlex() {
        Model studentModel = new ModelManager();
        studentModel.addStudent(ALEX);
        return studentModel;
    }

    /**
     * Returns the data file used by the storage in {@link #assertCommandFailureForExceptionFromStorage}.
     */
    private Path getExceptionDataFile() {
        return temporaryFolder.resolve("ExceptionAddressBook.json");
    }

    /**
     * Returns the error expected when saving to {@code addressBook.json} throws {@code DUMMY_IO_EXCEPTION}.
     */
    private String getSaveFailureMessage() {
        return String.format(LogicManager.FILE_OPS_ERROR_FORMAT, addressBookFile,
                DUMMY_IO_EXCEPTION.getMessage());
    }

    /**
     * Returns a {@code LogicManager} for {@code model} whose address book storage writes to a temporary file,
     * but throws {@code DUMMY_IO_EXCEPTION} for the first {@code failureCount} saves.
     */
    private LogicManager createLogicWithFailingSaves(Model model, int failureCount) {
        JsonAddressBookStorage addressBookStorage =
                new FailingAddressBookStorage(temporaryFolder.resolve("addressBook.json"), failureCount);
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        return new LogicManager(model, new StorageManager(addressBookStorage, userPrefsStorage));
    }

    /**
     * A {@code JsonAddressBookStorage} that fails a given number of saves before saving normally.
     */
    private static class FailingAddressBookStorage extends JsonAddressBookStorage {
        private int remainingFailures;

        FailingAddressBookStorage(Path filePath, int failureCount) {
            super(filePath);
            remainingFailures = failureCount;
        }

        @Override
        public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
            if (remainingFailures > 0) {
                remainingFailures--;
                throw DUMMY_IO_EXCEPTION;
            }
            super.saveAddressBook(addressBook);
        }
    }
}
