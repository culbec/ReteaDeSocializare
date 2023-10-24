package repository;

import entity.Entity;
import exception.RepositoryException;

/**
 * CRUD Operations for the repository interface.
 *
 * @param <E>  Entity that is stored in the Repository.
 * @param <ID> Type E must have an attribute of type ID.
 */
public interface AbstractRepository<ID, E extends Entity<ID>> {
    /**
     * Checks if the repository is empty.
     *
     * @return true if the repository is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Size of the repository.
     *
     * @return Number of entities E stored in the repository.
     */
    int size();

    /**
     * All the contents of the repository.
     *
     * @return All the values stored in the repository.
     */
    Iterable<E> getAll();

    /**
     * Searches for one entity in the repository.
     *
     * @param id ID of the Entity to search
     * @return Entity with its ID equal to id
     * @throws RepositoryException      If the entity with the specified ID doesn't exist
     * @throws IllegalArgumentException If the id is null.
     */
    E getOne(ID id) throws RepositoryException, IllegalArgumentException;

    /**
     * Adds an entity to the repository.
     *
     * @param e Entity that should be added
     * @throws RepositoryException      If the entity that should be added already exists.
     * @throws IllegalArgumentException If the entity is null.
     */
    void save(E e) throws RepositoryException, IllegalArgumentException;

    /**
     * Removes an entity from the repository
     *
     * @param id ID of the entity to remove.
     * @return Removed entity.
     * @throws RepositoryException      If the entity with the specified ID doesn't exist.
     * @throws IllegalArgumentException If the id is null.
     */
    E delete(ID id) throws RepositoryException, IllegalArgumentException;

    /**
     * Updates and entity.
     *
     * @param e New entity.
     * @return The entity before updates.
     * @throws RepositoryException      If the entity with the specified ID doesn't exist.
     * @throws IllegalArgumentException If the id is null.
     */
    E update(E e) throws RepositoryException, IllegalArgumentException;
}
