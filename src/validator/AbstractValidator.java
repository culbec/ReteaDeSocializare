package validator;

import entity.Entity;
import exception.ValidatorException;

/**
 * Validator interface for implementing the rest of the validators.
 */
public abstract class AbstractValidator<ID, E extends Entity<ID>> {

    /**
     * Validates an entity of a specific type.
     */
    public abstract void validateQuick(E e) throws ValidatorException;

    public abstract void validateSlow(E e) throws ValidatorException;
}

