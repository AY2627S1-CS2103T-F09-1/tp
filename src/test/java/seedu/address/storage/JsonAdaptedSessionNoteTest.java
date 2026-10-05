package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.session.SessionNote;

public class JsonAdaptedSessionNoteTest {

    private static final String VALID_RECORDED_AT = "2026-09-18T18:35:00+08:00";
    private static final String VALID_TEXT = "Reviewed factorisation.";

    @Test
    public void toModelType_validNote_returnsNote() throws Exception {
        assertEquals(FACTORISATION_NOTE, new JsonAdaptedSessionNote(FACTORISATION_NOTE).toModelType());
    }

    @Test
    public void constructor_noteOnTheMinute_writesSeconds() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedSessionNote(FACTORISATION_NOTE));

        assertTrue(json.contains("\"recordedAt\" : \"" + VALID_RECORDED_AT + "\""));
    }

    @Test
    public void toModelType_utcTimestamp_keepsOffset() throws Exception {
        SessionNote note = new JsonAdaptedSessionNote("2026-09-18T10:35:00Z", VALID_TEXT).toModelType();

        assertEquals(OffsetDateTime.parse("2026-09-18T10:35:00Z"), note.getRecordedAt());
    }

    @Test
    public void toModelType_missingFields_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedSessionNote.MISSING_FIELD_MESSAGE_FORMAT, "recordedAt"),
                new JsonAdaptedSessionNote(null, VALID_TEXT)::toModelType);
        assertThrows(IllegalValueException.class,
                String.format(JsonAdaptedSessionNote.MISSING_FIELD_MESSAGE_FORMAT, "text"),
                new JsonAdaptedSessionNote(VALID_RECORDED_AT, null)::toModelType);
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
    public void toModelType_invalidText_throwsIllegalValueException() {
        assertInvalidText("   ");
        assertInvalidText("Line one\nLine two");
        assertInvalidText("a".repeat(SessionNote.MAX_TEXT_LENGTH + 1));
    }

    private void assertInvalidRecordedAt(String recordedAt) {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote(recordedAt, VALID_TEXT);
        assertThrows(IllegalValueException.class, JsonAdaptedSessionNote.INVALID_RECORDED_AT_MESSAGE,
                note::toModelType);
    }

    private void assertInvalidText(String text) {
        JsonAdaptedSessionNote note = new JsonAdaptedSessionNote(VALID_RECORDED_AT, text);
        assertThrows(IllegalValueException.class, SessionNote.MESSAGE_CONSTRAINTS, note::toModelType);
    }
}
