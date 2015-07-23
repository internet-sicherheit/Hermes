package net.ifis.ites.hermes.domains.repository

import net.ifis.ites.hermes.domains.management.OperatingSystem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * CRUD-Repository for an operating system entity.
 *
 * @author Andreas Sekulski
 */
@Repository
public interface OperatingSystemRepository extends JpaRepository<OperatingSystem, Long> {



}