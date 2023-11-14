package repository;

import entity.Entity;
import exception.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private static String DB_URL;
    private static String USERNAME;
    private static String PASSWORD;

    public DBRepository(String db_url, String username, String password) {
        DB_URL = db_url;
        USERNAME = username;
        PASSWORD = password;
    }

    /**
     * Returns the SQL Interrogation for counting the rows in a table.
     *
     * @return SQL Interrogation for counting the rows in a table.
     */
    public abstract PreparedStatement statementCount(Connection connection) throws RepositoryException;

    /**
     * Returns the SQL Interrogation for selecting all entries in a table.
     *
     * @return SQL Interrogation for selecting all entries in a table.
     */
    public abstract PreparedStatement statementSelectAll(Connection connection) throws RepositoryException;

    /**
     * Returns the SQL Interrogation for selection by ID.
     *
     * @param id ID on which the interrogation will proceed.
     * @return SQL Interrogation for selection by ID.
     */
    public abstract PreparedStatement statementSelectOnID(Connection connection, ID id) throws RepositoryException;

    /**
     * Returns the SQL Interrogation for selection by fields.
     *
     * @param entity Entity on which the interrogation will proceed.
     * @return SQL Interrogation for selection by fields.
     */
    public abstract PreparedStatement statementSelectOnFields(Connection connection, E entity) throws RepositoryException;

    /**
     * Returns the SQL Interrogation for inserting into a table.
     *
     * @param entity Entity on which the interrogation will proceed.
     * @return SQL Interrogation for inserting into a table.
     */
    public abstract PreparedStatement statementInsert(Connection connection, E entity) throws RepositoryException;

    /**
     * Returns the SQL Interrogation for deleting from a table.
     *
     * @param id ID on which the interrogation will proceed.
     * @return SQL Interrogation for deleting from a table.
     */
    public abstract PreparedStatement statementDelete(Connection connection, ID id) throws RepositoryException;

    /**
     * Returns the SQL Interrogation for updating rows in a table.
     *
     * @param entity Entity on which the interrogation will proceed.
     * @return SQL Interrogation for updating rows in table.
     */
    public abstract PreparedStatement statementUpdate(Connection connection, E entity) throws RepositoryException;

    /**
     * Connects to the database.
     *
     * @return A connection to the database.
     */
    public Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }
    }

    /**
     * Extracts an Entity from a given result set.
     *
     * @param resultSet Given result set.
     * @return Entity extracted from the result set.
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
    public int size() throws RepositoryException {
        try (Connection connection = this.connect()) {
            try (PreparedStatement statement = this.statementCount(connection)) {
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
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
    public Iterable<E> getAll() throws RepositoryException {
        List<E> entities = new ArrayList<>();

        try (Connection connection = this.connect()) {
            try (PreparedStatement statement = this.statementSelectAll(connection)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    E entity = this.extractFromResultSet(resultSet);
                    entities.add(entity);
                }
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
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
    public Optional<E> getOne(ID id) throws IllegalArgumentException, RepositoryException {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null!");
        }

        try (Connection connection = this.connect()) {
            try (PreparedStatement statement = this.statementSelectOnID(connection, id)) {
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return Optional.of(this.extractFromResultSet(resultSet));
                }
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
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

        try(Connection connection = this.connect()) {
            try (PreparedStatement statementSelect = this.statementSelectOnFields(connection, entity)) {
                ResultSet resultSetSelect = statementSelect.executeQuery();

                if (resultSetSelect.next()) {
                    E _entity = this.extractFromResultSet(resultSetSelect);
                    return Optional.of(_entity);
                }

                try (PreparedStatement statementInsert = this.statementInsert(connection, entity)) {
                    statementInsert.execute();
                } catch (SQLException sqlException) {
                    throw new RepositoryException(sqlException.getMessage());
                }

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

        try(Connection connection = this.connect()) {
            try (PreparedStatement statementSelect = this.statementSelectOnID(connection, id)) {
                ResultSet resultSetSelect = statementSelect.executeQuery();
                if (resultSetSelect.next()) {
                    try (PreparedStatement statementDelete = this.statementDelete(connection, id)) {
                        statementDelete.execute();
                        return Optional.of(this.extractFromResultSet(resultSetSelect));
                    } catch (SQLException sqlException) {
                        throw new RepositoryException(sqlException.getMessage());
                    }
                }
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
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

        try(Connection connection = this.connect()) {
            try (PreparedStatement statementSelect = this.statementSelectOnID(connection, entity.getId())) {
                ResultSet resultSetSelect = statementSelect.executeQuery();

                if (resultSetSelect.next()) {
                    try (PreparedStatement statementUpdate = this.statementUpdate(connection, entity)) {
                        statementUpdate.execute();
                        return Optional.of(this.extractFromResultSet(resultSetSelect));
                    } catch (SQLException sqlException) {
                        throw new RepositoryException(sqlException.getMessage());
                    }
                }
            } catch (SQLException sqlException) {
                throw new RepositoryException(sqlException.getMessage());
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException(sqlException.getMessage());
        }

        return Optional.empty();
    }
}
