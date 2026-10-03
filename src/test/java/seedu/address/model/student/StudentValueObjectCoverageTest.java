package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class StudentValueObjectCoverageTest {

    @Test
    public void valueObjects_equalValues_haveConsistentEqualityAndHashCodes() {
        StudentName name = new StudentName("Ari Tan");
        ParentGuardianContact contact = new ParentGuardianContact("9123 4567", Optional.empty());
        Subject subject = new Subject("Mathematics");
        CurrentLevel level = new CurrentLevel("Secondary 3");

        assertEquals(name, new StudentName("Ari Tan"));
        assertEquals(name.hashCode(), new StudentName("Ari Tan").hashCode());
        assertEquals("Ari Tan", name.toString());
        assertFalse(name.equals(null));
        assertFalse(name.equals(subject));
        assertEquals(contact, new ParentGuardianContact("9123 4567", Optional.empty()));
        assertEquals(contact.hashCode(), new ParentGuardianContact("9123 4567", Optional.empty()).hashCode());
        assertEquals(Optional.empty(), contact.getEmail());
        assertFalse(contact.equals(null));
        assertEquals(subject, new Subject("Mathematics"));
        assertEquals(subject.hashCode(), new Subject("Mathematics").hashCode());
        assertEquals("Mathematics", subject.toString());
        assertFalse(subject.equals(null));
        assertEquals(level, new CurrentLevel("Secondary 3"));
        assertEquals(level.hashCode(), new CurrentLevel("Secondary 3").hashCode());
        assertEquals("Secondary 3", level.toString());
        assertFalse(level.equals(null));
    }

    @Test
    public void identityAndStudent_accessorsAndEquality_coverDistinctValues() {
        Student student = student("Ari Tan", "9123 4567", "Mathematics", "Secondary 3");
        Student sameStudent = student("Ari Tan", "9123 4567", "Mathematics", "Secondary 3");
        Student changedSubject = student("Ari Tan", "9123 4567", "English", "Secondary 3");
        StudentIdentity identity = new StudentIdentity(student.getName(), student.getParentGuardianContact());
        StudentIdentity matchingIdentity = new StudentIdentity(new StudentName("ari tan"),
                new ParentGuardianContact("9123-4567", Optional.empty()));

        assertEquals("Ari Tan", student.getName().getValue());
        assertEquals("9123 4567", student.getParentGuardianContact().getPhone());
        assertEquals("Mathematics", student.getSubject().getValue());
        assertEquals("Secondary 3", student.getCurrentLevel().getValue());
        assertEquals(student, sameStudent);
        assertEquals(student.hashCode(), sameStudent.hashCode());
        assertFalse(student.equals(changedSubject));
        assertFalse(student.equals(null));
        assertTrue(identity.matches(matchingIdentity));
        assertEquals(identity, matchingIdentity);
        assertEquals(identity.hashCode(), matchingIdentity.hashCode());
        assertFalse(identity.equals(null));
        assertThrows(NullPointerException.class, () -> identity.matches(null));
    }

    @Test
    public void validators_rejectNullAndInvalidWhitespaceCases() {
        assertThrows(NullPointerException.class, () -> StudentName.isValidName(null));
        assertThrows(NullPointerException.class, () -> ParentGuardianContact.isValidPhone(null));
        assertThrows(NullPointerException.class, () -> ParentGuardianContact.isValidEmail(null));
        assertThrows(NullPointerException.class, () -> Subject.isValidSubject(null));
        assertThrows(NullPointerException.class, () -> CurrentLevel.isValidCurrentLevel(null));
        assertFalse(Subject.isValidSubject("\n"));
        assertFalse(CurrentLevel.isValidCurrentLevel("\r"));
    }

    private Student student(String name, String phone, String subject, String level) {
        return new Student(new StudentName(name), new ParentGuardianContact(phone, Optional.empty()),
                new Subject(subject), new CurrentLevel(level));
    }
}
