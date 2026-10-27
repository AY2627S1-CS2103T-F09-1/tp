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
        header.setText("[" + noteNumber + "] " + formatRecordedAt(sessionNote));
        noteText.setText(sessionNote.getText());
    }

    /**
     * Returns a friendly timestamp that preserves the recorded UTC offset.
     */
    static String formatRecordedAt(SessionNote sessionNote) {
        requireNonNull(sessionNote);
        String recordedAt = formatDateTime(sessionNote.getRecordedAt(), true);
        return sessionNote.getLastEditedAt()
                .map(lastEditedAt -> recordedAt + " · edited "
                        + formatDateTime(lastEditedAt, !lastEditedAt.getOffset()
                                .equals(sessionNote.getRecordedAt().getOffset())))
                .orElse(recordedAt);
    }

    private static String formatDateTime(OffsetDateTime recordedAt, boolean includeOffset) {
        requireNonNull(recordedAt);
        if (!includeOffset) {
            return TIMESTAMP_FORMATTER.format(recordedAt);
        }
        ZoneOffset offset = recordedAt.getOffset();
        String offsetText = offset.equals(ZoneOffset.UTC) ? "+00:00" : offset.getId();
        return TIMESTAMP_FORMATTER.format(recordedAt) + " (UTC" + offsetText + ")";
    }
}
