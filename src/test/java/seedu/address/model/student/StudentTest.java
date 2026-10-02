package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class StudentTest {

    private static final ParentGuardianContact FIRST_CONTACT =
            new ParentGuardianContact("9123 4567", Optional.empty());

    @Test
    public void hasSameIdentity_sameNormalizedNameAndPhone_returnsTrue() {
        Student firstStudent = student("  ALEX   TAN ", FIRST_CONTACT, "Mathematics", "Secondary 3");
        Student duplicateStudent = student("alex tan", new ParentGuardianContact("9123-4567", Optional.of("a@b.com")),
                "English", "JC 1");

        assertTrue(firstStudent.hasSameIdentity(duplicateStudent));
    }

    @Test
    public void hasSameIdentity_differentNameOrPhone_returnsFalse() {
        Student student = student("Alex Tan", FIRST_CONTACT, "Mathematics", "Secondary 3");
        Student sibling = student("Bea Tan", FIRST_CONTACT, "Mathematics", "Secondary 3");
        Student sameNameDifferentContact = student("Alex Tan", new ParentGuardianContact("9876 5432", Optional.empty()),
                "Mathematics", "Secondary 3");

        assertFalse(student.hasSameIdentity(sibling));
        assertFalse(student.hasSameIdentity(sameNameDifferentContact));
        assertFalse(student.hasSameIdentity(null));
    }

    private Student student(String name, ParentGuardianContact contact, String subject, String currentLevel) {
        return new Student(new StudentName(name), contact, new Subject(subject), new CurrentLevel(currentLevel));
    }
}
