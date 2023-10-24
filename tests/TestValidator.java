import entity.User;
import exception.ValidatorException;
import validator.UserValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestValidator {
    public static void run() {
        UserValidator userValidator = new UserValidator();

        User allNullUser = new User(null, null, null);
        User firstNameNullUser = new User(null, "notnull", "notnull");
        User lastNameNullUser = new User("notnull", null, "notnull");
        User emailNullUser = new User("notnull", "notnull", null);

        User allEmptyUser = new User("", "", "");
        User firstNameEmptyUser = new User("", "notempty", "notempty");
        User lastNameEmptyUser = new User("notempty", "", "notempty");
        User emailEmptyUser = new User("notempty", "notempty", "");


        // Quick validators
        try {
            userValidator.validateQuick(allNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(firstNameNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(lastNameNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(emailNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(allEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(firstNameEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(lastNameEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateQuick(emailEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }

        // Slow validators
        try {
            userValidator.validateSlow(allNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(firstNameNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(lastNameNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(emailNullUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(allEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(firstNameEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(lastNameEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }
        try {
            userValidator.validateSlow(emailEmptyUser);
            assert (false);
        } catch (ValidatorException vE) {
            assert true;
        }

        System.out.println("Validator tests completed at: " + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));

    }
}
