package net.ifis.ites.hermes.domains.repository

import net.ifis.ites.hermes.domains.management.VirtualMachine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * CRUD-Repository for an virtual machine entity.
 *
 * @author Andreas Sekulski
 */
@Repository
public interface VirtualMachineRepository extends JpaRepository<VirtualMachine, Long> {

}
