package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentRoster;
import seedu.address.model.student.StudentRosterEntry;

/**
 * Displays the TutorTrack student roster as scrollable cards.
 */
public class StudentRosterPanel extends UiPart<Region> {

    private static final String FXML = "StudentRosterPanel.fxml";
    private static final PseudoClass PROFILE_SELECTED = PseudoClass.getPseudoClass("profile-selected");

    private final ObservableList<StudentRosterEntry> rosterEntries = FXCollections.observableArrayList();
    private StudentId selectedProfileStudentId;

    @FXML
    private ListView<StudentRosterEntry> studentRosterListView;
    @FXML
    private VBox emptyRosterState;

    /**
     * Creates a student roster panel with the supplied snapshot.
     *
     * @param roster The roster snapshot to display.
     */
    public StudentRosterPanel(StudentRoster roster) {
        super(FXML);
        studentRosterListView.setItems(rosterEntries);
        studentRosterListView.setCellFactory(listView -> new StudentRosterListViewCell());
        studentRosterListView.widthProperty().addListener((observable, oldWidth, newWidth) ->
                studentRosterListView.refresh());
        setRoster(roster);
    }

    /**
     * Replaces the displayed roster with a fresh immutable snapshot.
     *
     * @param roster The roster snapshot to display.
     */
    public void setRoster(StudentRoster roster) {
        requireNonNull(roster);
        StudentId studentIdToReselect = selectedProfileStudentId;
        rosterEntries.setAll(roster.getEntries());
        if (studentIdToReselect != null) {
            selectStudent(studentIdToReselect);
        }
        boolean isEmpty = rosterEntries.isEmpty();
        emptyRosterState.setVisible(isEmpty);
        emptyRosterState.setManaged(isEmpty);
    }

    /**
     * Selects the roster entry with the supplied stable student identifier.
     * Clears the selection when the student is not in the current roster.
     *
     * @param studentId The stable identifier of the student to select.
     */
    public void selectStudent(StudentId studentId) {
        requireNonNull(studentId);
        rosterEntries.stream()
                .filter(entry -> entry.getStudentId().equals(studentId))
                .findFirst()
                .ifPresentOrElse(entry -> {
                    selectedProfileStudentId = studentId;
                    studentRosterListView.getSelectionModel().select(entry);
                    studentRosterListView.refresh();
                }, this::clearSelection);
    }

    /**
     * Clears the selected roster entry.
     */
    public void clearSelection() {
        selectedProfileStudentId = null;
        studentRosterListView.getSelectionModel().clearSelection();
        studentRosterListView.refresh();
    }

    /**
     * Displays one roster entry as a student card.
     */
    class StudentRosterListViewCell extends ListCell<StudentRosterEntry> {
        @Override
        protected void updateItem(StudentRosterEntry entry, boolean empty) {
            super.updateItem(entry, empty);
            boolean isDisplayedProfile = !empty && entry != null
                    && entry.getStudentId().equals(selectedProfileStudentId);
            pseudoClassStateChanged(PROFILE_SELECTED, isDisplayedProfile);

            if (empty || entry == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new StudentRosterCard(entry).getRoot());
            }
        }
    }
}
