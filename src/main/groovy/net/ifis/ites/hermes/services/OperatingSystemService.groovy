package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.domains.management.OperatingSystem
import net.ifis.ites.hermes.domains.management.OperatingSystemType
import net.ifis.ites.hermes.domains.repository.OperatingSystemRepository
import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for an operating system entity.
 *
 * @author Andreas Sekulski
 */
@Service
class OperatingSystemService extends DefaultCRUDService<OperatingSystem> {

    @Autowired
    private CRUDService<OperatingSystemType> operatingSystemTypeService

    @Autowired
    OperatingSystemService(OperatingSystemRepository operatingSystemRepository) {
        super(operatingSystemRepository);
    }

    @Override
    @Transactional
    public OperatingSystem saveEntity(OperatingSystem entity) throws InvalidEntityException {

        // If operating system contains an operating system type entity
        if(entity.getType() != null && entity.getType().getId() != null) {
            entity.setType(operatingSystemTypeService.getEntityByID(entity.getType().getId()))
        }

        return super.saveEntity(entity)
    }
}