package seedu.address.storage;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.session.SessionNote;

/**
 * Jackson-friendly version of {@link SessionNote}.
 */
class JsonAdaptedSessionNote {

    static final String MISSING_FIELD_MESSAGE_FORMAT = "Session note's %s field is missing!";
    static final String INVALID_RECORDED_AT_MESSAGE =
            "Session note timestamps must include a date, time, and offset, "
            + "such as 2026-09-18T18:35:00+08:00.";

    private static final String RECORDED_AT_FIELD = "recordedAt";
    private static final String TEXT_FIELD = "text";

    /**
     * Always writes seconds, unlike {@link OffsetDateTime#toString()}, which omits them when they are zero.
     */
    private static final DateTimeFormatter RECORDED_AT_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final String recordedAt;
    private final String text;

    /**
     * Constructs a Jackson-friendly session note from persisted fields.
     */
    @JsonCreator
    JsonAdaptedSessionNote(@JsonProperty(RECORDED_AT_FIELD) String recordedAt,
            @JsonProperty(TEXT_FIELD) String text) {
        this.recordedAt = recordedAt;
        this.text = text;
    }

    /**
     * Constructs a Jackson-friendly session note from a domain session note.
     */
    JsonAdaptedSessionNote(SessionNote source) {
        recordedAt = source.getRecordedAt().format(RECORDED_AT_FORMAT);
        text = source.getText();
    }

    /**
     * Returns the validated domain session note represented by this JSON object.
     *
     * @throws IllegalValueException If a persisted field is missing or invalid.
     */
    SessionNote toModelType() throws IllegalValueException {
        return new SessionNote(requireValidText(), requireValidRecordedAt());
    }

    /**
     * Returns the recorded date-time parsed from {@code recordedAt}.
     *
     * @throws IllegalValueException If {@code recordedAt} is missing or not an offset date-time.
     */
    private OffsetDateTime requireValidRecordedAt() throws IllegalValueException {
        if (recordedAt == null) {
            throw createMissingFieldException(RECORDED_AT_FIELD);
        }
        try {
            return OffsetDateTime.parse(recordedAt, RECORDED_AT_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new IllegalValueException(INVALID_RECORDED_AT_MESSAGE);
        }
    }

    /**
     * Returns {@code text} after checking that it is present and valid.
     *
     * @throws IllegalValueException If {@code text} is missing or invalid.
     */
    private String requireValidText() throws IllegalValueException {
        if (text == null) {
            throw createMissingFieldException(TEXT_FIELD);
        }
        if (!SessionNote.isValidText(text)) {
            throw new IllegalValueException(SessionNote.MESSAGE_CONSTRAINTS);
        }
        return text;
    }

    private IllegalValueException createMissingFieldException(String fieldName) {
        return new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, fieldName));
    }
}
