package repository;

import entity.Entity;
import exception.RepositoryException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    private final Map<ID, E> entities;

    public InMemoryRepository() {
        this.entities = new HashMap<>();
    }


    /**
     * Checks if the repository is empty.
     *
     * @return true if the repository is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return this.entities.isEmpty();
    }

    /**
     * Size of the repository.
     *
     * @return Number of entities E stored in the repository.
     */
    @Override
    public int size() {
        return this.entities.values().size();
    }

    /**
     * All the contents of the repository.
     *
     * @return All the values stored in the repository.
     */
    @Override
    public Iterable<E> getAll() {
        return this.entities.values();
    }

    /**
     * Searches for one entity in the repository.
     *
     * @param id ID of the Entity to search
     * @return {@code Optional}
     * - null if the entity with the specified ID does not exist
     * - otherwise returns the entity
     * @throws IllegalArgumentException If the id is null
     */

    @Override
    public Optional<E> getOne(ID id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null!");
        }
        return Optional.ofNullable(this.entities.get(id)); // is null if the entity doesn't exist
    }

    /**
     * Adds an entity to the repository.
     *
     * @param e Entity that should be added
     * @return an {@code Optional}
     * - null if the entity was saved
     * - the entity if it was already saved
     * @throws RepositoryException      If the entity that should be added already exists.
     * @throws IllegalArgumentException If the entity is null.
     */
    @Override
    public Optional<E> save(E e) throws RepositoryException, IllegalArgumentException {
        if (e == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        if (this.entities.containsValue(e)) {
            throw new RepositoryException("The same entity is already stored!");
        }
        return Optional.ofNullable(this.entities.putIfAbsent(e.getId(), e));
    }


    /**
     * Removes an entity from the repository
     *
     * @param id ID of the entity to remove.
     * @return an {@code Optional}
     * - null if there is no entity with the given id
     * - otherwise returns the entity
     * @throws IllegalArgumentException If the id is null.
     */
    @Override
    public Optional<E> delete(ID id) throws IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }

        return Optional.ofNullable(this.entities.remove(id));
    }

    /**
     * Updates and entity.
     *
     * @param e New entity.
     * @return an {@code Optional} encapsulating the old entity
     * @throws RepositoryException      If the entity with the specified ID doesn't exist.
     * @throws IllegalArgumentException If the e is null.
     */
    @Override
    public Optional<E> update(E e) throws RepositoryException, IllegalArgumentException {
        if (e == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }
        if (this.entities.get(e.getId()) == null) {
            throw new RepositoryException("Entity with the specified id doesn't exist!");
        }
        return Optional.ofNullable(this.entities.put(e.getId(), e));
    }
}
