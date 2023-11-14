import repository.FriendshipDBRepository;
import repository.UserDBRepository;
import service.Service;
import ui.ConsoleUI;

import java.io.IOException;

public class Main {
    /*
    TODO
     - switch from ID operations in UI;
     - make so that the email will be unique for each user;
     - user equality will ensure that (firstName, lastName and email are equal);
     - stop duplicating data by using ID's in business logic;
     - and other optimizations;
     */
    public static void main(String[] args) throws IOException {
        /*InMemoryRepository<UUID, User> userInMemoryRepository = new InMemoryRepository<>();
        InMemoryRepository<Tuple<UUID, UUID>, Friendship> friendshipInMemoryRepository = new InMemoryRepository<>();*/

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "postgres");

        Service service = new Service(userDBRepository, friendshipDBRepository);

        ConsoleUI consoleUI = new ConsoleUI(service);
        consoleUI.run();
    }
}
