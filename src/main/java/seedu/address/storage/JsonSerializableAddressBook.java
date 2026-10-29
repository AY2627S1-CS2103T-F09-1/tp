package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_STUDENT_ID = "Students list contains duplicate student IDs.";
    public static final String MESSAGE_DUPLICATE_STUDENT = "Students list contains duplicate student(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedStudent> students = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("students") List<JsonAdaptedStudent> students) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (students != null) {
            this.students.addAll(students);
        }
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        students.addAll(source.getStudentList().stream()
                .map(student -> new JsonAdaptedStudent(student, source.getSessionHistory(student.getId())))
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @param loadedAt The date-time the data is being loaded.
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType(OffsetDateTime loadedAt) throws IllegalValueException {
        requireNonNull(loadedAt);
        AddressBook addressBook = new AddressBook();
        addPersons(addressBook);
        addStudents(addressBook, loadedAt);
        return addressBook;
    }

    /**
     * Adds the persisted persons to {@code addressBook}.
     *
     * @throws IllegalValueException If a person is invalid or duplicated.
     */
    private void addPersons(AddressBook addressBook) throws IllegalValueException {
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
    }

    /**
     * Adds the persisted students and their session histories to {@code addressBook}.
     *
     * @throws IllegalValueException If a student is invalid, shares an ID, or shares an identity.
     */
    private void addStudents(AddressBook addressBook, OffsetDateTime loadedAt) throws IllegalValueException {
        Set<String> studentIds = new HashSet<>();
        for (JsonAdaptedStudent jsonAdaptedStudent : students) {
            Student student = jsonAdaptedStudent.toModelType();
            if (!studentIds.add(student.getId().getValue())) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_STUDENT_ID);
            }
            if (addressBook.hasStudent(student)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_STUDENT);
            }
            addressBook.addStudent(student);
            addressBook.setSessionHistory(student.getId(), jsonAdaptedStudent.toModelSessionHistory(loadedAt));
        }
    }
}
