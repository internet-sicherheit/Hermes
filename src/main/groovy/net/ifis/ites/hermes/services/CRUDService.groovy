package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.validation.Validator

/**
 * Generic interface class to create an default crud service for an specific given entity <T>
 *
 * @author Andreas Sekulski
 *
 * @param < T > - Entity class which should be used.
 */
@NoRepositoryBean
interface CRUDService<T> {

    /**
     * Saves or Updates the given entity if validation is successfully.
     * @param entity
     * @return Saved entity object or null if validation from entity is invalid.
     */
    public T saveEntity(T entity) throws InvalidEntityException

    /**
     * Finds entity object if exists
     *
     * @param id - Serializable id.
     * @return Searched entity or null if not exist.
     */
    public T getEntityByID(Serializable id)

    /**
     * Gets current count from all entities in database.
     * @return Long value from all entities in the database.
     */
    public long getCount()

    /**
     * Checks if entity exists with an given id.
     *
     * @param id - Serializable id to check in database.
     *
     * @return TRUE if entity exists otherwise FALSE
     */
    public boolean containsEntity(Serializable id)

    /**
     * Gets an validator if set.
     * @return The current entity validator or NULL if not be set
     */
    public Validator getValidator()

    /**
     * Sets an entity validator
     *
     * @param validator
     */
    public void setValidator(Validator validator)
}