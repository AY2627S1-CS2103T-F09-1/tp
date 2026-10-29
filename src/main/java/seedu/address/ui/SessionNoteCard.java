package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.OffsetDateTime;
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
        header.setText(formatHeader(noteNumber, sessionNote));
        noteText.setText(sessionNote.getText());
    }

    private static String formatHeader(int noteNumber, SessionNote sessionNote) {
        return "[" + noteNumber + "] " + formatSessionNote(sessionNote);
    }

    /**
     * Returns the note's recorded time, followed by its latest edit time if it has been edited.
     * Each time is the clock reading at which it was saved, shown without its UTC offset.
     */
    static String formatSessionNote(SessionNote sessionNote) {
        requireNonNull(sessionNote);
        String recordedAt = formatDateTime(sessionNote.getRecordedAt());
        return sessionNote.getLastEditedAt()
                .map(lastEditedAt -> recordedAt + " · edited " + formatDateTime(lastEditedAt))
                .orElse(recordedAt);
    }

    /**
     * Returns a friendly timestamp showing the clock reading of {@code dateTime} without its UTC offset.
     */
    static String formatDateTime(OffsetDateTime dateTime) {
        requireNonNull(dateTime);
        return TIMESTAMP_FORMATTER.format(dateTime);
    }
}
