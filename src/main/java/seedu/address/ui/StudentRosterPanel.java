package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Displays the TutorTrack student roster as scrollable cards.
 */
public class StudentRosterPanel extends UiPart<Region> {

    private static final String FXML = "StudentRosterPanel.fxml";

    private final ObservableList<StudentRosterEntry> rosterEntries = FXCollections.observableArrayList();

    @FXML
    private ListView<StudentRosterEntry> studentRosterListView;
    @FXML
    private Label emptyRosterMessage;

    /**
     * Creates a student roster panel with the supplied snapshot.
     *
     * @param roster The roster snapshot to display.
     */
    public StudentRosterPanel(StudentRoster roster) {
        super(FXML);
        studentRosterListView.setItems(rosterEntries);
        studentRosterListView.setCellFactory(listView -> new StudentRosterListViewCell());
        setRoster(roster);
    }

    /**
     * Replaces the displayed roster with a fresh immutable snapshot.
     *
     * @param roster The roster snapshot to display.
     */
    public void setRoster(StudentRoster roster) {
        requireNonNull(roster);
        rosterEntries.setAll(roster.getEntries());
        boolean isEmpty = rosterEntries.isEmpty();
        emptyRosterMessage.setVisible(isEmpty);
        emptyRosterMessage.setManaged(isEmpty);
    }

    /**
     * Displays one roster entry as a student card.
     */
    static class StudentRosterListViewCell extends ListCell<StudentRosterEntry> {
        @Override
        protected void updateItem(StudentRosterEntry entry, boolean empty) {
            super.updateItem(entry, empty);

            if (empty || entry == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new StudentRosterCard(entry).getRoot());
            }
        }
    }
}
