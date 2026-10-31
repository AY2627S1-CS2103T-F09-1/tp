package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Logger;

import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.model.session.SessionNote;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final Clock clock;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs, timestamping session notes with
     * the system clock.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        this(addressBook, userPrefs, Clock.systemDefaultZone());
    }

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs, timestamping session notes with
     * {@code clock}.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs, Clock clock) {
        requireAllNonNull(addressBook, userPrefs, clock);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.clock = clock;
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasStudent(Student student) {
        requireNonNull(student);
        return addressBook.hasStudent(student);
    }

    @Override
    public void addStudent(Student student) {
        addressBook.addStudent(student);
    }

    @Override
    public void setStudent(StudentId studentId, Student editedStudent) {
        requireAllNonNull(studentId, editedStudent);
        addressBook.setStudent(studentId, editedStudent);
    }

    @Override
    public int deleteStudent(StudentId studentId) {
        return addressBook.removeStudent(studentId);
    }

    /**
     * {@inheritDoc}
     * The timestamp is truncated to whole seconds.
     */
    @Override
    public SessionNote addSessionNote(StudentId studentId, String text) {
        requireAllNonNull(studentId, text);
        OffsetDateTime recordedAt = OffsetDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        SessionNote note = new SessionNote(text, recordedAt);
        addressBook.addSessionNote(studentId, note);
        assert addressBook.getSessionHistory(studentId).getNotes().contains(note)
                : "An added session note must appear in the student's history";
        return note;
    }

    @Override
    public SessionNote deleteSessionNote(StudentId studentId, Index noteNumber) {
        return addressBook.removeSessionNote(studentId, noteNumber);
    }

    @Override
    public SessionNote editSessionNote(StudentId studentId, Index noteNumber, String text) {
        requireAllNonNull(studentId, noteNumber, text);
        OffsetDateTime lastEditedAt = OffsetDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        SessionNote editedNote = addressBook.editSessionNote(studentId, noteNumber, text, lastEditedAt);
        assert addressBook.getSessionHistory(studentId).getNote(noteNumber).equals(editedNote)
                : "The edited session note must appear in the student's history";
        return editedNote;
    }

    @Override
    public int removeSessionHistory(StudentId studentId) {
        return addressBook.removeSessionHistory(studentId);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs);
    }

}
