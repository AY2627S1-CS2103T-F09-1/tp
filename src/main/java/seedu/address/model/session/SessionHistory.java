package seedu.address.model.session;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents the immutable, newest-first collection of one student's session notes.
 */
public final class SessionHistory {

    private static final SessionHistory EMPTY_HISTORY = new SessionHistory(List.of());

    /** Orders notes by the instant they were recorded, latest first, regardless of time-zone offset. */
    private static final Comparator<SessionNote> NEWEST_FIRST = Comparator
            .comparing((SessionNote note) -> note.getRecordedAt().toInstant())
            .reversed();

    private final List<SessionNote> notes;

    private SessionHistory(List<SessionNote> notes) {
        this.notes = notes;
    }

    /**
     * Returns a history with no session notes.
     */
    public static SessionHistory empty() {
        return EMPTY_HISTORY;
    }

    /**
     * Returns a new history containing this history's notes and {@code note}.
     * Notes with equal timestamps keep the order in which they were added.
     */
    public SessionHistory withNote(SessionNote note) {
        requireNonNull(note);
        List<SessionNote> updatedNotes = new ArrayList<>(notes);
        int insertionIndex = findInsertionIndex(note);
        updatedNotes.add(insertionIndex, note);
        return new SessionHistory(List.copyOf(updatedNotes));
    }

    /**
     * Returns an unmodifiable list of the notes, newest first.
     */
    public List<SessionNote> getNotes() {
        return notes;
    }

    /**
     * Returns the number of notes in this history.
     */
    public int size() {
        return notes.size();
    }

    /**
     * Returns whether this history has no notes.
     */
    public boolean isEmpty() {
        return notes.isEmpty();
    }

    /**
     * Returns the index before the first note recorded earlier than {@code note},
     * so that the new note follows any existing notes with an equal timestamp.
     */
    private int findInsertionIndex(SessionNote note) {
        for (int i = 0; i < notes.size(); i++) {
            if (NEWEST_FIRST.compare(note, notes.get(i)) < 0) {
                return i;
            }
        }
        return notes.size();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof SessionHistory otherHistory && notes.equals(otherHistory.notes);
    }

    @Override
    public int hashCode() {
        return notes.hashCode();
    }
}
