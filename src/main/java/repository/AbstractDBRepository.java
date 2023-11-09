package repository;

import entity.Entity;
import exception.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements AbstractRepository<ID, E> {
    private final String DB_URL;
    private final String USERNAME;
    private final String PASSWORD;

    protected final Connection CONNECTION;

    public AbstractDBRepository(String db_url, String username, String password) {
        this.DB_URL = db_url;
        this.USERNAME = username;
        this.PASSWORD = password;

        try {
            this.CONNECTION = this.connect();
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Returns the SQL Interrogation for counting the rows in a table.
     *
     * @return SQL Interrogation for counting the rows in a table.
     */
    public abstract PreparedStatement statementCount() throws SQLException;

    /**
     * Returns the SQL Interrogation for selecting all entries in a table.
     *
     * @return SQL Interrogation for selecting all entries in a table.
     */
    public abstract PreparedStatement statementSelectAll() throws SQLException;

    /**
     * Returns the SQL Interrogation for selection by ID.
     *
     * @param id ID on which the interrogation will proceed.
     * @return SQL Interrogation for selection by ID.
     */
    public abstract PreparedStatement statementSelectOnID(ID id) throws SQLException;

    /**
     * Returns the SQL Interrogation for selection by fields.
     *
     * @param entity Entity on which the interrogation will proceed.
     * @return SQL Interrogation for selection by fields.
     */
    public abstract PreparedStatement statementSelectOnFields(E entity) throws SQLException;

    /**
     * Returns the SQL Interrogation for inserting into a table.
     *
     * @param entity Entity on which the interrogation will proceed.
     * @return SQL Interrogation for inserting into a table.
     */
    public abstract PreparedStatement statementInsert(E entity) throws SQLException;

    /**
     * Returns the SQL Interrogation for deleting from a table.
     *
     * @param id ID on which the interrogation will proceed.
     * @return SQL Interrogation for deleting from a table.
     */
    public abstract PreparedStatement statementDelete(ID id) throws SQLException;

    /**
     * Returns the SQL Interrogation for updating rows in a table.
     *
     * @param entity Entity on which the interrogation will proceed.
     * @return SQL Interrogation for updating rows in table.
     */
    public abstract PreparedStatement statementUpdate(E entity) throws SQLException;

    /**
     * Getter for the connection.
     *
     * @return Connection to the database.
     */
    public Connection getConnection() {
        return this.CONNECTION;
    }

    /**
     * Connects to the database.
     *
     * @return A connection to the database.
     * @throws SQLException Resulted from the connection if a problem was encountered.
     */
    Connection connect() throws SQLException {
        return DriverManager.getConnection(this.DB_URL, this.USERNAME, this.PASSWORD);
    }

    /**
     * Extracts an User from a given result set.
     *
     * @param resultSet Given result set.
     * @return User extracted from the result set.
     * @throws SQLException Resulted from the extraction if a problem was encountered.
     */
    protected abstract E extractFromResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Checks if the repository is empty.
     *
     * @return true if the repository is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    /**
     * Size of the repository.
     *
     * @return Number of entities E stored in the repository.
     */
    @Override
    public int size() {
        try (PreparedStatement statement = this.statementCount()) {
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return 0;
    }

    /**
     * All the contents of the repository.
     *
     * @return All the values stored in the repository.
     */
    @Override
    public Iterable<E> getAll() {
        List<E> entities = new ArrayList<>();

        try (PreparedStatement statement = this.statementSelectAll()) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                E entity = this.extractFromResultSet(resultSet);
                entities.add(entity);
            }

        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return entities;
    }

    /**
     * Searches for one entity in the repository.
     *
     * @param id ID of the Entity to search
     * @return {@code Optional}
     * - null if the entity with the specified ID does not exist
     * - otherwise returns the entity
     * @throws RepositoryException      For SQL error prone maneuvers.
     * @throws IllegalArgumentException If the id is null.
     */
    @Override
    public Optional<E> getOne(ID id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null!");
        }

        try (PreparedStatement statement = this.statementSelectOnID(id)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(this.extractFromResultSet(resultSet));
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Adds an entity to the repository.
     *
     * @param entity Entity that should be added
     * @return an {@code Optional}
     * - null if the entity was saved
     * - the entity if it was already saved
     * @throws RepositoryException      For problems encountered in SQL maneuvers.
     * @throws IllegalArgumentException If the entity is null.
     */
    @Override
    public Optional<E> save(E entity) throws RepositoryException, IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }

        try (PreparedStatement statementSelect = this.statementSelectOnFields(entity)) {
            ResultSet resultSetSelect = statementSelect.executeQuery();

            if (resultSetSelect.next()) {
                E _entity = this.extractFromResultSet(resultSetSelect);
                return Optional.of(_entity);
            }

            try (PreparedStatement statementInsert = this.statementInsert(entity)) {
                statementInsert.execute();
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
            }

        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Removes an entity from the repository
     *
     * @param id ID of the entity to remove.
     * @return an {@code Optional}
     * - null if there is no entity with the given id
     * - otherwise returns the entity
     * @throws RepositoryException      For problems encountered in SQL maneuvers.
     * @throws IllegalArgumentException If the id is null.
     */
    @Override
    public Optional<E> delete(ID id) throws RepositoryException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }

        try (PreparedStatement statementSelect = this.statementSelectOnID(id)) {
            ResultSet resultSetSelect = statementSelect.executeQuery();
            if (resultSetSelect.next()) {
                try (PreparedStatement statementDelete = this.statementDelete(id)) {
                    statementDelete.execute();
                    return Optional.of(this.extractFromResultSet(resultSetSelect));
                } catch (SQLException sqlException) {
                    throw new RepositoryException(sqlException.getMessage());
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Updates an entity.
     *
     * @param entity New entity.
     * @return an {@code Optional}
     * - null if the entity was updated
     * - otherwise (e.g. id does not exist) returns the entity
     * @throws RepositoryException      For problems encountered in SQL maneuvers.
     * @throws IllegalArgumentException If the id is null.
     */
    @Override
    public Optional<E> update(E entity) throws RepositoryException, IllegalArgumentException {
        if (entity == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }

        try (PreparedStatement statementSelect = this.statementSelectOnID(entity.getId())) {
            ResultSet resultSetSelect = statementSelect.executeQuery();

            if (resultSetSelect.next()) {
                try (PreparedStatement statementUpdate = this.statementUpdate(entity)) {
                    statementUpdate.execute();
                    return Optional.of(this.extractFromResultSet(resultSetSelect));
                } catch (SQLException sqlException) {
                    throw new RepositoryException(sqlException.getMessage());
                }
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return Optional.empty();
    }
}
