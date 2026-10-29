package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import seedu.address.model.session.SessionNote;

/**
 * Displays one timestamped session note.
 */
public class SessionNoteCard extends UiPart<Region> {

    private static final String FXML = "SessionNoteCard.fxml";
    private static final String UTC_OFFSET_TEXT = "+00:00";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("d MMM uuuu, h:mm a", Locale.ENGLISH);

    @FXML
    private SelectableText header;
    @FXML
    private SelectableText noteText;

    /**
     * Creates a card displaying {@code sessionNote} with its chronological {@code noteNumber}.
     */
    public SessionNoteCard(SessionNote sessionNote, int noteNumber) {
        super(FXML);
        requireNonNull(sessionNote);
        if (noteNumber < 1) {
            throw new IllegalArgumentException("Session note number must be positive");
        }
        header.setText(formatHeader(noteNumber, sessionNote.getRecordedAt()));
        noteText.setText(sessionNote.getText());
    }

    private static String formatHeader(int noteNumber, OffsetDateTime recordedAt) {
        return "[" + noteNumber + "] " + formatRecordedAt(recordedAt);
    }

    /**
     * Returns a friendly timestamp that preserves the recorded UTC offset.
     */
    static String formatRecordedAt(OffsetDateTime recordedAt) {
        requireNonNull(recordedAt);
        ZoneOffset offset = recordedAt.getOffset();
        String offsetText = offset.equals(ZoneOffset.UTC) ? UTC_OFFSET_TEXT : offset.getId();
        return TIMESTAMP_FORMATTER.format(recordedAt) + " (UTC" + offsetText + ")";
    }
}
