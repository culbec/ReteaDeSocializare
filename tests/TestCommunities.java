import entity.Friendship;
import entity.Tuple;
import entity.User;
import repository.InMemoryRepository;
import service.Service;
import validator.ValidateStrategy;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestCommunities {
    public static void run() {
        InMemoryRepository<UUID, User> userInMemoryRepository = new InMemoryRepository<>();
        InMemoryRepository<Tuple<UUID, UUID>, Friendship> friendshipInMemoryRepository = new InMemoryRepository<>();
        Service service = new Service(userInMemoryRepository, friendshipInMemoryRepository);

        service.addUser("Ion", "Remus", "ion.remus@mail.com", ValidateStrategy.QUICK);
        service.addUser("Marius", "Chiriac", "marius.chiriac@mail.com", ValidateStrategy.QUICK);
        service.addUser("Vlad", "Remus", "vlad.remus@mail.com", ValidateStrategy.QUICK);
        service.addUser("Florin", "Remus", "florin.remus@mail.com", ValidateStrategy.QUICK);
        service.addUser("Cosmin", "Popovici", "cosmin.popovici@mail.com", ValidateStrategy.QUICK);
        service.addUser("Laura", "Matei", "laura.matei@mail.com", ValidateStrategy.QUICK);
        service.addUser("Ionut", "Andrei", "ionut.andrei@mail.com", ValidateStrategy.QUICK);

        ArrayList<User> users = service.getUsers();

        // making friends
        service.addFriendship(users.get(0).getId(), users.get(2).getId());
        service.addFriendship(users.get(0).getId(), users.get(3).getId());
        service.addFriendship(users.get(1).getId(), users.get(2).getId());
        service.addFriendship(users.get(1).getId(), users.get(3).getId());
        service.addFriendship(users.get(1).getId(), users.get(4).getId());

        service.addFriendship(users.get(5).getId(), users.get(6).getId());

        // testing communities
        int numberOfCommunities = service.getNumberOfCommunities();
        assert (numberOfCommunities == 2);
        List<List<UUID>> mostActiveCommunity = service.mostActiveCommunity();
        assert (mostActiveCommunity.size() == 1 && mostActiveCommunity.get(0).size() == 5);

        System.out.println("Communities tests passed at: " + DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(LocalDateTime.now()));
    }
}
