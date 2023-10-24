import entity.User;
import exception.RepositoryException;
import repository.AbstractRepository;
import repository.InMemoryRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class TestRepository {
    public static void run() throws RepositoryException {
        AbstractRepository<UUID, User> userRepository = new InMemoryRepository<>();

        User user1 = new User("Laurentiu", "Muresan", "laurentiu.muresan@mail.com");
        User user2 = new User("Marian", "Chiriac", "marian.chiriac@mail.com");

        assert (userRepository.isEmpty());

        try {
            userRepository.save(null);
            assert false;
        } catch (IllegalArgumentException exception) {
            assert true;
        }

        try {
            userRepository.getOne(null);
            assert false;
        } catch (IllegalArgumentException exception) {
            assert true;
        }

        try {
            userRepository.getOne(user1.getId());
            assert false;
        } catch (RepositoryException rE) {
            assert true;
        }

        assert (!userRepository.getAll().iterator().hasNext());

        userRepository.save(user1);
        assert (userRepository.size() == 1);

        try {
            userRepository.save(user1);
            assert false;
        } catch (RepositoryException rE) {
            assert true;
        }

        assert (userRepository.getOne(user1.getId()).equals(user1));

        try {
            userRepository.delete(null);
            assert false;
        } catch (IllegalArgumentException exception) {
            assert true;
        }

        try {
            userRepository.delete(user2.getId());
            assert false;
        } catch (RepositoryException rE) {
            assert true;
        }

        User deleted = userRepository.delete(user1.getId());
        assert (deleted.equals(user1));
        userRepository.save(user1);

        try {
            userRepository.update(null);
            assert false;
        } catch (IllegalArgumentException exception) {
            assert true;
        }

        try {
            userRepository.update(user2);
            assert false;
        } catch (RepositoryException rE) {
            assert true;
        }

        user2.setId(user1.getId());
        assert (userRepository.update(user2).equals(user1));

        System.out.println("Repository tests passed at: " + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }
}
