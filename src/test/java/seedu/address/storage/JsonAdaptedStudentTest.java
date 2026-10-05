package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalSessionNotes.FACTORISATION_NOTE;
import static seedu.address.testutil.TypicalSessionNotes.INDICES_NOTE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.session.SessionHistory;
import seedu.address.model.session.SessionNote;
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

    @Test
    public void toModelSessionHistory_noSessionNotes_returnsEmptyHistory() throws Exception {
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, "Test Learner", "9123 4567", null,
                "Mathematics", "Secondary 3", null);

        assertEquals(SessionHistory.empty(), student.toModelSessionHistory());
        assertEquals(SessionHistory.empty(), new JsonAdaptedStudent(VALID_STUDENT).toModelSessionHistory());
    }

    @Test
    public void toModelSessionHistory_withSessionNotes_returnsHistory() throws Exception {
        SessionHistory history = SessionHistory.empty().withNote(INDICES_NOTE).withNote(FACTORISATION_NOTE);

        JsonAdaptedStudent student = new JsonAdaptedStudent(VALID_STUDENT, history);

        assertEquals(history, student.toModelSessionHistory());
        assertEquals(VALID_STUDENT, student.toModelType());
    }

    @Test
    public void toModelSessionHistory_emptyEntry_throwsIllegalValueException() {
        JsonAdaptedSessionNote validNote = new JsonAdaptedSessionNote(INDICES_NOTE);
        List<JsonAdaptedSessionNote> sessionNotes = Arrays.asList(validNote, null);
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, "Test Learner", "9123 4567", null,
                "Mathematics", "Secondary 3", sessionNotes);

        assertThrows(IllegalValueException.class, JsonAdaptedStudent.MISSING_SESSION_NOTE_MESSAGE,
                student::toModelSessionHistory);
    }

    @Test
    public void toModelSessionHistory_invalidNote_throwsIllegalValueException() {
        List<JsonAdaptedSessionNote> sessionNotes = List.of(
                new JsonAdaptedSessionNote("2026-09-18T18:35:00+08:00", "Line one\nLine two"));
        JsonAdaptedStudent student = new JsonAdaptedStudent(null, "Test Learner", "9123 4567", null,
                "Mathematics", "Secondary 3", sessionNotes);

        assertThrows(IllegalValueException.class, SessionNote.MESSAGE_CONSTRAINTS,
                student::toModelSessionHistory);
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
