package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.domains.management.Hypervisor
import net.ifis.ites.hermes.domains.management.OperatingSystem
import net.ifis.ites.hermes.domains.management.VirtualMachine
import net.ifis.ites.hermes.domains.repository.VirtualMachineRepository
import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service class for an virtual machine entity.
 *
 * @author Andreas Sekulski
 */
@Service
class VirtualMachineService extends DefaultCRUDService<VirtualMachine> {

    @Autowired
    private CRUDService<Hypervisor> hypervisorService

    @Autowired
    private CRUDService<OperatingSystem> operatingSystemService

    @Autowired
    public VirtualMachineService(VirtualMachineRepository virtualMachineRepository) {
        super(virtualMachineRepository);
    }

    @Override
    @Transactional
    public VirtualMachine saveEntity(VirtualMachine entity) throws InvalidEntityException {

        if(entity.getHypervisor() != null && entity.getHypervisor().getId() != null) {
            entity.setHypervisor(hypervisorService.getEntityByID(entity.getHypervisor().getId()))
        }

        if(entity.getOs() != null && entity.getOs().getId() != null) {
            entity.setOs(operatingSystemService.getEntityByID(entity.getOs().getId()))
        }

        return super.saveEntity(entity)
    }
}