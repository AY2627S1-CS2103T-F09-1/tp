package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.CurrentLevel;
import seedu.address.model.student.ParentGuardianContact;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Subject;

public class JsonSerializableStudentRosterTest {

    @Test
    public void toModelType_validStudents_returnsStudents() throws Exception {
        Student firstStudent = student("Ari Tan", "9123 4567");
        Student secondStudent = student("Bea Tan", "9123 4567");

        JsonSerializableStudentRoster roster = new JsonSerializableStudentRoster(List.of(firstStudent, secondStudent));

        assertEquals(List.of(firstStudent, secondStudent), roster.toModelType());
    }

    @Test
    public void toModelType_duplicateNormalizedIdentity_throwsIllegalValueException() {
        JsonAdaptedStudent firstStudent = new JsonAdaptedStudent("Ari Tan", "9123 4567", null,
                "Mathematics", "Secondary 3");
        JsonAdaptedStudent duplicateStudent = new JsonAdaptedStudent(" ari   tan ", "9123-4567", "other@example.com",
                "English", "JC 1");
        JsonSerializableStudentRoster roster = new JsonSerializableStudentRoster(
                List.of(firstStudent, duplicateStudent));

        assertThrows(IllegalValueException.class, JsonSerializableStudentRoster.MESSAGE_DUPLICATE_STUDENT,
                roster::toModelType);
    }

    private Student student(String name, String phone) {
        return new Student(new StudentName(name), new ParentGuardianContact(phone, Optional.empty()),
                new Subject("Mathematics"), new CurrentLevel("Secondary 3"));
    }
}
