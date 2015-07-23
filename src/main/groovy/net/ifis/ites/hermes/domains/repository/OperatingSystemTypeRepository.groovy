package net.ifis.ites.hermes.domains.repository

import net.ifis.ites.hermes.domains.management.OperatingSystemType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * CRUD-Repository for an operating system type entity.
 *
 * @author Andreas Sekulski
 */
@Repository
public interface OperatingSystemTypeRepository extends JpaRepository<OperatingSystemType, Long> {

    /**
     * Check if an operating system type with the same name already exists.
     *
     * @param name
     *
     * @return If ost with name exists entity will be returned otherwise NULL
     */
    OperatingSystemType findByName(String name)
}
