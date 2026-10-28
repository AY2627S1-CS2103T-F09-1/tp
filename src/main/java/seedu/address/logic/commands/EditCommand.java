package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LEVEL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SUBJECT;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.CollectionUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
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
    public static final String COMMAND_FORMAT = COMMAND_WORD + " INDEX "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PARENT_PHONE] "
            + "[" + PREFIX_EMAIL + "PARENT_EMAIL] "
            + "[" + PREFIX_SUBJECT + "SUBJECT] "
            + "[" + PREFIX_LEVEL + "CURRENT_LEVEL]";
    public static final String COMMAND_EXAMPLE = COMMAND_WORD + " 2 "
            + PREFIX_PHONE + "9123 4567 " + PREFIX_LEVEL + "Secondary 4";

    public static final String MESSAGE_SUCCESS = "Edited student: %1$s (%2$s, %3$s).";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STUDENT = AddCommand.MESSAGE_DUPLICATE_STUDENT;

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
        StudentRosterEntry targetEntry = getRosterEntry(model, targetIndex);
        Student originalStudent = model.getStudent(targetEntry.getStudentId()).orElseThrow();
        Student editedStudent = createStudent(originalStudent, editStudentDescriptor);

        Optional<Student> conflictingStudent = model.findConflictingStudent(targetEntry.getStudentId(), editedStudent);
        if (conflictingStudent.isPresent()) {
            throw new CommandException(String.format(MESSAGE_DUPLICATE_STUDENT, conflictingStudent.get().getName()));
        }

        model.setStudent(targetEntry.getStudentId(), editedStudent);
        return new CommandResult(String.format(MESSAGE_SUCCESS, editedStudent.getName(), editedStudent.getSubject(),
                editedStudent.getCurrentLevel()));
    }

    /**
     * Returns a student containing the supplied edits and the original student's unchanged stable ID.
     */
    private static Student createStudent(Student originalStudent, EditStudentDescriptor descriptor) {
        StudentName name = descriptor.getName().orElse(originalStudent.getName());
        String phone = descriptor.getParentPhone()
                .orElse(originalStudent.getParentGuardianContact().getPhone());
        Optional<String> email = descriptor.isParentEmailEdited()
                ? descriptor.getParentEmail()
                : originalStudent.getParentGuardianContact().getEmail();
        Subject subject = descriptor.getSubject().orElse(originalStudent.getSubject());
        CurrentLevel currentLevel = descriptor.getCurrentLevel().orElse(originalStudent.getCurrentLevel());
        ParentGuardianContact contact = new ParentGuardianContact(phone, email);
        return new Student(name, contact, subject, currentLevel, originalStudent.getId());
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
        private boolean isParentEmailEdited;
        private Optional<String> parentEmail = Optional.empty();
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
            isParentEmailEdited = toCopy.isParentEmailEdited;
            parentEmail = toCopy.parentEmail;
            subject = toCopy.subject;
            currentLevel = toCopy.currentLevel;
        }

        /**
         * Returns whether at least one student field is edited.
         */
        public boolean isAnyFieldEdited() {
            return isParentEmailEdited || CollectionUtil.isAnyNonNull(name, parentPhone, subject, currentLevel);
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
            isParentEmailEdited = true;
        }

        public boolean isParentEmailEdited() {
            return isParentEmailEdited;
        }

        public Optional<String> getParentEmail() {
            return parentEmail;
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
                    && isParentEmailEdited == otherDescriptor.isParentEmailEdited
                    && Objects.equals(parentEmail, otherDescriptor.parentEmail)
                    && Objects.equals(subject, otherDescriptor.subject)
                    && Objects.equals(currentLevel, otherDescriptor.currentLevel);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, parentPhone, isParentEmailEdited, parentEmail, subject, currentLevel);
        }
    }
}
