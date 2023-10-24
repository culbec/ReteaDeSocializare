package repository;

import entity.Entity;
import exception.RepositoryException;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements AbstractRepository<ID, E> {
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
     * @return Entity with its ID equal to id
     * @throws RepositoryException      If the entity with the specified ID doesn't exist
     * @throws IllegalArgumentException If the id is null
     */

    @Override
    public E getOne(ID id) throws RepositoryException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("The id cannot be null!");
        }
        if (this.entities.get(id) == null) {
            throw new RepositoryException("Entity with the specified id doesn't exist!");
        }
        return this.entities.get(id);
    }

    /**
     * Adds an entity to the repository.
     *
     * @param e Entity that should be added
     * @throws RepositoryException      If the entity that should be added already exists.
     * @throws IllegalArgumentException If the entity is null.
     */
    @Override
    public void save(E e) throws RepositoryException, IllegalArgumentException {
        if (e == null) {
            throw new IllegalArgumentException("Entity cannot be null!");
        }
        if (this.entities.get(e.getId()) != null) {
            throw new RepositoryException("An entity with the same id is already stored!");
        } else {
            for (Entity<ID> entity : this.entities.values()) {
                if (entity.equals(e)) {
                    throw new RepositoryException("The same entity is already stored!");
                }
            }
        }
        this.entities.put(e.getId(), e);
    }


    /**
     * Removes an entity from the repository
     *
     * @param id ID of the entity to remove.
     * @return Removed entity.
     * @throws RepositoryException      If the entity with the specified ID doesn't exist.
     * @throws IllegalArgumentException If the id is null.
     */
    @Override
    public E delete(ID id) throws RepositoryException, IllegalArgumentException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }
        if (this.entities.get(id) == null) {
            throw new RepositoryException("Entity with the specified id doesn't exist!");
        }

        return this.entities.remove(id);
    }

    /**
     * Updates and entity.
     *
     * @param e New entity.
     * @return The entity before update.
     * @throws RepositoryException      If the entity with the specified ID doesn't exist.
     * @throws IllegalArgumentException If the e is null.
     */
    @Override
    public E update(E e) throws RepositoryException, IllegalArgumentException {
        if (e == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }
        if (this.entities.get(e.getId()) == null) {
            throw new RepositoryException("Entity with the specified id doesn't exist!");
        }
        return this.entities.put(e.getId(), e);
    }
}
