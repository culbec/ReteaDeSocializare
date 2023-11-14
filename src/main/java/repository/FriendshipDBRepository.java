package repository;

import entity.Friendship;
import entity.Tuple;
import exception.RepositoryException;

import java.sql.*;
import java.util.UUID;

public class FriendshipDBRepository extends DBRepository<Tuple<UUID, UUID>, Friendship> {
    public FriendshipDBRepository(String db_url, String username, String password) {
        super(db_url, username, password);
    }

    /**
     * Returns the SQL Interrogation for counting the rows in a table.
     *
     * @return SQL Interrogation for counting the rows in a table.
     */
    @Override
    public PreparedStatement statementCount(Connection connection) throws RepositoryException {
        try {
            return connection.prepareStatement("select COUNT(*) from friendships");
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for selecting all entries in a table.
     *
     * @return SQL Interrogation for selecting all entries in a table.
     */
    @Override
    public PreparedStatement statementSelectAll(Connection connection) throws RepositoryException {
        try {
            return connection.prepareStatement("select * from friendships");
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for selection by ID.
     *
     * @param id Tuple ID on which the interrogation will proceed.
     * @return SQL Interrogation for selection by ID.
     */
    @Override
    public PreparedStatement statementSelectOnID(Connection connection, Tuple<UUID, UUID> id) throws RepositoryException {
        String sql = "select * from friendships where (id_user1 = ? AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, id.getLeft());
            statement.setObject(2, id.getRight());
            statement.setObject(3, id.getRight());
            statement.setObject(4, id.getLeft());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for selection by fields.
     *
     * @param friendship Friendship on which the interrogation will proceed.
     * @return SQL Interrogation for selection by fields.
     */
    @Override
    public PreparedStatement statementSelectOnFields(Connection connection, Friendship friendship) throws RepositoryException {
        String sql = "select * from friendships where (id_user1 = ? AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = ?) AND date = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, friendship.getId().getLeft());
            statement.setObject(2, friendship.getId().getRight());
            statement.setObject(3, friendship.getId().getRight());
            statement.setObject(4, friendship.getId().getLeft());
            statement.setObject(5, friendship.getFriendshipDate());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for inserting into a table.
     *
     * @param friendship Friendship on which the interrogation will proceed.
     * @return SQL Interrogation for inserting into a table.
     */
    @Override
    public PreparedStatement statementInsert(Connection connection, Friendship friendship) throws RepositoryException {
        String sql = "insert into friendships(id_user1, id_user2, date) values(?, ?, ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, friendship.getId().getLeft());
            statement.setObject(2, friendship.getId().getRight());
            statement.setObject(3, friendship.getFriendshipDate());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for deleting from a table.
     *
     * @param id Tuple ID on which the interrogation will proceed.
     * @return SQL Interrogation for deleting from a table.
     */
    @Override
    public PreparedStatement statementDelete(Connection connection, Tuple<UUID, UUID> id) throws RepositoryException {
        String sql = "delete from friendships where (id_user1 = ? AND id_user2 = ?) OR (id_user1 = ? AND id_user2 = ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, id.getLeft());
            statement.setObject(2, id.getRight());
            statement.setObject(3, id.getRight());
            statement.setObject(4, id.getLeft());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for updating rows in a table.
     *
     * @param friendship Friendship on which the interrogation will proceed.
     * @return SQL Interrogation for updating rows in table.
     */
    @Override
    public PreparedStatement statementUpdate(Connection connection, Friendship friendship) throws RepositoryException {
        String sql = "update friendships set date = ? where (id_user1 = ? AND id_user2 = ?) OR (id_user2 = ? AND id_user1 = ?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, friendship.getFriendshipDate());
            statement.setObject(2, friendship.getId().getLeft());
            statement.setObject(3, friendship.getId().getRight());
            statement.setObject(4, friendship.getId().getRight());
            statement.setObject(5, friendship.getId().getLeft());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Extracts a Friendship from a given result set.
     *
     * @param resultSet Given result set.
     * @return Friendship extracted from the result set.
     * @throws SQLException Resulted from the extraction if a problem was encountered.
     */

    protected Friendship extractFromResultSet(ResultSet resultSet) throws SQLException {
        String idUser1 = resultSet.getString("id_user1");
        String idUser2 = resultSet.getString("id_user2");
        Timestamp date = resultSet.getTimestamp("date");
        return new Friendship(UUID.fromString(idUser1), UUID.fromString(idUser2), date.toLocalDateTime());
    }
}
