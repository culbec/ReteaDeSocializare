import entity.Friendship;
import entity.Tuple;
import entity.User;
import repository.FriendshipDBRepository;
import repository.UserDBRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestDBRepository {
    private static void clearDBUser(UserDBRepository userDBRepository) {
        try (PreparedStatement statement = userDBRepository.getConnection().prepareStatement("delete from users")) {
            statement.execute();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    private static void clearDBFriendship(FriendshipDBRepository friendshipDBRepository) {
        try (PreparedStatement statement = friendshipDBRepository.getConnection().prepareStatement("delete from friendships")) {
            statement.execute();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    public static void runUserDBRepository() throws SQLException {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialNetworkTests", "postgres", "postgres");
        clearDBUser(userDBRepository);

        assert userDBRepository.isEmpty();
        assert userDBRepository.size() == 0;

        User user1 = new User("Ion", "Lungu", "ion.lungu@mail.com");
        User user2 = new User("Maria", "Lungu", "maria.lungu@mail.com");

        assert userDBRepository.save(user1).isEmpty();
        assert userDBRepository.save(user1).isPresent();

        assert userDBRepository.save(user2).isEmpty();

        assert !userDBRepository.isEmpty();
        assert userDBRepository.size() == 2;

        assert userDBRepository.delete(user1.getId()).isPresent();
        assert userDBRepository.update(user1).isEmpty();

        User user3 = new User(user2.getId(), "Andreea", "Lungu", "andreea.lungu@mail.com");
        assert userDBRepository.update(user3).isPresent();

        assert userDBRepository.getOne(user3.getId()).isPresent();

        System.out.println("UserDBRepository passed at: " + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }

    public static void runFriendshipDBRepository() {
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialNetworkTests", "postgres", "postgres");
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialNetworkTests", "postgres", "postgres");
        clearDBFriendship(friendshipDBRepository);
        clearDBUser(userDBRepository);

        assert friendshipDBRepository.isEmpty();
        assert friendshipDBRepository.size() == 0;

        User user1 = new User("Ion", "Lungu", "ion.lungu@mail.com");
        User user2 = new User("Maria", "Lungu", "maria.lungu@mail.com");

        userDBRepository.save(user1);
        userDBRepository.save(user2);

        friendshipDBRepository.save(new Friendship(user1.getId(), user2.getId()));
        assert !friendshipDBRepository.isEmpty();
        assert friendshipDBRepository.size() == 1;

        friendshipDBRepository.delete(new Tuple<>(user2.getId(), user1.getId()));
        assert friendshipDBRepository.isEmpty();

        Friendship friendship = new Friendship(user1.getId(), user2.getId());
        friendshipDBRepository.save(friendship);

        Friendship friendshipUpdated = new Friendship(user1.getId(), user2.getId());
        friendshipDBRepository.update(friendshipUpdated);

        Friendship friendshipRet = friendshipDBRepository.getOne(friendship.getId()).get();
        assert friendshipRet.equals(friendship);

        System.out.println("FriendshipDBRepository passed at: " + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }
}
