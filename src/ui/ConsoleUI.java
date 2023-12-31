package ui;

import entity.User;
import exception.ServiceException;
import service.Service;
import validator.ValidateStrategy;

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
    private ConsoleUI consoleUI;

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
            this.service.addUser(userFirstName, userLastName, userEmail, ValidateStrategy.SLOW);
            System.out.println("User added successfully!");
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
            for (User user : userList) {
                System.out.println(user);
            }
        }
    }

    @SuppressWarnings("DuplicatedCode")
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
        } catch (ServiceException sE) {
            System.out.println(sE.getMessage() + sE.getCause());
        }
    }

    @SuppressWarnings("DuplicatedCode")
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
            for (User user : friends) {
                System.out.println(user);
            }
        }
    }

    private void numberOfCommunitiesCommand() {
        int numberOfCommunities = this.service.getNumberOfCommunities();

        if (numberOfCommunities > 0) {
            System.out.println("The number of communities is: " + numberOfCommunities);
        } else {
            System.out.println("The network has no communities!");
        }
    }

    private void mostActiveCommunityCommand() {
        List<List<UUID>> communities = this.service.mostActiveCommunity();

        if (communities.isEmpty()) {
            System.out.println("The network has no communities!");
        } else {
            for (List<UUID> community : communities) {
                System.out.println("\nTHE MOST ACTIVE COMMUNITIES MEMBERS\n");
                for (UUID userId : community) {
                    System.out.println(this.service.getUser(userId));
                }
            }
        }
    }

    public void run() throws IOException {
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
