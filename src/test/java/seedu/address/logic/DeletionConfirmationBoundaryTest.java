package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteSessionNoteCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.ModelManager;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Checks confirmation partitions that depend on changes after the preview.
 */
public class DeletionConfirmationBoundaryTest {
    @TempDir
    public Path testFolder;

    private ModelManager model;
    private LogicManager logic;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addStudent(ALEX);
        model.addStudent(BEA);
        model.addSessionNote(ALEX.getId(), "First session.");
        model.addSessionNote(BEA.getId(), "Other session.");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(testFolder.resolve("data.json")),
                new JsonUserPrefsStorage(testFolder.resolve("preferences.json")));
        logic = new LogicManager(model, storage);
    }

    @Test
    public void confirmStudent_afterNotePreview_rejectsDifferentDeletionType() throws Exception {
        logic.execute("delete note 1 1");

        CommandException exception = assertThrows(CommandException.class, () -> logic.execute("delete 1 /confirm"));

        assertEquals(DeleteCommand.MESSAGE_CONFIRMATION_MISMATCH, exception.getMessage());
        assertTrue(model.getStudent(ALEX.getId()).isPresent());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
    }

    @Test
    public void confirmNote_withDifferentStudentIndex_rejectsConfirmation() throws Exception {
        logic.execute("delete note 1 1");

        CommandException exception = assertThrows(CommandException.class, () ->
                logic.execute("delete note 2 1 /confirm"));

        assertEquals(DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH, exception.getMessage());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
        assertEquals(1, model.getSessionHistory(BEA.getId()).size());
    }

    @Test
    public void confirmNote_afterTargetRemoved_rejectsConfirmation() throws Exception {
        logic.execute("delete note 2 1");
        model.deleteStudent(BEA.getId());

        CommandException exception = assertThrows(CommandException.class, () ->
                logic.execute("delete note 2 1 /confirm"));

        assertEquals(DeleteSessionNoteCommand.MESSAGE_CONFIRMATION_MISMATCH, exception.getMessage());
        assertTrue(model.getStudent(ALEX.getId()).isPresent());
        assertEquals(1, model.getSessionHistory(ALEX.getId()).size());
    }
}
