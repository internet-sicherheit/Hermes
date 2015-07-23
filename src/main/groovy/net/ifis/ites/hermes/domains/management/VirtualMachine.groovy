package net.ifis.ites.hermes.domains.management

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Transient

/**
 * A virtual machine represents the concrete configurations of a
 * real virtual machine image.
 *
 * @author Kevin Wittek, Andreas Sekulski
 */
@Entity
class VirtualMachine {

    private static final String DIR_VM = "vms/"

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    /** The name of the virtual machine. */
    private String name

    /** A general description the machines features and usage. */
    @Column(nullable = true, length = 1000)
    private String description

    /** The operating system which runs on this virtual machine. */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="FK_OS")
    private OperatingSystem os

    /** The hypervisor that has to be used to run this virtual machine. */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="FK_HYPERVISOR")
    private Hypervisor hypervisor

    @Transient
    private String fileLocation

    @Transient
    private String fileUrl

    public VirtualMachine() {}

    public VirtualMachine(String name, String description, OperatingSystem os, Hypervisor hypervisor) {
        this.name = name
        this.description = description
        this.os = os
        this.hypervisor = hypervisor
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

    public String getDescription() {
        return description
    }

    public void setDescription(String description) {
        this.description = description
    }

    public OperatingSystem getOs() {
        return os
    }

    public void setOs(OperatingSystem os) {
        this.os = os
    }

    public Hypervisor getHypervisor() {
        return hypervisor
    }

    public void setHypervisor(Hypervisor hypervisor) {
        this.hypervisor = hypervisor
    }

    public boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        VirtualMachine that = (VirtualMachine) o

        if (id != that.id) return false
        if (name != that.name) return false

        return true
    }

    public int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return name;
    }
}