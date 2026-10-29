package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.LOADED_AT;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.session.SessionNote;

public class JsonAdaptedSessionNoteTest {

    private static final String VALID_RECORDED_AT = "2026-09-18T18:35:00+08:00";
    private static final String VALID_LAST_EDITED_AT = "2026-10-23T14:10:00+08:00";
    private static final String VALID_TEXT = "Reviewed factorisation.";

    @Test
    public void toModelType_validNote_returnsNote() throws Exception {
        assertEquals(FACTORISATION_NOTE, new JsonAdaptedSessionNote(FACTORISATION_NOTE).toModelType(LOADED_AT));
    }

    @Test
    public void constructor_noteOnTheMinute_writesSeconds() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedSessionNote(FACTORISATION_NOTE));

        assertTrue(json.contains("\"recordedAt\" : \"" + VALID_RECORDED_AT + "\""));
        assertTrue(!json.contains("lastEditedAt"));
    }

    @Test
    public void constructor_editedNote_writesLastEditedAt() throws Exception {
        SessionNote editedNote = new SessionNote(VALID_TEXT, OffsetDateTime.parse(VALID_RECORDED_AT),
                OffsetDateTime.parse(VALID_LAST_EDITED_AT));

        String json = JsonUtil.toJsonString(new JsonAdaptedSessionNote(editedNote));

        assertTrue(json.contains("\"lastEditedAt\" : \"" + VALID_LAST_EDITED_AT + "\""));
    }

    @Test
    public void toModelType_utcTimestamp_keepsOffset() throws Exception {
        SessionNote note = new JsonAdaptedSessionNote("2026-09-18T10:35:00Z", VALID_TEXT).toModelType(LOADED_AT);

        assertEquals(OffsetDateTime.parse("2026-09-18T10:35:00Z"), note.getRecordedAt());
    }

    @Test
    public void toModelType_maximumLengthText_returnsNote() throws Exception {
        String maximumLengthText = "a".repeat(SessionNote.MAX_TEXT_LENGTH);

        SessionNote note = new JsonAdaptedSessionNote(VALID_RECORDED_AT, maximumLengthText).toModelType(LOADED_AT);

        assertEquals(maximumLengthText, note.getText());
    }

    @Test
    public void toModelType_bothFieldsMissing_reportsRecordedAtFirst() {
        // Each missing field is tested alone below; this checks which one is reported when both are missing.
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedSessionNote.MISSING_FIELD_MESSAGE_FORMAT, "recordedAt"), ()
                -> new JsonAdaptedSessionNote(null, null).toModelType(LOADED_AT));
    }

    @Test
    public void toModelType_missingFields_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedSessionNote.MISSING_FIELD_MESSAGE_FORMAT, "recordedAt"), ()
                -> new JsonAdaptedSessionNote(null, VALID_TEXT).toModelType(LOADED_AT));
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedSessionNote.MISSING_FIELD_MESSAGE_FORMAT, "text"), ()
                -> new JsonAdaptedSessionNote(VALID_RECORDED_AT, null).toModelType(LOADED_AT));
    }

    @Test
    public void toModelType_invalidRecordedAt_throwsIllegalValueException() {
        assertInvalidRecordedAt("yesterday");
        assertInvalidRecordedAt("");
        assertInvalidRecordedAt("2026-09-18T18:35:00");
        assertInvalidRecordedAt("2026-09-18");
        assertInvalidRecordedAt("2026-02-30T18:35:00+08:00");
    }

    @Test
    public void toModelType_invalidRecordedAt_keepsParseFailureAsCause() {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote("yesterday", VALID_TEXT);

        IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, ()
                -> note.toModelType(LOADED_AT));

        assertInstanceOf(DateTimeParseException.class, exception.getCause());
    }

    @Test
    public void toModelType_invalidLastEditedAt_keepsParseFailureAsCause() {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote(VALID_RECORDED_AT, VALID_TEXT, "yesterday");

        IllegalValueException exception = Assertions.assertThrows(IllegalValueException.class, ()
                -> note.toModelType(LOADED_AT));

        assertInstanceOf(DateTimeParseException.class, exception.getCause());
    }

    @Test
    public void constructor_nullSource_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new JsonAdaptedSessionNote(null));
    }

    @Test
    public void toModelType_invalidLastEditedAt_throwsIllegalValueException() {
        assertInvalidLastEditedAt("yesterday");
        assertInvalidLastEditedAt("2026-09-18T18:35:00");
        assertInvalidLastEditedAt("2026-09-17T18:35:00+08:00");
    }

    @Test
    public void toModelType_invalidText_throwsIllegalValueException() {
        assertInvalidText("   ");
        assertInvalidText("Line one\nLine two");
        assertInvalidText("a".repeat(SessionNote.MAX_TEXT_LENGTH + 1));
    }

    private void assertInvalidRecordedAt(String recordedAt) {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote(recordedAt, VALID_TEXT);
        assertThrows(IllegalValueException.class, JsonAdaptedSessionNote.INVALID_RECORDED_AT_MESSAGE, ()
                -> note.toModelType(LOADED_AT));
    }

    private void assertInvalidLastEditedAt(String lastEditedAt) {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote(VALID_RECORDED_AT, VALID_TEXT, lastEditedAt);
        assertThrows(IllegalValueException.class, JsonAdaptedSessionNote.INVALID_LAST_EDITED_AT_MESSAGE, ()
                -> note.toModelType(LOADED_AT));
    }

    private void assertInvalidText(String text) {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote(VALID_RECORDED_AT, text);
        assertThrows(IllegalValueException.class, SessionNote.MESSAGE_CONSTRAINTS, () -> note.toModelType(LOADED_AT));
    }
}
