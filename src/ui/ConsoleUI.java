package ui;

import entity.Tuple;
import entity.User;
import exception.ServiceException;
import service.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConsoleUI extends AbstractUI {
    private final HashMap<String, Action> actions = new HashMap<>();
    private final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    private Tuple<Integer, List<List<UUID>>> uiCommunities = new Tuple<>(-1, new ArrayList<>());

    public ConsoleUI(Service service) {
        super(service);
        this.initCommands();
    }

    private String[] splitInput(String input) {
        return input.split(" ");
    }

    private void initCommands() {
        actions.put("help", this::showCommands);
        actions.put("adauga_user", this::addUserCommand);
        actions.put("remove_user", this::removeUserCommand);
        actions.put("afisare_useri", this::showUsersCommand);
        actions.put("adauga_prietenie", this::addFriendshipCommand);
        actions.put("remove_prietenie", this::removeFriendshipCommand);
        actions.put("afisare_prieteni", this::showFriendsCommands);
        actions.put("comunitati", this::numberOfCommunitiesCommand);
        actions.put("most_active", this::mostActiveCommunityCommand);
        actions.put("exit", () -> System.out.println("Closing app..."));
    }

    private void showCommands() {
        System.out.println("\nApp commands\n");
        System.out.println("""
                help - afiseaza lista de comenzi
                adauga_user - adauga un user ; parametrii se specifica separati prin spatiu ; un user are prenume, nume si adresa de email
                remove_user - sterge un user specificat prin id
                afisare_useri - afiseaza userii curenti
                adauga_prietenie - adauga o prietenie intre doi useri specificati prin id
                remove_prietenie - sterge o prieteni intre doi useri specificati prin id
                afisare_prieteni - afiseaza prieteni unui user specificat prin id de la tastatura
                comunitati - afiseaza numarul de comunitati din retea
                most_active - afiseaza cea mai activa comunitate din retea
                exit - iesire din aplicatie""");
    }

    private void addPredefined() {
        this.service.addUser("John", "Snow", "john.snow@mail.com");
        this.service.addUser("Maria", "Pop", "maria.pop@mail.com");
        this.service.addUser("Marius", "Smith", "marius.smith@mail.com");
        this.service.addUser("Florin", "Purice", "florin.purice@mail.com");
        this.service.addUser("Ioan", "Ciobotaru", "ioan.ciobo@mail.com");
        this.service.addUser("Vasile", "Pruna", "vasile.pruna@mail.com");
        this.service.addUser("Cosmin", "Ilie", "cosmin.ilie@mail.com");
        this.service.addUser("Marina", "Florian", "marina.florian@mail.com");
        this.service.addUser("Oana", "Marin", "oana.marin@mail.com");
        this.service.addUser("Ionut", "Vantu", "ionut.vantu@mail.com");
        this.service.addUser("Ana", "Manole", "ana.manole@mail.com");

        this.uiCommunities = this.service.communities();
    }

    private void addUserCommand() throws IOException {
        System.out.print("Introduce user arguments (firstName lastName email): ");
        String input = this.bufferedReader.readLine();
        String[] fields = this.splitInput(input);
        if (fields.length != 3) {
            System.out.println("Format for adding user invalid!");
            return;
        }

        String userFirstName = fields[0];
        String userLastName = fields[1];
        String userEmail = fields[2];

        try {
            this.service.addUser(userFirstName, userLastName, userEmail);
            System.out.println("User added successfully!");
            this.uiCommunities = this.service.communities();
        } catch (ServiceException sE) {
            System.out.println(sE.getMessage() + sE.getCause());
        }
    }

    private void removeUserCommand() throws IOException {
        System.out.print("Introduce user ID: ");
        String input = this.bufferedReader.readLine();
        String[] fields = this.splitInput(input);

        if (fields.length != 1) {
            System.out.println("Format for removing user invalid!");
            return;
        }

        UUID userId;

        try {
            userId = UUID.fromString(fields[0]);
            User user = this.service.removeUser(userId);
            System.out.println("Removed user: " + user);
            this.uiCommunities = this.service.communities();
        } catch (IllegalArgumentException iAE) {
            System.out.println("Specified ID is invalid.");
        } catch (ServiceException sE) {
            System.out.println(sE.getMessage() + sE.getCause());
        }
    }

    private void showUsersCommand() {
        ArrayList<User> userList = this.service.getUsers();

        if (userList.isEmpty()) {
            System.out.println("User list is empty!");
        } else {
            System.out.println("\nUSERS\n");

            // sorting using a lambda comparator
            userList.sort((o1, o2) -> {
                int firstNameCompare = o1.getFirstName().compareTo(o2.getFirstName());
                int lastNameCompare = o1.getLastName().compareTo(o2.getLastName());

                return (firstNameCompare == 0 ? lastNameCompare : firstNameCompare);
            });
            userList.forEach(System.out::println);
        }
    }

    private void addFriendshipCommand() throws IOException {
        System.out.print("Introduce user IDs: ");
        String input = this.bufferedReader.readLine();
        String[] fields = this.splitInput(input);

        if (fields.length != 2) {
            System.out.println("Format for adding friendship is invalid!");
            return;
        }

        try {
            UUID userId1, userId2;

            try {
                userId1 = UUID.fromString(fields[0]);
            } catch (IllegalArgumentException iAE) {
                System.out.println("Invalid ID format for the first user.");
                return;
            }

            try {
                userId2 = UUID.fromString(fields[1]);
            } catch (IllegalArgumentException iAE) {
                System.out.println("Invalid ID format for the second user.");
                return;
            }

            this.service.addFriendship(userId1, userId2);
            System.out.println("Friendship added successfully!");
            this.uiCommunities = this.service.communities();
        } catch (ServiceException sE) {
            System.out.println(sE.getMessage() + sE.getCause());
        }
    }

    private void removeFriendshipCommand() throws IOException {
        System.out.print("Introduce userIDs: ");
        String input = this.bufferedReader.readLine();
        String[] fields = this.splitInput(input);

        if (fields.length != 2) {
            System.out.println("Format invalid for removing friendship.");
            return;
        }

        try {
            UUID userId1, userId2;

            try {
                userId1 = UUID.fromString(fields[0]);
            } catch (IllegalArgumentException iAE) {
                System.out.println("Invalid ID format for the first user.");
                return;
            }

            try {
                userId2 = UUID.fromString(fields[1]);
            } catch (IllegalArgumentException iAE) {
                System.out.println("Invalid ID format for the second user.");
                return;
            }

            this.service.removeFriendship(userId1, userId2);
            System.out.println("Removed the friendship between: " + this.service.getUser(userId1) + " and " + this.service.getUser(userId2));
            this.uiCommunities = this.service.communities();
        } catch (ServiceException sE) {
            System.out.println(sE.getMessage() + sE.getCause());
        }
    }

    private void showFriendsCommands() throws IOException {
        System.out.print("Introduce user ID: ");
        String input = this.bufferedReader.readLine();
        String[] fields = this.splitInput(input);

        if (fields.length != 1) {
            System.out.println("Format for showing friends invalid.");
            return;
        }

        UUID userId;

        try {
            userId = UUID.fromString(fields[0]);
        } catch (IllegalArgumentException iAE) {
            System.out.println("Invalid ID specified for the user.");
            return;
        }

        ArrayList<User> friends = this.service.getFriendsOf(userId);

        if (friends.isEmpty()) {
            System.out.println("The specified user has no friends!");
        } else {
            System.out.println("\nFRIENDS\n");

            friends.sort((o1, o2) -> {
                int firstNameCompare = o1.getFirstName().compareTo(o2.getFirstName());
                int lastNameCompare = o1.getLastName().compareTo(o2.getLastName());

                return (firstNameCompare == 0 ? lastNameCompare : firstNameCompare);
            });

            friends.forEach(System.out::println);
        }
    }

    private void numberOfCommunitiesCommand() {
        if (this.uiCommunities.getLeft() != this.service.getUsers().size()) {
            System.out.println("The number of communities is: " + this.uiCommunities.getLeft());
        } else {
            System.out.println("The network has no communities!");
        }
    }

    private void mostActiveCommunityCommand() {
        if (this.uiCommunities.getRight().size() == this.service.getUsers().size()) {
            System.out.println("The network has no communities!");
        } else {
            for (List<UUID> community : this.uiCommunities.getRight()) {
                System.out.println("\nTHE MOST ACTIVE COMMUNITY' MEMBERS\n");
                for (UUID userId : community) {
                    System.out.println(this.service.getUser(userId));
                }
            }
        }
    }

    public void run() throws IOException {
        this.addPredefined();
        System.out.println("Welcome to a social network app. Down below are the commands of the app.");
        this.showCommands();

        while (true) {
            System.out.print("\nIntroduce a command: ");
            String command = this.bufferedReader.readLine();

            if (!this.actions.containsKey(command.toLowerCase())) {
                System.out.println("Invalid command!");
            } else {
                this.actions.get(command.toLowerCase()).performAction();
                if (command.equals("exit")) {
                    return;
                }
            }
        }
    }

}
