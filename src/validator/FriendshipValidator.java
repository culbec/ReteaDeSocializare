package validator;

import entity.Friendship;
import entity.Tuple;
import exception.ValidatorException;

import java.util.UUID;

public class FriendshipValidator extends AbstractValidator<Tuple<UUID, UUID>, Friendship> {
    /**
     * Validates an entity of a specific type.
     *
     * @param friendship Friendship to validate
     */
    @Override
    public void validate(Friendship friendship) throws ValidatorException {
        if (friendship == null) {
            throw new ValidatorException("Friendship cannot be null!");
        } else if (friendship.getId().getLeft() == null || friendship.getId().getRight() == null) {
            throw new ValidatorException("Friendship contents cannot be null!");
        } else if (friendship.getId().getLeft().equals(friendship.getId().getRight())) {
            throw new ValidatorException("Friendship cannot be made between the same user!");
        }
    }

}
