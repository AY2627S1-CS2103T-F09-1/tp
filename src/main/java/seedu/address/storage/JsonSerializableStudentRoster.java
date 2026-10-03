package seedu.address.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Student;

/**
 * Immutable Jackson-friendly student collection that validates duplicate identities while loading.
 */
@JsonRootName(value = "studentRoster")
class JsonSerializableStudentRoster {

    static final String MESSAGE_DUPLICATE_STUDENT = "Students list contains duplicate student(s).";

    private final List<JsonAdaptedStudent> students = new ArrayList<>();

    /**
     * Constructs a Jackson-friendly roster from persisted students.
     */
    @JsonCreator
    JsonSerializableStudentRoster(@JsonProperty("students") List<JsonAdaptedStudent> students) {
        if (students != null) {
            this.students.addAll(students);
        }
    }

    /**
     * Constructs a Jackson-friendly roster from domain students.
     */
    JsonSerializableStudentRoster(Collection<Student> students) {
        this.students.addAll(students.stream().map(JsonAdaptedStudent::new).toList());
    }

    /**
     * Returns the validated students represented by this JSON roster.
     *
     * @throws IllegalValueException if a field is invalid or two students share an identity.
     */
    List<Student> toModelType() throws IllegalValueException {
        List<Student> modelStudents = new ArrayList<>();
        for (JsonAdaptedStudent jsonStudent : students) {
            Student student = jsonStudent.toModelType();
            if (modelStudents.stream().anyMatch(student::hasSameIdentity)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_STUDENT);
            }
            modelStudents.add(student);
        }
        return List.copyOf(modelStudents);
    }
}
