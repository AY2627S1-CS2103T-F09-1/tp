package seedu.address.model.session;

import static java.util.Objects.requireNonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import seedu.address.commons.core.index.Index;

/**
 * Represents the immutable, newest-first collection of one student's session notes.
 */
public final class SessionHistory {

    /** Orders notes by the instant they were recorded, latest first, regardless of time-zone offset. */
    private static final Comparator<SessionNote> NEWEST_FIRST = Comparator
            .comparing((SessionNote note) -> note.getRecordedAt().toInstant())
            .reversed();

    private static final SessionHistory EMPTY_HISTORY = new SessionHistory(List.of());

    private final List<SessionNote> notes;

    private SessionHistory(List<SessionNote> notes) {
        assert isNewestFirst(notes) : "Session notes must be ordered newest first";
        this.notes = notes;
    }

    /**
     * Returns a history with no session notes.
     */
    public static SessionHistory empty() {
        return EMPTY_HISTORY;
    }

    /**
     * Returns a history containing {@code notes}, ordered newest first.
     * Notes with equal timestamps keep their relative order in {@code notes}, so a history saved newest first
     * is restored exactly.
     */
    public static SessionHistory of(List<SessionNote> notes) {
        requireNonNull(notes);
        List<SessionNote> sortedNotes = new ArrayList<>(notes);
        // List#sort is stable, which keeps notes with equal timestamps in their given order.
        sortedNotes.sort(NEWEST_FIRST);
        return sortedNotes.isEmpty() ? empty() : new SessionHistory(List.copyOf(sortedNotes));
    }

    /**
     * Returns a new history containing this history's notes and {@code note}.
     * Among notes with equal timestamps, {@code note} is treated as the newest, so notes are numbered in the
     * order they were added.
     */
    public SessionHistory withNote(SessionNote note) {
        requireNonNull(note);
        List<SessionNote> updatedNotes = new ArrayList<>(notes);
        int insertionIndex = findInsertionIndex(note);
        updatedNotes.add(insertionIndex, note);
        return new SessionHistory(List.copyOf(updatedNotes));
    }

    /**
     * Returns the note with the given chronological {@code noteNumber}, where one identifies the oldest note.
     *
     * @throws IndexOutOfBoundsException If {@code noteNumber} exceeds this history's size.
     */
    public SessionNote getNote(Index noteNumber) {
        requireNonNull(noteNumber);
        return notes.get(toNewestFirstIndex(noteNumber));
    }

    /**
     * Returns a new history without the note with the given chronological {@code noteNumber}, where one identifies
     * the oldest note.
     *
     * @throws IndexOutOfBoundsException If {@code noteNumber} exceeds this history's size.
     */
    public SessionHistory withoutNote(Index noteNumber) {
        requireNonNull(noteNumber);
        List<SessionNote> updatedNotes = new ArrayList<>(notes);
        updatedNotes.remove(toNewestFirstIndex(noteNumber));
        return updatedNotes.isEmpty() ? empty() : new SessionHistory(List.copyOf(updatedNotes));
    }

    /**
     * Returns a new history with the selected note's text replaced while preserving its recorded date and time.
     *
     * @throws IndexOutOfBoundsException If {@code noteNumber} exceeds this history's size.
     * @throws IllegalArgumentException If {@code text} is invalid.
     */
    public SessionHistory withEditedNote(Index noteNumber, String text, OffsetDateTime lastEditedAt) {
        requireNonNull(noteNumber);
        requireNonNull(text);
        requireNonNull(lastEditedAt);
        int noteIndex = toNewestFirstIndex(noteNumber);
        SessionNote editedNote = new SessionNote(text, notes.get(noteIndex).getRecordedAt(), lastEditedAt);
        List<SessionNote> updatedNotes = new ArrayList<>(notes);
        updatedNotes.set(noteIndex, editedNote);
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

    private int toNewestFirstIndex(Index noteNumber) {
        int newestFirstIndex = notes.size() - noteNumber.getOneBased();
        if (newestFirstIndex < 0 || newestFirstIndex >= notes.size()) {
            throw new IndexOutOfBoundsException("Session note number exceeds history size");
        }
        return newestFirstIndex;
    }

    /**
     * Returns the index of the first note recorded no later than {@code note},
     * so that the new note precedes any existing notes with an equal timestamp.
     */
    private int findInsertionIndex(SessionNote note) {
        for (int i = 0; i < notes.size(); i++) {
            if (NEWEST_FIRST.compare(note, notes.get(i)) <= 0) {
                return i;
            }
        }
        return notes.size();
    }

    /**
     * Returns whether {@code notes} are ordered newest first, as every history must be.
     */
    private static boolean isNewestFirst(List<SessionNote> notes) {
        for (int i = 1; i < notes.size(); i++) {
            if (NEWEST_FIRST.compare(notes.get(i - 1), notes.get(i)) > 0) {
                return false;
            }
        }
        return true;
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
