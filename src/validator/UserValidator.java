package validator;

import entity.User;
import exception.ValidatorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class UserValidator extends AbstractValidator<UUID, User> {
    /**
     * Validates the first name of a user.
     * The first name should start with a capital letter, have at least to letters, and contain only lower case letters
     * for all the letters except the first.
     *
     * @param firstName The first name.
     * @return String of errors.
     */
    private String validateFirstNameSlow(String firstName) {
        ArrayList<String> errors = new ArrayList<>();
        // checking if the first name contains only letters and the first letter is uppercase.
        if (firstName == null) {
            errors.add("First name cannot be null.");
        } else if (firstName.isEmpty()) {
            errors.add("First name cannot be empty.");
        } else if (firstName.length() < 3) {
            errors.add("First name should at least contain 2 characters.");
        } else if (!Character.isUpperCase(firstName.charAt(0))) {
            errors.add("First name needs to start with uppercase letter.");
        } else {
            String restOfFirstName = firstName.substring(1);
            for (Character character : restOfFirstName.toCharArray()) {
                if (character.compareTo('a') < 0 || character.compareTo('z') > 0) {
                    errors.add("First name should contain only letters.");
                    break;
                }
            }
        }
        return errors
                .stream()
                .reduce("", (str1, str2) -> str1.concat(" ").concat(str2));
    }

    /**
     * Validates the first name quick.
     *
     * @param firstName The first name.
     * @return String of errors.
     */
    private String validateFirstNameQuick(String firstName) {
        if (firstName == null) {
            return "First name cannot be null.";
        } else if (firstName.isEmpty()) {
            return "First name cannot be empty.";
        }
        return "";
    }

    /**
     * Validates the last name of a user.
     * The last name should start with a capital letter, have at least to letters, and contain only lower case letters
     * for all the letters except the first.
     *
     * @param lastName The last name.
     * @return String of errors.
     */
    private String validateLastNameSlow(String lastName) {
        ArrayList<String> errors = new ArrayList<>();

        // checking if the last name starts with an uppercase letter and it contains only letters

        if (lastName == null) {
            errors.add("Last name cannot be null.");
        } else if (lastName.isEmpty()) {
            errors.add("Last name cannot be empty.");
        } else if (lastName.length() < 3) {
            errors.add("Last name should at least contain 2 characters.");
        } else if (!Character.isUpperCase(lastName.charAt(0))) {
            errors.add("Last name needs to start with uppercase letter.");
        } else {
            String restOfLastName = lastName.substring(1);
            for (Character character : restOfLastName.toCharArray()) {
                if (character.compareTo('a') < 0 || character.compareTo('z') > 0) {
                    errors.add("Last name should contain only letters.");
                    break;
                }
            }
        }
        return errors
                .stream()
                .reduce("", (str1, str2) -> str1.concat(" ").concat(str2));
    }

    /**
     * Validates the last name quick.
     *
     * @param lastName The last name.
     * @return String of errors.
     */
    private String validateLastNameQuick(String lastName) {
        if (lastName == null) {
            return "Last name cannot be null.";
        } else if (lastName.isEmpty()) {
            return "Last name cannot be empty.";
        }
        return "";
    }

    /**
     * Validates the email of a user.
     *
     * @param email The email.
     * @return String of errors.
     */
    private String validateEmailSlow(String email) {
        ArrayList<String> errors = new ArrayList<>();

        // checking if the email is valid
        if (email == null) {
            errors.add("Email cannot be null.");
        } else if (email.isEmpty()) {
            errors.add("Email cannot be empty.");
        } else {
            List<String> emailContents = Arrays.asList(email.split("@"));

            if (emailContents.size() != 2) {
                errors.add("Email format not valid.");
            } else {
                if (emailContents.get(1).split("\\.").length == 1) {
                    errors.add("Email domain format not valid.");
                }
            }
        }

        return errors
                .stream()
                .reduce("", (str1, str2) -> str1.concat(" ").concat(str2));
    }

    private String validateEmailQuick(String email) {
        if (email == null) {
            return "Email cannot be null.";
        } else if (email.isEmpty()) {
            return "Email cannot be empty.";
        }
        return "";
    }

    /**
     * Validates an entity of type User.
     */
    @Override
    public void validateSlow(User user) throws ValidatorException {
        ArrayList<String> errors = new ArrayList<>();
        errors.add(this.validateFirstNameSlow(user.getFirstName()));
        errors.add(this.validateLastNameSlow(user.getLastName()));
        errors.add(this.validateEmailSlow(user.getEmail()));

        String result = errors
                .stream()
                .reduce("", (str1, str2) -> str1.concat("").concat(str2));

        if (!result.isEmpty()) {
            throw new ValidatorException(result);
        }
    }

    @Override
    public void validateQuick(User user) throws ValidatorException {
        ArrayList<String> errors = new ArrayList<>();
        errors.add(this.validateFirstNameQuick(user.getFirstName()));
        errors.add(this.validateLastNameQuick(user.getLastName()));
        errors.add(this.validateEmailQuick(user.getEmail()));

        String result = errors
                .stream()
                .reduce("", (str1, str2) -> str1.concat("").concat(str2));

        if (!result.isEmpty()) {
            throw new ValidatorException(result);
        }
    }
}
