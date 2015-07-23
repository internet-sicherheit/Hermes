package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import org.springframework.validation.DataBinder
import org.springframework.validation.Validator

/**
 * Abstract service class to implement default CRUD methods
 *
 * @author Andreas Sekulski
 *
 * @param < T > - Generic class type which entity should be represent
 *
 */
@Service
public abstract class DefaultCRUDService<T> implements CRUDService<T> {

    /** An specific jpa repository  **/
    private JpaRepository repository

    /** An entity validator **/
    private Validator validator

    /**
     * Constructor to create an default crud service
     * @param repository - Repository with an given entity repo
     */
    public DefaultCRUDService(JpaRepository repository) {
        this.repository = repository;
    }

    /**
     * Constructor to create an default crud service with an given validator
     * @param repository - Repository with an given entity repo
     * @param validator - Entity validator to check data
     */
    public DefaultCRUDService(JpaRepository repository, Validator validator) {
        this.repository = repository
        this.validator = validator
    }

    @Override
    @Transactional
    public T saveEntity(T entity) throws InvalidEntityException {

        if (validator != null) {
            DataBinder binder = new DataBinder(entity);
            binder.setValidator(validator)
            binder.validate()
            BindingResult result = binder.getBindingResult()

            // If validation has failed
            if (result.hasErrors()) {
                throw new InvalidEntityException(result.getFieldErrors())
            }
        }

        return repository.saveAndFlush(entity)
    }

    @Override
    public long getCount() {
        return repository.count()
    }

    @Override
    public boolean containsEntity(Serializable id) {
        return repository.exists(id)
    }

    @Override
    public T getEntityByID(Serializable id) {
        return repository.findOne(id)
    }

    @Override
    public void setValidator(Validator validator) {
        this.validator = validator
    }

    @Override
    public Validator getValidator() {
        return validator
    }
}