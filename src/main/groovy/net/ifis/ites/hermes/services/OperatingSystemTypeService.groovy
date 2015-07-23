package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.domains.management.OperatingSystemType
import net.ifis.ites.hermes.domains.repository.OperatingSystemTypeRepository
import net.ifis.ites.hermes.domains.validator.OperatingSystemTypeValidator
import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service

/**
 * Service class for an operating system type entity.
 *
 * @author Andreas Sekulski
 */
@Service
class OperatingSystemTypeService extends DefaultCRUDService<OperatingSystemType> {

    /** OperatingSystemType repository for crud usage **/
    OperatingSystemTypeRepository repository

    @Autowired
    private MessageSource messageSource;

    @Autowired
    OperatingSystemTypeService(OperatingSystemTypeRepository operatingSystemTypeRepository) {
        super(operatingSystemTypeRepository, new OperatingSystemTypeValidator());
        repository = operatingSystemTypeRepository
    }

    @Override
    OperatingSystemType saveEntity(OperatingSystemType entity) throws InvalidEntityException {

        // Check if hypervisor already exists.
        if(entity.getId() == null && existsOSTByName(entity.getName())) {
            throw new InvalidEntityException(messageSource.getMessage("constraint.unique.error", [entity.getName()] as Object[], null, null))
        }

        return super.saveEntity(entity)
    }

    /**
     * Gets an given ost by his name if exists.
     * @param name - Name from ost
     * @return NULL if not exists otherwise an OST
     */
    public OperatingSystemType getOSTByName(String name) {
        return repository.findByName(name)
    }

    /**
     * Method to check if an operating system type already exists with the same name.
     *
     * @param name
     * @return TRUE if exists otherwise FALSE
     */
    public boolean existsOSTByName(String name) {
        return repository.findByName(name) != null
    }
}