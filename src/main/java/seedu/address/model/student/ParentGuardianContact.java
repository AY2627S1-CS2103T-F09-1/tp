package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Optional;

/**
 * Represents the contact details of the adult responsible for a student.
 */
public final class ParentGuardianContact {

    public static final String PHONE_MESSAGE_CONSTRAINTS =
            "Parent or guardian phone numbers must contain at least three digits and may use spaces or hyphens.";
    public static final String EMAIL_MESSAGE_CONSTRAINTS =
            "Parent or guardian email addresses must contain one '@' and a non-blank local part and domain.";
    private static final String PHONE_CHARACTERS_REGEX = "\\+?[0-9][0-9 -]*";
    private static final String NORMALIZED_PHONE_REGEX = "\\+?[0-9]{3,}";
    private static final String EMAIL_REGEX = "[^@\\s]+@[^@\\s]+";

    private final String phone;
    private final String email;

    /**
     * Constructs parent or guardian contact details with an optional email address.
     *
     * @param phone A valid phone number.
     * @param email A valid email address, or an empty optional when it is unavailable.
     */
    public ParentGuardianContact(String phone, Optional<String> email) {
        requireNonNull(phone);
        requireNonNull(email);
        checkArgument(isValidPhone(phone), PHONE_MESSAGE_CONSTRAINTS);
        checkArgument(email.isEmpty() || isValidEmail(email.get()), EMAIL_MESSAGE_CONSTRAINTS);
        this.phone = phone.trim();
        this.email = email.map(String::trim).orElse(null);
    }

    /**
     * Returns whether {@code phone} is a valid parent or guardian phone number.
     */
    public static boolean isValidPhone(String phone) {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        String normalizedPhone = normalizePhone(trimmedPhone);
        return trimmedPhone.matches(PHONE_CHARACTERS_REGEX) && normalizedPhone.matches(NORMALIZED_PHONE_REGEX);
    }

    /**
     * Returns whether {@code email} is a valid parent or guardian email address.
     */
    public static boolean isValidEmail(String email) {
        requireNonNull(email);
        return email.trim().matches(EMAIL_REGEX);
    }

    /**
     * Returns the phone number without spaces or hyphens for identity comparison.
     */
    public static String normalizePhone(String phone) {
        requireNonNull(phone);
        return phone.replaceAll("[ -]", "");
    }

    /**
     * Returns the recorded phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the recorded email address when available.
     */
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Returns the normalized phone number used for student identity comparison.
     */
    public String getNormalizedPhone() {
        return normalizePhone(phone);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        return other instanceof ParentGuardianContact otherContact
                && phone.equals(otherContact.phone)
                && Optional.ofNullable(email).equals(Optional.ofNullable(otherContact.email));
    }

    @Override
    public int hashCode() {
        return 31 * phone.hashCode() + Optional.ofNullable(email).hashCode();
    }
}
