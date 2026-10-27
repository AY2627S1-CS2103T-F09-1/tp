package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import seedu.address.commons.core.index.Index;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.session.SessionNote;

/**
 * Displays the contact, learning context, and chronological notes for one student profile.
 */
public class StudentProfilePanel extends UiPart<Region> {

    private static final String FXML = "StudentProfilePanel.fxml";
    private static final String EMAIL_NOT_PROVIDED = "Not provided";
    private static final PseudoClass DELETION_TARGET = PseudoClass.getPseudoClass("deletion-target");

    private final ObservableList<NumberedSessionNote> sessionNotes = FXCollections.observableArrayList();
    private Integer previewNoteNumber;

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
        clipContentToPanel();
        sessionNoteListView.setItems(sessionNotes);
        sessionNoteListView.setCellFactory(listView -> new SessionNoteListViewCell());
        clearProfile();
    }

    private void clipContentToPanel() {
        Rectangle panelClip = new Rectangle();
        panelClip.widthProperty().bind(getRoot().widthProperty());
        panelClip.heightProperty().bind(getRoot().heightProperty());
        getRoot().setClip(panelClip);
    }

    /**
     * Displays the contact and learning context from {@code profile}.
     */
    public void setProfile(StudentProfile profile) {
        requireNonNull(profile);
        previewNoteNumber = null;
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
        previewNoteNumber = null;
        sessionNotes.clear();
        updateNotesEmptyState();
        setProfileVisible(false);
    }

    private void updateNotesEmptyState() {
        boolean isEmpty = sessionNotes.isEmpty();
        emptyNotesState.setVisible(isEmpty);
        emptyNotesState.setManaged(isEmpty);
    }

    /**
     * Marks and scrolls to the displayed session note selected for a deletion preview.
     */
    public void previewDeletionOf(Index noteNumber) {
        requireNonNull(noteNumber);
        int listIndex = sessionNotes.size() - noteNumber.getOneBased();
        if (listIndex < 0 || listIndex >= sessionNotes.size()) {
            clearDeletionPreview();
            return;
        }

        previewNoteNumber = noteNumber.getOneBased();
        sessionNoteListView.refresh();
        sessionNoteListView.scrollTo(listIndex);
    }

    /**
     * Clears any temporary session-note deletion marker without changing the displayed profile.
     */
    public void clearDeletionPreview() {
        if (previewNoteNumber != null) {
            previewNoteNumber = null;
            sessionNoteListView.refresh();
        }
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
    class SessionNoteListViewCell extends ListCell<NumberedSessionNote> {
        private Region sessionNoteCard;

        @Override
        protected void updateItem(NumberedSessionNote numberedNote, boolean empty) {
            super.updateItem(numberedNote, empty);
            clearSessionNoteCard();
            boolean isDeletionTarget = !empty && numberedNote != null
                    && Integer.valueOf(numberedNote.noteNumber()).equals(previewNoteNumber);
            pseudoClassStateChanged(DELETION_TARGET, isDeletionTarget);

            if (empty || numberedNote == null) {
                setGraphic(null);
                setText(null);
            } else {
                sessionNoteCard = new SessionNoteCard(
                        numberedNote.sessionNote(), numberedNote.noteNumber()).getRoot();
                sessionNoteCard.maxWidthProperty().bind(widthProperty());
                setGraphic(sessionNoteCard);
            }
        }

        private void clearSessionNoteCard() {
            if (sessionNoteCard == null) {
                return;
            }
            sessionNoteCard.maxWidthProperty().unbind();
            sessionNoteCard = null;
        }
    }

    /** A session note paired with its chronological display number. */
    static record NumberedSessionNote(SessionNote sessionNote, int noteNumber) {
    }
}
