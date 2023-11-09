package repository;

import entity.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDBRepository extends AbstractDBRepository<UUID, User> {
    public UserDBRepository(String db_url, String username, String password) {
        super(db_url, username, password);
    }

    /**
     * Returns the SQL Interrogation for counting the rows in a table.
     *
     * @return SQL Interrogation for counting the rows in a table.
     */
    @Override
    public PreparedStatement statementCount() throws SQLException {
        return this.CONNECTION.prepareStatement("select COUNT(*) from users");
    }

    /**
     * Returns the SQL Interrogation for selecting all entries in a table.
     *
     * @return SQL Interrogation for selecting all entries in a table.
     */
    @Override
    public PreparedStatement statementSelectAll() throws SQLException {
        return this.CONNECTION.prepareStatement("select * from users");
    }

    /**
     * Returns the SQL Interrogation for selection by ID.
     *
     * @param uuid UUID on which the interrogation will proceed.
     * @return SQL Interrogation for selection by ID.
     */
    @Override
    public PreparedStatement statementSelectOnID(UUID uuid) throws SQLException {
        String sql = "select * from users where id = ?";
        PreparedStatement statement = this.CONNECTION.prepareStatement(sql);
        statement.setObject(1, uuid);
        return statement;
    }

    /**
     * Returns the SQL Interrogation for selection by fields.
     *
     * @param user User on which the interrogation will proceed.
     * @return SQL Interrogation for selection by fields.
     */
    @Override
    public PreparedStatement statementSelectOnFields(User user) throws SQLException {
        String sql = "select * from users where first_name = ? AND last_name = ? AND email = ?";
        PreparedStatement statement = this.CONNECTION.prepareStatement(sql);
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        return statement;
    }

    /**
     * Returns the SQL Interrogation for inserting into a table.
     *
     * @param user User on which the interrogation will proceed.
     * @return SQL Interrogation for inserting into a table.
     */
    @Override
    public PreparedStatement statementInsert(User user) throws SQLException {
        String sql = "insert into users(id, first_name, last_name, email) values(?, ?, ?,?)";
        PreparedStatement statement = this.CONNECTION.prepareStatement(sql);
        statement.setObject(1, user.getId());
        statement.setString(2, user.getFirstName());
        statement.setObject(3, user.getLastName());
        statement.setObject(4, user.getEmail());
        return statement;
    }

    /**
     * Returns the SQL Interrogation for deleting from a table.
     *
     * @param uuid UUID on which the interrogation will proceed.
     * @return SQL Interrogation for deleting from a table.
     */
    @Override
    public PreparedStatement statementDelete(UUID uuid) throws SQLException {
        String sql = "delete from users where id = ?";
        PreparedStatement statement = this.CONNECTION.prepareStatement(sql);
        statement.setObject(1, uuid);
        return statement;
    }

    /**
     * Returns the SQL Interrogation for updating rows in a table.
     *
     * @param user User on which the interrogation will proceed.
     * @return SQL Interrogation for updating rows in table.
     */
    @Override
    public PreparedStatement statementUpdate(User user) throws SQLException {
        String sql = "update users set first_name = ?, last_name = ?, email = ? where id = ?";
        PreparedStatement statement = this.CONNECTION.prepareStatement(sql);
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        statement.setObject(4, user.getId());
        return statement;
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
}
