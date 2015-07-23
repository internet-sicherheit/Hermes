package net.ifis.ites.hermes.domains.management

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * A operating system type represents an concrete real world type from an operating system.
 *
 * @author Andreas Sekulski
 */
@Entity
class OperatingSystemType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    /** The type of the operating system, e.g. Unix or Win. */
    @Column(nullable = false, unique = true)
    private String name

    /**
     * Creates an empty operating system entity
     */
    public OperatingSystemType() {}

    /**
     * Creates an operating system type entity.
     *
     * @param name
     */
    public OperatingSystemType(String name) {
        this.name = name
    }

    public Long getId() {
        return id
    }

    public String getName() {
        return name
    }

    public void setName(String name) {
        this.name = name
    }

    public boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        OperatingSystemType that = (OperatingSystemType) o

        if (name != that.name) return false

        return true
    }

    public int hashCode() {
        return name.hashCode()
    }
}