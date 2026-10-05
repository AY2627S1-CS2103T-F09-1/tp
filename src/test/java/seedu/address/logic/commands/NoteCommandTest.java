package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.session.SessionNote;

public class NoteCommandTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-09-18T10:35:00Z"), ZoneOffset.ofHours(8));
    private static final String NOTE_TEXT = "Reviewed factorisation.";

    private ModelManager model;

    @BeforeEach
    public void setUp() {
        AddressBook addressBook = new AddressBook();
        addressBook.addStudent(BEA);
        addressBook.addStudent(ALEX);
        model = new ModelManager(addressBook, new UserPrefs(), FIXED_CLOCK);
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NoteCommand(null, NOTE_TEXT));
        assertThrows(NullPointerException.class, () -> new NoteCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_validIndex_addsTimestampedNoteToRosterStudent() throws Exception {
        // The roster is sorted by name, so index 1 is Alex even though Bea was added first.
        CommandResult result = new NoteCommand(INDEX_FIRST_PERSON, NOTE_TEXT).execute(model);

        assertEquals(String.format(NoteCommand.MESSAGE_SUCCESS, ALEX.getName()), result.getFeedbackToUser());
        OffsetDateTime expectedRecordedAt = OffsetDateTime.parse("2026-09-18T18:35:00+08:00");
        SessionNote expectedNote = new SessionNote(NOTE_TEXT, expectedRecordedAt);
        assertEquals(List.of(expectedNote), model.getSessionHistory(ALEX.getId()).getNotes());
        assertTrue(model.getSessionHistory(BEA.getId()).isEmpty());
    }

    @Test
    public void execute_validIndex_increasesRosterNoteCount() throws Exception {
        new NoteCommand(INDEX_SECOND_PERSON, NOTE_TEXT).execute(model);
        new NoteCommand(INDEX_SECOND_PERSON, NOTE_TEXT).execute(model);

        assertEquals(List.of(0, 2), model.getStudentRoster().getEntries().stream()
                .map(entry -> entry.getNoteCount()).toList());
    }

    @Test
    public void execute_indexOutOfRange_throwsCommandException() {
        Index outOfRangeIndex = Index.fromOneBased(model.getStudentRoster().size() + 1);

        assertCommandFailure(new NoteCommand(outOfRangeIndex, NOTE_TEXT), model,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyRoster_throwsCommandException() {
        ModelManager emptyModel = new ModelManager(new AddressBook(), new UserPrefs(), FIXED_CLOCK);

        assertCommandFailure(new NoteCommand(INDEX_FIRST_PERSON, NOTE_TEXT), emptyModel,
                Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, NOTE_TEXT);

        assertTrue(noteCommand.equals(noteCommand));
        assertTrue(noteCommand.equals(new NoteCommand(INDEX_FIRST_PERSON, NOTE_TEXT)));
        assertFalse(noteCommand.equals(null));
        assertFalse(noteCommand.equals(new ListCommand()));
        assertFalse(noteCommand.equals(new NoteCommand(INDEX_SECOND_PERSON, NOTE_TEXT)));
        assertFalse(noteCommand.equals(new NoteCommand(INDEX_FIRST_PERSON, "Reviewed indices.")));
    }

    @Test
    public void toStringMethod_omitsNoteText() {
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PERSON, NOTE_TEXT);

        assertEquals(NoteCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}",
                noteCommand.toString());
    }
}
