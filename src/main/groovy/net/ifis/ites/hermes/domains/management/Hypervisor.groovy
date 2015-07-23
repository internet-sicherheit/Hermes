package net.ifis.ites.hermes.domains.management

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

/**
 * Represents a hypervisor that can be used to run images of virtual machines.
 *
 * @author Kevin Wittek, Andreas Sekulski
 */
@Entity
class Hypervisor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    /** The name of the hypervisor. */
    @Column(nullable = false, unique = true)
    private String name

    /**
     * Constructor to create an empty hypervisor entity.
     */
    public Hypervisor() { }

    /**
     * Constructor to create an hypervisor entity.
     *
     * @param name - Unique name from hypervisor
     */
    public Hypervisor(String name) {
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

    @Override
    public boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Hypervisor that = (Hypervisor) o

        if (name != that.name) return false

        return true
    }

    @Override
    public int hashCode() {
        return name.hashCode()
    }

    @Override
    public String toString() {
        return name
    }
}