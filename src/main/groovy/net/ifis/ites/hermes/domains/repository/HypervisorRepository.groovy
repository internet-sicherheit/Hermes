package net.ifis.ites.hermes.domains.repository

import net.ifis.ites.hermes.domains.management.Hypervisor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * CRUD-Repository for an hypervisor entity.
 *
 * @author Andreas Sekulski
 */
@Repository
public interface HypervisorRepository extends JpaRepository<Hypervisor, Long> {

    /**
     * Check if an hypervisor with the same name already exists.
     *
     * @param name
     *
     * @return If hypervisor with name exists entity will be returned otherwise NULL
     */
    public Hypervisor findByName(String name)
}