import entity.Friendship;
import entity.Tuple;
import entity.User;
import repository.InMemoryRepository;
import service.Service;
import ui.ConsoleUI;

import java.io.IOException;
import java.util.UUID;

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
        InMemoryRepository<UUID, User> userInMemoryRepository = new InMemoryRepository<>();
        InMemoryRepository<Tuple<UUID, UUID>, Friendship> friendshipInMemoryRepository = new InMemoryRepository<>();
        Service service = new Service(userInMemoryRepository, friendshipInMemoryRepository);

        ConsoleUI consoleUI = new ConsoleUI(service);
        consoleUI.run();
    }
}
