package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.session.SessionNote;

/**
 * Displays the contact, learning context, and chronological notes for one student profile.
 */
public class StudentProfilePanel extends UiPart<Region> {

    private static final String FXML = "StudentProfilePanel.fxml";
    private static final String EMAIL_NOT_PROVIDED = "Not provided";

    private final ObservableList<SessionNote> sessionNotes = FXCollections.observableArrayList();

    @FXML
    private VBox emptyState;
    @FXML
    private VBox profileContent;
    @FXML
    private Label studentName;
    @FXML
    private Label parentGuardianPhone;
    @FXML
    private Label parentGuardianEmail;
    @FXML
    private Label subject;
    @FXML
    private Label currentLevel;
    @FXML
    private ListView<SessionNote> sessionNoteListView;
    @FXML
    private Label emptyNotesMessage;

    /**
     * Creates an empty panel that prompts the user to select a profile.
     */
    public StudentProfilePanel() {
        super(FXML);
        sessionNoteListView.setItems(sessionNotes);
        sessionNoteListView.setCellFactory(listView -> new SessionNoteListViewCell());
        clearProfile();
    }

    /**
     * Displays the contact and learning context from {@code profile}.
     */
    public void setProfile(StudentProfile profile) {
        requireNonNull(profile);
        studentName.setText(profile.getName().toString());
        parentGuardianPhone.setText(profile.getParentGuardianContact().getPhone());
        parentGuardianEmail.setText(profile.getParentGuardianContact().getEmail().orElse(EMAIL_NOT_PROVIDED));
        subject.setText(profile.getSubject().toString());
        currentLevel.setText(profile.getCurrentLevel().toString());
        sessionNotes.setAll(profile.getSessionNotes());
        updateNotesEmptyState();
        setProfileVisible(true);
    }

    /**
     * Clears the displayed profile and restores the empty-state prompt.
     */
    public void clearProfile() {
        sessionNotes.clear();
        updateNotesEmptyState();
        setProfileVisible(false);
    }

    private void updateNotesEmptyState() {
        boolean isEmpty = sessionNotes.isEmpty();
        emptyNotesMessage.setVisible(isEmpty);
        emptyNotesMessage.setManaged(isEmpty);
    }

    private void setProfileVisible(boolean isVisible) {
        profileContent.setVisible(isVisible);
        profileContent.setManaged(isVisible);
        emptyState.setVisible(!isVisible);
        emptyState.setManaged(!isVisible);
    }

    /**
     * Displays one session note as a timestamped card.
     */
    static class SessionNoteListViewCell extends ListCell<SessionNote> {
        @Override
        protected void updateItem(SessionNote sessionNote, boolean empty) {
            super.updateItem(sessionNote, empty);

            if (empty || sessionNote == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new SessionNoteCard(sessionNote).getRoot());
            }
        }
    }
}
