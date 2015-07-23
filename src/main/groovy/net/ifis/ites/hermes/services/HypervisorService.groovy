package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.domains.management.Hypervisor
import net.ifis.ites.hermes.domains.repository.HypervisorRepository
import net.ifis.ites.hermes.domains.validator.HypervisorValidator
import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for an hypervisor entity.
 *
 * @author Andreas Sekulski
 */
@Service
class HypervisorService extends DefaultCRUDService<Hypervisor> {

    /** Hypervisor repository for crud usage **/
    private HypervisorRepository repository

    @Autowired
    private MessageSource messageSource;

    @Autowired
    public HypervisorService(HypervisorRepository hypervisorRepository) {
        super(hypervisorRepository, new HypervisorValidator())
        repository = hypervisorRepository
    }

    @Override
    @Transactional
    public Hypervisor saveEntity(Hypervisor entity) throws InvalidEntityException {

        // Check if hypervisor already exists.
        if(entity.getId() == null && existsHypervisorByName(entity.getName())) {
            throw new InvalidEntityException(messageSource.getMessage("constraint.unique.error", [entity.getName()] as Object[], null, null))
        }

        return super.saveEntity(entity)
    }

    /**
     * Gets an given hypervisor by his name if exists.
     * @param name - Name from ost
     * @return NULL if not exists otherwise an hypervisor
     */
    public Hypervisor getHypervisorByName(String name) {
        return repository.findByName(name)
    }

    /**
     * Method to check if an hypervisor already exists with the same name.
     *
     * @param name
     * @return TRUE if exists otherwise FALSE
     */
    public boolean existsHypervisorByName(String name) {
        return repository.findByName(name) != null
    }
}