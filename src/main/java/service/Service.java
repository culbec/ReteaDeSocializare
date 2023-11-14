package service;

import entity.Friendship;
import entity.Tuple;
import entity.User;
import exception.RepositoryException;
import exception.ServiceException;
import exception.ValidatorException;
import repository.Repository;
import repository.UserDBRepository;
import utility.Graph;
import validator.FriendshipValidator;
import validator.UserValidator;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements AbstractService<UUID> {
    private final Repository<UUID, User> userRepository;
    private final Repository<Tuple<UUID, UUID>, Friendship> friendshipRepository;

    public Service(Repository<UUID, User> userRepo, Repository<Tuple<UUID, UUID>, Friendship> friendshipRepo) {
        this.userRepository = userRepo;
        this.friendshipRepository = friendshipRepo;
    }

    /**
     * Adds a user to the list of users.
     *
     * @param firstName The First name of the user to be added.
     * @param lastName  The Last name of the user to be added.
     * @param email     Email of the user to be added.
     * @throws ServiceException If the user couldn't be added.
     */
    @Override
    public void addUser(String firstName, String lastName, String email) throws ServiceException {
        User user = new User(firstName, lastName, email);
        try {
            new UserValidator().validate(user);
        } catch (ValidatorException vE) {
            throw new ServiceException("User wasn't validated.", vE);
        }

        try {
            if (this.userRepository.save(user).isPresent()) {
                throw new ServiceException("An user with the same ID already exists.");
            }
        } catch (RepositoryException rE) {
            throw new ServiceException("Couldn't add user.", rE);
        }
    }

    /**
     * Removes a user from the list of users.
     *
     * @param userId ID of the user to be removed
     * @return The user removed.
     * @throws ServiceException If the user can't be removed.
     */
    @Override
    public User removeUser(UUID userId) throws ServiceException {
        Optional<User> deleted = this.userRepository.delete(userId);

        if (deleted.isEmpty()) {
            throw new ServiceException("The user with the specified ID does not exist.");
        }

        Iterable<User> friendsOf = this.getFriendsOf(userId);
        friendsOf.forEach(user -> {
            if (this.friendshipRepository.getOne(new Tuple<>(userId, user.getId())).isEmpty()) {
                this.friendshipRepository.delete(new Tuple<>(user.getId(), userId));
            } else {
                this.friendshipRepository.delete(new Tuple<>(userId, user.getId()));
            }
        });

        return deleted.get();
    }

    /**
     * Gets a user based on its id.
     *
     * @param userId ID of the user to get.
     * @return The user with the specified ID.
     * @throws ServiceException If the user couldn't be found.
     */
    @Override
    public User getUser(UUID userId) throws ServiceException {
        try {
            Optional<User> userFound = this.userRepository.getOne(userId);
            if (userFound.isEmpty()) {
                throw new ServiceException("No user was found.");
            }
            return userFound.get();
        } catch (IllegalArgumentException iAE) {
            throw new ServiceException("Couldn't find user.", iAE);
        }
    }

    /**
     * Returns the user list.
     *
     * @return User list.
     */
    @Override
    public ArrayList<User> getUsers() {
        ArrayList<User> userList = new ArrayList<>();
        this.userRepository.getAll().forEach(userList::add);
        return userList;
    }

    /**
     * Returns an iterable of users which are friends with the user with the specified id.
     *
     * @param uuid ID of user.
     * @return Iterable with the friends of user with id = id
     */
    @Override
    public ArrayList<User> getFriendsOf(UUID uuid) throws RepositoryException {
        ArrayList<User> friends = new ArrayList<>();

        this.friendshipRepository.getAll().forEach(friendship -> {
            if (friendship.getId().getLeft().equals(uuid)) {
                Optional<User> friend = this.userRepository.getOne(friendship.getId().getRight());
                friend.ifPresent(friends::add);
            } else if (friendship.getId().getRight().equals(uuid)) {
                Optional<User> friend = this.userRepository.getOne(friendship.getId().getLeft());
                friend.ifPresent(friends::add);
            }
        });

        return friends;
    }

    /**
     * Adds a friendship between two users.
     *
     * @param id1 ID of the first user in the friendship.
     * @param id2 ID of the second user in the friendship.
     * @throws ServiceException If the friendship already exists
     */
    @Override
    public void addFriendship(UUID id1, UUID id2) throws ServiceException {
        try {
            Friendship friendship = new Friendship(id1, id2);
            new FriendshipValidator().validate(friendship); // the friendships are the same but reversed
            if (this.friendshipRepository.save(friendship).isPresent()) {
                throw new ServiceException("A friendship with the same ID already exists!");
            }
        } catch (ValidatorException | RepositoryException exception) {
            throw new ServiceException("Couldn't add friendship.", exception);
        }
    }

    /**
     * Removes the friendship between two users.
     *
     * @param id1 ID of the first user.
     * @param id2 ID of the second user.
     * @return Friendship that was removed.
     */
    @Override
    public Friendship removeFriendship(UUID id1, UUID id2) throws ServiceException {
        Optional<Friendship> friendship = this.friendshipRepository.getOne(new Tuple<>(id1, id2));

        if (friendship.isEmpty()) {
            friendship = this.friendshipRepository.getOne(new Tuple<>(id2, id1));
            if (friendship.isEmpty()) {
                throw new ServiceException("No friendship found.");
            } else {
                this.friendshipRepository.delete(friendship.get().getId());
            }
        } else {
            this.friendshipRepository.delete(friendship.get().getId());
        }

        return friendship.get();
    }

    /**
     * Returns the list of friendships.
     *
     * @return The list of friendships.
     */
    @Override
    public ArrayList<Friendship> getFriendships() {
        ArrayList<Friendship> friendshipList = new ArrayList<>();
        this.friendshipRepository.getAll().forEach(friendshipList::add);
        return friendshipList;
    }


    /**
     * Returns the number of communities and a list of the most active communities.
     */
    @Override
    public Tuple<Integer, List<List<UUID>>> communities() {
        List<List<UUID>> communityMembers = new ArrayList<>();

        Set<UUID> userSet = new HashSet<>();
        ArrayList<UUID> userIds = new ArrayList<>();
        HashMap<UUID, List<UUID>> friends = new HashMap<>();

        this.userRepository.getAll().forEach(user -> userIds.add(user.getId()));

        userIds.forEach(userId -> friends.put(userId, new ArrayList<>()));
        userIds.forEach(userId -> {
            Iterable<User> friendsOf = this.getFriendsOf(userId);
            friendsOf.forEach(user -> friends.get(userId).add(user.getId()));
        });

        Graph graph = new Graph();

        final int[] max = {-1};
        userIds.forEach(userId -> {
            if (!userSet.contains(userId)) {
                List<UUID> component = graph.runDFS(userId, userSet, friends);
                int path = graph.longestPath(component, friends);

                if (path > max[0]) {
                    communityMembers.clear();
                    communityMembers.add(component);
                    max[0] = path;
                } else if (path == max[0]) {
                    communityMembers.add(component);
                }
            }
        });

        Integer noCommunities = graph.communities(userIds, friends).size();
        return new Tuple<>(noCommunities, communityMembers);
    }

    /**
     * Computes a list with users that have minimum N friends.
     *
     * @param N Minimum number of friends.
     * @return List of users that have minimum N friends.
     */
    @Override
    public List<User> usersWithMinimumFriends(int N) {
        return this.getUsers().stream()
                .filter(user -> this.getFriendsOf(user.getId()).size() >= N)
                .sorted(Comparator
                        .comparingInt((User user) -> this.getFriendsOf(user.getId()).size()).reversed()
                        .thenComparing(User::getFirstName).reversed()
                        .thenComparing(User::getLastName).reversed())
                .toList();
    }

    /**
     * Returns the list of friends from a given month of the given user.
     *
     * @param userId ID of the user.
     * @param month  Month of the friendship date.
     * @return List of friends from a given month of the given user.
     */
    @Override
    public List<User> friendsFromMonth(UUID userId, String month) {
        Optional<User> userOptional = this.userRepository.getOne(userId);
        if (userOptional.isEmpty()) {
            throw new ServiceException("The user does not exist!");
        }

        Iterable<Friendship> friendshipIterable = this.friendshipRepository.getAll();
        return StreamSupport.stream(friendshipIterable.spliterator(), false)
                .filter(friendship -> friendship.getFriendshipDate().getMonthValue() == Integer.parseInt(month))
                .filter(friendship -> friendship.getId().getLeft().equals(userId) || friendship.getId().getRight().equals(userId))
                .map(friendship -> {
                    Optional<User> user;
                    if (friendship.getId().getLeft().equals(userId)) {
                        user = this.userRepository.getOne(friendship.getId().getRight());
                    } else {
                        user = this.userRepository.getOne(friendship.getId().getLeft());
                    }
                    return user.get();
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of users for which the last name contains a given string.
     *
     * @param string String to verify.
     * @return List of users for which the last name contains a given string.
     */
    public List<User> usersWithStringInLastName(String string) throws ServiceException {
        try {
            return ((UserDBRepository)this.userRepository).usersLastNameContainsString(string);
        } catch (RepositoryException repositoryException) {
            throw new ServiceException(repositoryException.getMessage());
        }
    }
}
