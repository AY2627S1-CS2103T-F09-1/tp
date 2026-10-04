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
        rosterIndex.setText(entry.getRosterIndex() + ". ");
        name.setText(entry.getName().getValue());
        subject.setText("Subject: " + entry.getSubject().getValue());
        currentLevel.setText("Level: " + entry.getCurrentLevel().getValue());
        noteCount.setText("Session notes: " + entry.getNoteCount());
    }
}
