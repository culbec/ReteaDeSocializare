package entity;

import java.util.*;

public class Community extends Entity<UUID> {
    private final List<User> users;

    public Community() {
        super(UUID.randomUUID());
        this.users = new ArrayList<>();
    }

    /**
     * Adds a user to the community.
     *
     * @param user User to be added to the community.
     */
    public void addUser(User user) {
        this.users.add(user);
    }

    /**
     * Remove an user from the community.
     *
     * @param userId ID of the user to be removed.
     * @return Removed user from the community.
     */
    public Optional<User> removeUser(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        for (User user : this.users) {
            if (user.getId() == userId) {
                this.users.remove(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    /**
     * @return List of users of the community.
     */
    public Iterable<User> getUsers() {
        return this.users;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Community community = (Community) o;
        return Objects.equals(users, community.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), users);
    }

    @Override
    public String toString() {
        return this.id + " " + this.users;
    }
}
