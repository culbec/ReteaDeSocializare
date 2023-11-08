package service;

import entity.Friendship;
import entity.Tuple;
import entity.User;
import exception.RepositoryException;
import exception.ServiceException;
import exception.ValidatorException;
import repository.AbstractRepository;
import utility.Graph;
import validator.FriendshipValidator;
import validator.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

public class Service implements AbstractService<UUID> {
    private final AbstractRepository<UUID, User> users;
    private final AbstractRepository<Tuple<UUID, UUID>, Friendship> friendships;

    public Service(AbstractRepository<UUID, User> userRepo, AbstractRepository<Tuple<UUID, UUID>, Friendship> friendshipRepo) {
        this.users = userRepo;
        this.friendships = friendshipRepo;
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
            if (this.users.save(user).isPresent()) {
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
        Optional<User> deleted = this.users.delete(userId);

        if (deleted.isEmpty()) {
            throw new ServiceException("The user with the specified ID does not exist.");
        }

        Iterable<User> friendsOf = this.getFriendsOf(userId);
        friendsOf.forEach(user -> {
            if (this.friendships.getOne(new Tuple<>(userId, user.getId())).isEmpty()) {
                this.friendships.delete(new Tuple<>(user.getId(), userId));
            } else {
                this.friendships.delete(new Tuple<>(userId, user.getId()));
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
            Optional<User> userFound = this.users.getOne(userId);
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
        this.users.getAll().forEach(userList::add);
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

        this.friendships.getAll().forEach(friendship -> {
            if (friendship.getId().getLeft().equals(uuid)) {
                Optional<User> friend = this.users.getOne(friendship.getId().getRight());
                friend.ifPresent(friends::add);
            } else if (friendship.getId().getRight().equals(uuid)) {
                Optional<User> friend = this.users.getOne(friendship.getId().getLeft());
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
            if (this.friendships.save(friendship).isPresent()) {
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
        Optional<Friendship> friendship = this.friendships.getOne(new Tuple<>(id1, id2));

        if (friendship.isEmpty()) {
            friendship = this.friendships.getOne(new Tuple<>(id2, id1));
            if (friendship.isEmpty()) {
                throw new ServiceException("No friendship found.");
            } else {
                this.friendships.delete(friendship.get().getId());
            }
        } else {
            this.friendships.delete(friendship.get().getId());
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
        this.friendships.getAll().forEach(friendshipList::add);
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

        this.users.getAll().forEach(user -> userIds.add(user.getId()));

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
        Optional<User> userOptional = this.users.getOne(userId);
        if (userOptional.isEmpty()) {
            throw new ServiceException("The user does not exist!");
        }

        ArrayList<Friendship> friendshipArrayList = (ArrayList<Friendship>) this.friendships.getAll();
        return friendshipArrayList.stream()
                .filter(friendship -> friendship.getFriendshipDate().getMonthValue() == Integer.parseInt(month))
                .filter(friendship -> friendship.getId().getLeft().equals(userId) || friendship.getId().getRight().equals(userId))
                .map(friendship -> {
                    Optional<User> user;
                    if (friendship.getId().getLeft().equals(userId)) {
                        user = this.users.getOne(friendship.getId().getRight());
                    } else {
                        user = this.users.getOne(friendship.getId().getLeft());
                    }
                    return user.get();
                })
                .collect(Collectors.toList());
    }
}