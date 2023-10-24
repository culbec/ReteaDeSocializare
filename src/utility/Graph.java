package utility;

import java.util.*;

public class Graph {

    /**
     * Lee algorithm to calculate the longest path from a source to the rest of the members.
     *
     * @param source  Source from where we compute the longest path.
     * @param users   Set of users.
     * @param friends Collection of User/Friends_of_User
     * @return Longest path from source to all other members in the network.
     */
    private int lee(UUID source, Set<UUID> users, HashMap<UUID, List<UUID>> friends) {
        int max = -1;
        for (UUID uuid : friends.get(source)) {
            if (!users.contains(uuid)) {
                users.add(uuid);
                int tempPath = lee(uuid, users, friends);
                if (tempPath > max) {
                    max = tempPath;
                }
                users.remove(uuid);
            }
        }
        return max + 1;
    }

    /**
     * Longest path from a source to the rest of the members.
     *
     * @param source  Source where we start the computing.
     * @param friends Collection of User/Friends_of_User
     * @return Longest path from source to the rest of the members.
     */
    private int longestPathFromSource(UUID source, HashMap<UUID, List<UUID>> friends) {
        Set<UUID> set = new HashSet<>();
        return lee(source, set, friends);
    }

    /**
     * Longest path of the whole graph on the network.
     *
     * @param users   Users from the network.
     * @param friends Collection of User/Friends_of_User
     * @return Longest path in the graph.
     */
    public int longestPath(Iterable<UUID> users, HashMap<UUID, List<UUID>> friends) {
        int max = 0;
        for (UUID uuid : users) {
            int path = this.longestPathFromSource(uuid, friends);
            if (max < path) {
                max = path;
            }
        }
        return max;
    }

    /**
     * DFS on the network.
     *
     * @param userId  UserId from where we start the search.
     * @param users   Set of users.
     * @param friends Friends with the user with userId.
     * @return List of a community of users.
     */
    public List<UUID> runDFS(UUID userId, Set<UUID> users, HashMap<UUID, List<UUID>> friends) {
        List<UUID> list = new ArrayList<>();
        list.add(userId);
        users.add(userId);

        for (UUID uuid : friends.get(userId)) {
            if (!users.contains(uuid)) {
                List<UUID> uuidFriends = runDFS(uuid, users, friends);
                list.addAll(uuidFriends);
            }
        }

        return list;
    }

    /**
     * Calculates the number of communities from a given graph using DFS.
     *
     * @return Each user with its community number.
     */
    public List<List<UUID>> communities(Iterable<UUID> users, HashMap<UUID, List<UUID>> friends) {
        Set<UUID> set = new HashSet<>();
        List<List<UUID>> list = new ArrayList<>();

        for (UUID userId : users) {
            if (!set.contains(userId)) {
                list.add(runDFS(userId, set, friends));
            }
        }
        return list;
    }
}
