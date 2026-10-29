package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalSessionNotes.ALEX;
import static seedu.address.testutil.TypicalSessionNotes.BEA;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditStudentDescriptor;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.profile.StudentProfile;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

public class EditCommandTest {

    @Test
    public void execute_allFieldsSpecified_updatesStudentAndPreservesHistory() throws Exception {
        Model model = modelWith(ALEX);
        model.addSessionNote(ALEX.getId(), "Reviewed indices.");
        EditStudentDescriptor descriptor = descriptor("Alexis Tan", "8000 0001",
                Optional.of("guardian@example.com"), "Physics", "Secondary 4");

        CommandResult result = new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        StudentProfile profile = model.getStudentProfile(ALEX.getId()).orElseThrow();
        assertEquals("Edited student: Alexis Tan (Physics, Secondary 4).", result.getFeedbackToUser());
        assertEquals(ALEX.getId(), profile.getStudentId());
        assertEquals(new StudentName("Alexis Tan"), profile.getName());
        assertEquals(new ParentGuardianContact("8000 0001", Optional.of("guardian@example.com")),
                profile.getParentGuardianContact());
        assertEquals(new Subject("Physics"), profile.getSubject());
        assertEquals(new CurrentLevel("Secondary 4"), profile.getCurrentLevel());
        assertEquals(1, profile.getSessionNotes().size());
    }

    @Test
    public void execute_someFieldsSpecified_retainsUnspecifiedFields() throws Exception {
        Model model = modelWith(ALEX);
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setSubject(new Subject("Physics"));

        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        StudentProfile profile = model.getStudentProfile(ALEX.getId()).orElseThrow();
        assertEquals(ALEX.getName(), profile.getName());
        assertEquals(ALEX.getParentGuardianContact(), profile.getParentGuardianContact());
        assertEquals(new Subject("Physics"), profile.getSubject());
        assertEquals(ALEX.getCurrentLevel(), profile.getCurrentLevel());
    }

    @Test
    public void execute_emptyEmail_removesExistingEmail() throws Exception {
        Student studentWithEmail = new Student(ALEX.getName(),
                new ParentGuardianContact("9123 4567", Optional.of("guardian@example.com")), ALEX.getSubject(),
                ALEX.getCurrentLevel(), ALEX.getId());
        Model model = modelWith(studentWithEmail);
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setParentEmail(Optional.empty());

        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(model);

        assertTrue(model.getStudentProfile(ALEX.getId()).orElseThrow()
                .getParentGuardianContact().getEmail().isEmpty());
    }

    @Test
    public void execute_nameChangesRosterOrder_preservesStudentId() throws Exception {
        Model model = modelWith(ALEX, BEA);
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setName(new StudentName("Aaron Tan"));

        new EditCommand(INDEX_SECOND_PERSON, descriptor).execute(model);

        assertEquals(List.of(BEA.getId(), ALEX.getId()), model.getStudentRoster().getEntries().stream()
                .map(entry -> entry.getStudentId()).toList());
        assertEquals(new StudentName("Aaron Tan"), model.getStudentProfile(BEA.getId()).orElseThrow().getName());
    }

    @Test
    public void execute_duplicateIdentity_throwsCommandExceptionAndLeavesStudentsUnchanged() {
        Model model = modelWith(ALEX, BEA);
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setName(BEA.getName());
        descriptor.setParentPhone(BEA.getParentGuardianContact().getPhone());
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_DUPLICATE_STUDENT, BEA.getName());
        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
        assertEquals(ALEX.getName(), model.getStudentProfile(ALEX.getId()).orElseThrow().getName());
    }

    @Test
    public void execute_invalidIndex_throwsCommandException() {
        Model model = modelWith(ALEX);
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setSubject(new Subject("Physics"));
        EditCommand command = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        String expectedMessage = String.format(Messages.MESSAGE_STUDENT_INDEX_OUT_OF_RANGE, 2, 1);
        assertThrows(CommandException.class, expectedMessage, () -> command.execute(model));
    }

    @Test
    public void constructor_nullArguments_throwNullPointerException() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setSubject(new Subject("Physics"));
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        assertThrows(NullPointerException.class, () -> new EditCommand(null, new EditStudentDescriptor()));
        assertThrows(NullPointerException.class, () -> new EditCommand(INDEX_FIRST_PERSON, null));
        assertThrows(NullPointerException.class, () -> new EditStudentDescriptor(null));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void descriptor_accessorsCopyAndEquality_behaveAsValueObject() {
        EditStudentDescriptor descriptor = descriptor("Alexis Tan", "8000 0001",
                Optional.of("guardian@example.com"), "Physics", "Secondary 4");
        EditStudentDescriptor copy = new EditStudentDescriptor(descriptor);

        assertTrue(descriptor.isAnyFieldEdited());
        assertEquals(Optional.of(new StudentName("Alexis Tan")), copy.getName());
        assertEquals(Optional.of("8000 0001"), copy.getParentPhone());
        assertTrue(copy.isParentEmailEdited());
        assertEquals(Optional.of("guardian@example.com"), copy.getParentEmail());
        assertEquals(Optional.of(new Subject("Physics")), copy.getSubject());
        assertEquals(Optional.of(new CurrentLevel("Secondary 4")), copy.getCurrentLevel());
        assertEquals(descriptor, copy);
        assertEquals(descriptor.hashCode(), copy.hashCode());
        assertEquals(descriptor, descriptor);
        assertFalse(descriptor.equals(null));
        assertFalse(descriptor.equals("not a descriptor"));

        EditStudentDescriptor differentPhone = new EditStudentDescriptor(copy);
        differentPhone.setParentPhone("8000 0002");
        assertNotEquals(differentPhone, copy);
        EditStudentDescriptor clearedEmail = new EditStudentDescriptor(copy);
        clearedEmail.setParentEmail(Optional.empty());
        assertNotEquals(clearedEmail, copy);
        EditStudentDescriptor differentLevel = new EditStudentDescriptor(copy);
        differentLevel.setCurrentLevel(new CurrentLevel("Secondary 5"));
        assertNotEquals(differentLevel, copy);

        descriptor.setSubject(new Subject("Chemistry"));
        assertNotEquals(descriptor, copy);
        assertThrows(NullPointerException.class, () -> descriptor.setName(null));
        assertThrows(NullPointerException.class, () -> descriptor.setParentPhone(null));
        assertThrows(NullPointerException.class, () -> descriptor.setParentEmail(null));
        assertThrows(NullPointerException.class, () -> descriptor.setSubject(null));
        assertThrows(NullPointerException.class, () -> descriptor.setCurrentLevel(null));
        assertFalse(new EditStudentDescriptor().isAnyFieldEdited());
        assertFalse(new EditStudentDescriptor().isParentEmailEdited());

        EditStudentDescriptor explicitEmailRemoval = new EditStudentDescriptor();
        explicitEmailRemoval.setParentEmail(Optional.empty());
        assertTrue(explicitEmailRemoval.isAnyFieldEdited());
        assertNotEquals(explicitEmailRemoval, new EditStudentDescriptor());
    }

    @Test
    public void equalsAndToString_useIndexAndDescriptorWithoutPrivateDetails() {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setName(new StudentName("Alexis Tan"));
        EditCommand command = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        EditCommand sameCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);
        EditCommand differentIndex = new EditCommand(INDEX_SECOND_PERSON, descriptor);
        EditStudentDescriptor differentDescriptor = new EditStudentDescriptor();
        differentDescriptor.setSubject(new Subject("Physics"));

        assertEquals(command, command);
        assertEquals(command, sameCommand);
        assertNotEquals(command, differentIndex);
        assertNotEquals(command, new EditCommand(INDEX_FIRST_PERSON, differentDescriptor));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new HelpCommand()));
        assertEquals(EditCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}",
                command.toString());
        assertFalse(command.toString().contains("Alexis"));
    }

    private static Model modelWith(Student... students) {
        Model model = new ModelManager();
        for (Student student : students) {
            model.addStudent(student);
        }
        return model;
    }

    private static EditStudentDescriptor descriptor(String name, String phone, Optional<String> email,
            String subject, String currentLevel) {
        EditStudentDescriptor descriptor = new EditStudentDescriptor();
        descriptor.setName(new StudentName(name));
        descriptor.setParentPhone(phone);
        descriptor.setParentEmail(email);
        descriptor.setSubject(new Subject(subject));
        descriptor.setCurrentLevel(new CurrentLevel(currentLevel));
        return descriptor;
    }
}
