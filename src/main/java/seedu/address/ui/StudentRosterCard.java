package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Displays the fields of one student roster entry.
 */
public class StudentRosterCard extends UiPart<Region> {

    private static final String FXML = "StudentRosterCard.fxml";
    private static final String SUBJECT_LABEL = "Subject: ";
    private static final String LEVEL_LABEL = "Level: ";
    private static final String SESSION_NOTES_LABEL = "Session notes: ";

    @FXML
    private HBox cardPane;
    @FXML
    private Label rosterIndex;
    @FXML
    private Label name;
    @FXML
    private Label subject;
    @FXML
    private Label currentLevel;
    @FXML
    private Label noteCount;

    /**
     * Creates a card for the supplied roster entry.
     *
     * @param entry The immutable roster entry to display.
     */
    public StudentRosterCard(StudentRosterEntry entry) {
        super(FXML);
        rosterIndex.setText(String.valueOf(entry.getRosterIndex()));
        name.setText(entry.getName().getValue());
        subject.setText(SUBJECT_LABEL + entry.getSubject().getValue());
        currentLevel.setText(LEVEL_LABEL + entry.getCurrentLevel().getValue());
        noteCount.setText(SESSION_NOTES_LABEL + entry.getNoteCount());
    }
}
