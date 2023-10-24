package service;

import entity.Friendship;
import entity.Tuple;
import entity.User;
import exception.RepositoryException;
import exception.ServiceException;
import exception.ValidatorException;
import repository.AbstractRepository;
import utility.Graph;
import validator.UserValidator;
import validator.ValidateStrategy;

import java.util.*;

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
     * @param strategy  Strategy by which an user will be validated.
     * @throws ServiceException If the user couldn't be added.
     */
    @Override
    public void addUser(String firstName, String lastName, String email, ValidateStrategy strategy) throws ServiceException {
        User user = new User(firstName, lastName, email);
        try {
            switch (strategy) {
                case QUICK -> new UserValidator().validateQuick(user);
                case SLOW -> new UserValidator().validateSlow(user);
            }
        } catch (ValidatorException vE) {
            throw new ServiceException("User wasn't validated.", vE);
        }

        try {
            this.users.save(user);
        } catch (RepositoryException rE) {
            throw new ServiceException("Couldn't add user.");
        }
    }

    /**
     * Removes a user from the list of users.
     *
     * @param userId ID of the user to be removed
     * @return The removed user.
     * @throws ServiceException If the user can't be removed.
     */
    @Override
    public User removeUser(UUID userId) throws ServiceException {
        try {
            Iterable<User> friendsOf = this.getFriendsOf(userId);
            for (User user : friendsOf) {
                this.friendships.delete(new Tuple<>(user.getId(), userId));
                this.friendships.delete(new Tuple<>(userId, user.getId()));
            }
            return this.users.delete(userId);
        } catch (RepositoryException rE) {
            throw new ServiceException("User couldn't be removed.", rE);
        }
    }

    /**
     * Gets a user based on its email.
     *
     * @param userId ID of the user to get.
     * @return User based on its ID.
     * @throws ServiceException If the user couldn't be found.
     */
    @Override
    public User getUser(UUID userId) throws ServiceException {
        try {
            return this.users.getOne(userId);
        } catch (RepositoryException rE) {
            throw new ServiceException("User couldn't be found.", rE);
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
        for (Friendship friendship : this.friendships.getAll()) {
            if (friendship.getId().getLeft().equals(uuid)) {
                friends.add(this.users.getOne(friendship.getId().getRight()));
            }
        }
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
    public void addFriendship(UUID id1, UUID id2) throws ServiceException, RepositoryException {
        try {
            this.friendships.save(new Friendship(id1, id2));
            this.friendships.save(new Friendship(id2, id1));
        } catch (RepositoryException rE) {
            throw new ServiceException("Couldn't add friendship.", rE);
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
        try {
            Friendship friendship1to2 = this.friendships.getOne(new Tuple<>(id1, id2));
            Friendship friendship2to1 = this.friendships.getOne(new Tuple<>(id2, id1));
            this.friendships.delete(friendship1to2.getId());
            this.friendships.delete(friendship2to1.getId());
            return friendship1to2;
        } catch (RepositoryException rE) {
            throw new ServiceException("Friendship couldn't be removed.", rE);
        }
    }

    /**
     * Returns the friendship between two users.
     *
     * @param id1 ID of the first user.
     * @param id2 ID of the second user.
     * @return The friendship between the two users.
     */
    @Override
    public Friendship getFriendship(UUID id1, UUID id2) throws ServiceException {
        try {
            return this.friendships.getOne(new Tuple<>(id1, id2));
        } catch (RepositoryException rE) {
            throw new ServiceException("Couldn't get friendship.", rE);
        }
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
     * @return Number of communities between users.
     */
    @Override
    public int getNumberOfCommunities() {
        //noinspection DuplicatedCode
        ArrayList<UUID> userIds = new ArrayList<>();
        HashMap<UUID, List<UUID>> friends = new HashMap<>();
        this.users.getAll().forEach(user -> userIds.add(user.getId()));

        for (UUID id : userIds) {
            friends.put(id, new ArrayList<>());
        }

        for (UUID id : userIds) {
            Iterable<User> friendsOf = this.getFriendsOf(id);
            for (User user : friendsOf) {
                friends.get(id).add(user.getId());
            }
        }

        Graph graph = new Graph();
        return graph.communities(userIds, friends).size();
    }

    /**
     * @return A list of the most active communities in the network.
     */
    @Override
    public List<List<UUID>> mostActiveCommunity() {
        List<List<UUID>> communityMembers = new ArrayList<>();

        Set<UUID> userSet = new HashSet<>();
        //noinspection DuplicatedCode
        ArrayList<UUID> userIds = new ArrayList<>();
        HashMap<UUID, List<UUID>> friends = new HashMap<>();

        this.users.getAll().forEach(user -> userIds.add(user.getId()));

        for (UUID id : userIds) {
            friends.put(id, new ArrayList<>());
        }

        for (UUID id : userIds) {
            Iterable<User> friendsOf = this.getFriendsOf(id);
            for (User user : friendsOf) {
                friends.get(id).add(user.getId());
            }
        }

        Graph graph = new Graph();

        int max = -1;
        for (UUID id : userIds) {
            if (!userSet.contains(id)) {
                List<UUID> component = graph.runDFS(id, userSet, friends);
                int path = graph.longestPath(component, friends);

                if (path > max) {
                    communityMembers.clear();
                    communityMembers.add(component);
                    max = path;
                } else if (path == max) {
                    communityMembers.add(component);
                }
            }
        }
        return communityMembers;
    }
}
