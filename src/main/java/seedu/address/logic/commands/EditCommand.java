package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.StudentRosterEntry;
import seedu.address.model.student.Subject;

/**
 * Edits selected details of a student in the roster.
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the student identified by the index number in the student roster. "
            + "Existing values are retained for fields that are not supplied.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PARENT_PHONE] "
            + "[" + PREFIX_EMAIL + "PARENT_EMAIL] "
            + "[" + PREFIX_SUBJECT + "SUBJECT] "
            + "[" + PREFIX_LEVEL + "CURRENT_LEVEL]\n"
            + "To remove the parent or guardian email, provide " + PREFIX_EMAIL + " without a value.\n"
            + "Example: " + COMMAND_WORD + " 2 "
            + PREFIX_PHONE + "9123 4567 "
            + PREFIX_LEVEL + "Secondary 4";

    public static final String MESSAGE_SUCCESS = "Edited student: %1$s (%2$s, %3$s).";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STUDENT =
            "Duplicate student: %1$s already has this parent phone.";

    private final Index targetIndex;
    private final EditStudentDescriptor editStudentDescriptor;

    /**
     * Creates an edit command for the student at {@code targetIndex}.
     *
     * @param targetIndex The student's one-based roster index.
     * @param editStudentDescriptor The student details to replace.
     */
    public EditCommand(Index targetIndex, EditStudentDescriptor editStudentDescriptor) {
        requireAllNonNull(targetIndex, editStudentDescriptor);
        this.targetIndex = targetIndex;
        this.editStudentDescriptor = new EditStudentDescriptor(editStudentDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<StudentRosterEntry> rosterEntries = model.getStudentRoster().getEntries();
        if (targetIndex.getZeroBased() >= rosterEntries.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STUDENT_DISPLAYED_INDEX);
        }

        StudentRosterEntry targetEntry = rosterEntries.get(targetIndex.getZeroBased());
        Optional<StudentProfile> targetProfile = model.getStudentProfile(targetEntry.getStudentId());
        assert targetProfile.isPresent() : "A roster entry must have a matching student profile";
        StudentProfile profile = targetProfile.orElseThrow();
        Student originalStudent = createStudent(profile, new EditStudentDescriptor());
        Student editedStudent = createStudent(profile, editStudentDescriptor);

        if (!originalStudent.hasSameIdentity(editedStudent) && model.hasStudent(editedStudent)) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, editedStudent.getName())
                    + "\n" + MESSAGE_USAGE);
        }

        model.setStudent(targetEntry.getStudentId(), editedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedStudent.getName(), editedStudent.getSubject(),
                editedStudent.getCurrentLevel()));
    }

    /**
     * Returns a student containing the supplied edits and the profile's unchanged stable ID.
     */
    private static Student createStudent(StudentProfile profile, EditStudentDescriptor descriptor) {
        StudentName name = descriptor.getName().orElse(profile.getName());
        String phone = descriptor.getParentPhone()
                .orElse(profile.getParentGuardianContact().getPhone());
        Optional<String> email = descriptor.getParentEmail()
                .orElse(profile.getParentGuardianContact().getEmail());
        Subject subject = descriptor.getSubject().orElse(profile.getSubject());
        CurrentLevel currentLevel = descriptor.getCurrentLevel().orElse(profile.getCurrentLevel());
        ParentGuardianContact contact = new ParentGuardianContact(phone, email);
        return new Student(name, contact, subject, currentLevel, profile.getStudentId());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof EditCommand otherEditCommand
                && targetIndex.equals(otherEditCommand.targetIndex)
                && editStudentDescriptor.equals(otherEditCommand.editStudentDescriptor);
    }

    /**
     * Returns a description that omits edited student details because they may contain private information.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }

    /**
     * Stores the student details to edit, including whether the optional email should be removed.
     */
    public static final class EditStudentDescriptor {
        private StudentName name;
        private String parentPhone;
        private Optional<String> parentEmail;
        private Subject subject;
        private CurrentLevel currentLevel;

        /**
         * Creates an empty descriptor.
         */
        public EditStudentDescriptor() {
        }

        /**
         * Creates a defensive copy of {@code toCopy}.
         */
        public EditStudentDescriptor(EditStudentDescriptor toCopy) {
            requireNonNull(toCopy);
            name = toCopy.name;
            parentPhone = toCopy.parentPhone;
            parentEmail = toCopy.parentEmail;
            subject = toCopy.subject;
            currentLevel = toCopy.currentLevel;
        }

        /**
         * Returns whether at least one student field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, parentPhone, parentEmail, subject, currentLevel);
        }

        public void setName(StudentName name) {
            this.name = requireNonNull(name);
        }

        public Optional<StudentName> getName() {
            return Optional.ofNullable(name);
        }

        public void setParentPhone(String parentPhone) {
            this.parentPhone = requireNonNull(parentPhone);
        }

        public Optional<String> getParentPhone() {
            return Optional.ofNullable(parentPhone);
        }

        public void setParentEmail(Optional<String> parentEmail) {
            this.parentEmail = requireNonNull(parentEmail);
        }

        public Optional<Optional<String>> getParentEmail() {
            return Optional.ofNullable(parentEmail);
        }

        public void setSubject(Subject subject) {
            this.subject = requireNonNull(subject);
        }

        public Optional<Subject> getSubject() {
            return Optional.ofNullable(subject);
        }

        public void setCurrentLevel(CurrentLevel currentLevel) {
            this.currentLevel = requireNonNull(currentLevel);
        }

        public Optional<CurrentLevel> getCurrentLevel() {
            return Optional.ofNullable(currentLevel);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }

            return other instanceof EditStudentDescriptor otherDescriptor
                    && Objects.equals(name, otherDescriptor.name)
                    && Objects.equals(parentPhone, otherDescriptor.parentPhone)
                    && Objects.equals(parentEmail, otherDescriptor.parentEmail)
                    && Objects.equals(subject, otherDescriptor.subject)
                    && Objects.equals(currentLevel, otherDescriptor.currentLevel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parentPhone, parentEmail, subject, currentLevel);
        }
    }
}
