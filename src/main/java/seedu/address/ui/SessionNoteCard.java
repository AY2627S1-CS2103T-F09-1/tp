package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    private Label timestamp;
    @FXML
    private Label noteText;

    /**
     * Creates a card displaying {@code sessionNote}.
     */
    public SessionNoteCard(SessionNote sessionNote) {
        super(FXML);
        requireNonNull(sessionNote);
        timestamp.setText(formatRecordedAt(sessionNote.getRecordedAt()));
        noteText.setText(sessionNote.getText());
    }

    /**
     * Returns a friendly timestamp that preserves the recorded UTC offset.
     */
    static String formatRecordedAt(OffsetDateTime recordedAt) {
        requireNonNull(recordedAt);
        ZoneOffset offset = recordedAt.getOffset();
        String offsetText = offset.equals(ZoneOffset.UTC) ? "+00:00" : offset.getId();
        return TIMESTAMP_FORMATTER.format(recordedAt) + " (UTC" + offsetText + ")";
    }
}
