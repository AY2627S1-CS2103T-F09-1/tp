package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

public class JsonAdaptedStudentTest {

    private static final Student VALID_STUDENT = new Student(new StudentName("Test Learner"),
            new ParentGuardianContact("9123 4567", Optional.of("guardian@example.com")), new Subject("Mathematics"),
            new CurrentLevel("Secondary 3"));

    @Test
    public void toModelType_validStudent_returnsStudent() throws Exception {
        assertEquals(VALID_STUDENT, new JsonAdaptedStudent(VALID_STUDENT).toModelType());
        assertEquals(VALID_STUDENT.getId(), new JsonAdaptedStudent(VALID_STUDENT).toModelType().getId());
    }

    @Test
    public void toModelType_legacyStudent_derivesStableId() throws Exception {
        JsonAdaptedStudent firstLoad = new JsonAdaptedStudent("Test Learner", "9123 4567", null,
                "Mathematics", "Secondary 3");
        JsonAdaptedStudent secondLoad = new JsonAdaptedStudent("Test Learner", "9123 4567", null,
                "Mathematics", "Secondary 3");

        assertEquals(firstLoad.toModelType().getId(), secondLoad.toModelType().getId());
        assertNotEquals(VALID_STUDENT.getId(), firstLoad.toModelType().getId());
    }

    @Test
    public void toModelType_invalidStudentId_throwsIllegalValueException() {
        JsonAdaptedStudent student = new JsonAdaptedStudent("not-a-uuid", "Test Learner", "9123 4567", null,
                "Mathematics", "Secondary 3");

        assertThrows(IllegalValueException.class, JsonAdaptedStudent.INVALID_ID_MESSAGE, student::toModelType);
    }

    @Test
    public void toModelType_missingRequiredFields_throwsIllegalValueException() {
        assertMissingField(null, "9123 4567", "guardian@example.com", "Mathematics", "Secondary 3", "StudentName");
        assertMissingField("Test Learner", null, "guardian@example.com", "Mathematics", "Secondary 3",
                "ParentGuardianContact");
        assertMissingField("Test Learner", "9123 4567", "guardian@example.com", null, "Secondary 3", "Subject");
        assertMissingField("Test Learner", "9123 4567", "guardian@example.com", "Mathematics", null,
                "CurrentLevel");
    }

    @Test
    public void toModelType_invalidFields_throwsIllegalValueException() {
        assertInvalidField("Test Learner", "not-a-phone", "guardian@example.com", "Mathematics", "Secondary 3",
                ParentGuardianContact.PHONE_MESSAGE_CONSTRAINTS);
        assertInvalidField("Test Learner", "9123 4567", "invalid-email", "Mathematics", "Secondary 3",
                ParentGuardianContact.EMAIL_MESSAGE_CONSTRAINTS);
        assertInvalidField("Test Learner", "9123 4567", "guardian@example.com", "", "Secondary 3",
                Subject.MESSAGE_CONSTRAINTS);
        assertInvalidField("Test Learner", "9123 4567", "guardian@example.com", "Mathematics", "",
                CurrentLevel.MESSAGE_CONSTRAINTS);
    }

    private void assertMissingField(String name, String phone, String email, String subject, String currentLevel,
            String expectedFieldType) {
        JsonAdaptedStudent student = new JsonAdaptedStudent(name, phone, email, subject, currentLevel);
        String expectedMessage = String.format(JsonAdaptedStudent.MISSING_FIELD_MESSAGE_FORMAT, expectedFieldType);
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }

    private void assertInvalidField(String name, String phone, String email, String subject, String currentLevel,
            String expectedMessage) {
        JsonAdaptedStudent student = new JsonAdaptedStudent(name, phone, email, subject, currentLevel);
        assertThrows(IllegalValueException.class, expectedMessage, student::toModelType);
    }
}
