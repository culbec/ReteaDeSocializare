package repository;

import entity.User;
import exception.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDBRepository extends DBRepository<UUID, User> {
    public UserDBRepository(String db_url, String username, String password) {
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
            return connection.prepareStatement("select COUNT(*) from users");
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
            return connection.prepareStatement("select * from users");
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for selection by ID.
     *
     * @param uuid UUID on which the interrogation will proceed.
     * @return SQL Interrogation for selection by ID.
     */
    @Override
    public PreparedStatement statementSelectOnID(Connection connection, UUID uuid) throws RepositoryException {
        String sql = "select * from users where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, uuid);
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for selection by fields.
     *
     * @param user User on which the interrogation will proceed.
     * @return SQL Interrogation for selection by fields.
     */
    @Override
    public PreparedStatement statementSelectOnFields(Connection connection, User user) throws RepositoryException {
        String sql = "select * from users where first_name = ? AND last_name = ? AND email = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for inserting into a table.
     *
     * @param user User on which the interrogation will proceed.
     * @return SQL Interrogation for inserting into a table.
     */
    @Override
    public PreparedStatement statementInsert(Connection connection, User user) throws RepositoryException {
        String sql = "insert into users(id, first_name, last_name, email) values(?, ?, ?,?)";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, user.getId());
            statement.setString(2, user.getFirstName());
            statement.setObject(3, user.getLastName());
            statement.setObject(4, user.getEmail());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for deleting from a table.
     *
     * @param uuid UUID on which the interrogation will proceed.
     * @return SQL Interrogation for deleting from a table.
     */
    @Override
    public PreparedStatement statementDelete(Connection connection, UUID uuid) throws RepositoryException {
        String sql = "delete from users where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, uuid);
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for updating rows in a table.
     *
     * @param user User on which the interrogation will proceed.
     * @return SQL Interrogation for updating rows in table.
     */
    @Override
    public PreparedStatement statementUpdate(Connection connection, User user) throws RepositoryException {
        String sql = "update users set first_name = ?, last_name = ?, email = ? where id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            statement.setObject(4, user.getId());
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL interrogation for checking if the last name of an user contains a string.
     *
     * @param string String to check in like interrogation.
     * @return SQL interrogation for checking if the last name of an user contains a string.
     */
    public PreparedStatement statementLastNameLike(Connection connection, String string) throws RepositoryException {
        String sql = "select * from users where last_name like ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + string + "%");
            return statement;
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Extracts an User from a given result set.
     *
     * @param resultSet Given result set.
     * @return User extracted from the result set.
     * @throws SQLException Resulted from the extraction if a problem was encountered.
     */
    protected User extractFromResultSet(ResultSet resultSet) throws SQLException {
        String ID = resultSet.getString("ID");
        String first_name = resultSet.getString("first_name");
        String last_name = resultSet.getString("last_name");
        String email = resultSet.getString("email");

        return new User(UUID.fromString(ID), first_name, last_name, email);
    }

    /**
     * Returns a list of users for which the last name contains a given string.
     *
     * @param string String to verify.
     * @return List of users for which the last name contains a given string.
     */
    public List<User> usersLastNameContainsString(String string) throws RepositoryException {
        try (Connection connection = this.connect()) {
            try (PreparedStatement statement = this.statementLastNameLike(connection, string)) {
                ResultSet resultSet = statement.executeQuery();
                List<User> userList = new ArrayList<>();

                while (resultSet.next()) {
                    String ID = resultSet.getString("ID");
                    String first_name = resultSet.getString("first_name");
                    String last_name = resultSet.getString("last_name");
                    String email = resultSet.getString("email");

                    User user = new User(UUID.fromString(ID), first_name, last_name, email);
                    userList.add(user);
                }
                return userList;
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }
}
