package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.student.StudentId;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** Stable ID of the student profile that should be displayed, if any. */
    private final StudentId profileStudentId;

    /** Displayed note number to mark during a deletion preview, if any. */
    private final Index previewNoteNumber;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, null);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, StudentId profileStudentId,
            Index previewNoteNumber) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.profileStudentId = profileStudentId;
        this.previewNoteNumber = previewNoteNumber;
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Constructs a {@code CommandResult} that requests display of one student profile.
     */
    public CommandResult(String feedbackToUser, StudentId profileStudentId) {
        this(feedbackToUser, false, false, requireNonNull(profileStudentId), null);
    }

    /**
     * Constructs a preview result that opens a student's profile and marks one displayed session note.
     */
    public CommandResult(String feedbackToUser, StudentId profileStudentId, Index previewNoteNumber) {
        this(feedbackToUser, false, false, requireNonNull(profileStudentId), requireNonNull(previewNoteNumber));
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    /**
     * Returns the stable ID of the student profile to display, if this result requests one.
     */
    public Optional<StudentId> getProfileStudentId() {
        return Optional.ofNullable(profileStudentId);
    }

    /**
     * Returns the displayed session-note number to mark during a deletion preview, if any.
     */
    public Optional<Index> getPreviewNoteNumber() {
        return Optional.ofNullable(previewNoteNumber);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && Objects.equals(profileStudentId, otherCommandResult.profileStudentId)
                && Objects.equals(previewNoteNumber, otherCommandResult.previewNoteNumber);
    }

    @Override
    public int hashCode() {
        Integer previewNoteNumberValue = previewNoteNumber == null ? null : previewNoteNumber.getOneBased();
        return Objects.hash(feedbackToUser, showHelp, exit, profileStudentId, previewNoteNumberValue);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("profileStudentId", profileStudentId)
                .add("previewNoteNumber", previewNoteNumber)
                .toString();
    }

}
