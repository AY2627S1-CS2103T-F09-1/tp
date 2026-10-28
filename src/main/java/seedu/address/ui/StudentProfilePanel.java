package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.session.SessionNote;

/**
 * Displays the contact, learning context, and chronological notes for one student profile.
 */
public class StudentProfilePanel extends UiPart<Region> {

    private static final String FXML = "StudentProfilePanel.fxml";
    private static final String EMAIL_NOT_PROVIDED = "Not provided";
    private static final double VISIBLE_EDGE_TOLERANCE_PIXELS = 1;

    private final ObservableList<NumberedSessionNote> sessionNotes = FXCollections.observableArrayList();

    @FXML
    private VBox emptyState;
    @FXML
    private ScrollPane profileScrollPane;
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
        studentName.setText(profile.getName().toString());
        parentGuardianPhone.setText(profile.getParentGuardianContact().getPhone());
        parentGuardianEmail.setText(profile.getParentGuardianContact().getEmail().orElse(EMAIL_NOT_PROVIDED));
        subject.setText(profile.getSubject().toString());
        currentLevel.setText(profile.getCurrentLevel().toString());
        sessionNotes.setAll(numberSessionNotes(profile.getSessionNotes()));
        updateNotesEmptyState();
        setProfileVisible(true);
        profileScrollPane.setVvalue(profileScrollPane.getVmin());
    }

    /**
     * Clears the displayed profile and restores the empty-state prompt.
     */
    public void clearProfile() {
        sessionNotes.clear();
        updateNotesEmptyState();
        setProfileVisible(false);
    }

    /**
     * Scrolls the profile by {@code pageCount} visible pages; a negative count scrolls upward.
     * Paging down first moves the outer profile pane until its end is visible, then pages the session-note
     * history; paging up first pages the history back to its top, then moves the outer pane.
     * Does nothing when the profile has not been laid out yet.
     */
    public void scrollSessionNotes(int pageCount) {
        if (!(sessionNoteListView.lookup(".virtual-flow") instanceof VirtualFlow<?> virtualFlow)) {
            return;
        }
        boolean shouldPageNotes = pageCount > 0 ? isSessionNoteListBottomVisible() : virtualFlow.getPosition() > 0;
        if (shouldPageNotes) {
            virtualFlow.scrollPixels(pageCount * virtualFlow.getHeight());
        } else {
            scrollProfilePane(pageCount);
        }
    }

    private double getScrollableProfilePixels() {
        return profileScrollPane.getContent().getLayoutBounds().getHeight()
                - profileScrollPane.getViewportBounds().getHeight();
    }

    /**
     * Returns true if the profile pane needs no further scrolling for the end of the session-note list to be
     * visible. Compares edges instead of scroll values because the pane keeps its pixel offset, not its
     * {@code vvalue}, when the list re-estimates its cell heights while scrolling.
     */
    private boolean isSessionNoteListBottomVisible() {
        double scrollablePixels = getScrollableProfilePixels();
        if (scrollablePixels <= 0) {
            return true;
        }
        double valueRange = profileScrollPane.getVmax() - profileScrollPane.getVmin();
        double scrolledFraction = (profileScrollPane.getVvalue() - profileScrollPane.getVmin()) / valueRange;
        double visibleBottom = scrolledFraction * scrollablePixels + profileScrollPane.getViewportBounds().getHeight();
        Node content = profileScrollPane.getContent();
        double listBottom = content.sceneToLocal(
                sessionNoteListView.localToScene(sessionNoteListView.getLayoutBounds())).getMaxY();
        return listBottom <= visibleBottom + VISIBLE_EDGE_TOLERANCE_PIXELS;
    }

    private void scrollProfilePane(int pageCount) {
        double scrollablePixels = getScrollableProfilePixels();
        if (scrollablePixels <= 0) {
            return;
        }
        double valueRange = profileScrollPane.getVmax() - profileScrollPane.getVmin();
        double pageFraction = profileScrollPane.getViewportBounds().getHeight() / scrollablePixels;
        double newValue = profileScrollPane.getVvalue() + pageCount * pageFraction * valueRange;
        profileScrollPane.setVvalue(Math.max(profileScrollPane.getVmin(),
                Math.min(profileScrollPane.getVmax(), newValue)));
    }

    private void updateNotesEmptyState() {
        boolean isEmpty = sessionNotes.isEmpty();
        emptyNotesState.setVisible(isEmpty);
        emptyNotesState.setManaged(isEmpty);
    }

    private void setProfileVisible(boolean isVisible) {
        profileScrollPane.setVisible(isVisible);
        profileScrollPane.setManaged(isVisible);
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
        private Region sessionNoteCard;

        @Override
        protected void updateItem(NumberedSessionNote numberedNote, boolean empty) {
            super.updateItem(numberedNote, empty);
            clearSessionNoteCard();

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
