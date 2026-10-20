package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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

    private final ObservableList<NumberedSessionNote> sessionNotes = FXCollections.observableArrayList();

    @FXML
    private VBox emptyState;
    @FXML
    private VBox profileContent;
    @FXML
    private SelectableText studentName;
    @FXML
    private SelectableText parentGuardianPhone;
    @FXML
    private SelectableText parentGuardianEmail;
    @FXML
    private SelectableText subject;
    @FXML
    private SelectableText currentLevel;
    @FXML
    private ListView<NumberedSessionNote> sessionNoteListView;
    @FXML
    private VBox emptyNotesState;

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
        sessionNotes.setAll(numberSessionNotes(profile.getSessionNotes()));
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
        emptyNotesState.setVisible(isEmpty);
        emptyNotesState.setManaged(isEmpty);
    }

    private void setProfileVisible(boolean isVisible) {
        profileContent.setVisible(isVisible);
        profileContent.setManaged(isVisible);
        emptyState.setVisible(!isVisible);
        emptyState.setManaged(!isVisible);
    }

    private List<NumberedSessionNote> numberSessionNotes(List<SessionNote> notes) {
        List<NumberedSessionNote> numberedNotes = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            numberedNotes.add(new NumberedSessionNote(notes.get(i), notes.size() - i));
        }
        return numberedNotes;
    }

    /**
     * Displays one session note as a timestamped card.
     */
    static class SessionNoteListViewCell extends ListCell<NumberedSessionNote> {
        @Override
        protected void updateItem(NumberedSessionNote numberedNote, boolean empty) {
            super.updateItem(numberedNote, empty);

            if (empty || numberedNote == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new SessionNoteCard(numberedNote.sessionNote(), numberedNote.noteNumber()).getRoot());
            }
        }
    }

    /** A session note paired with its chronological display number. */
    static record NumberedSessionNote(SessionNote sessionNote, int noteNumber) {
    }
}
