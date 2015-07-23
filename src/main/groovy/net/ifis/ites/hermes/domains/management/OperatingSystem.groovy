package net.ifis.ites.hermes.domains.management

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * A operating system represents a concrete real world operating system.
 *
 * @author Kevin Wittek, Andreas Sekulski
 */
@Entity
class OperatingSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id

    /** The final name from an operating system **/
    @Column(nullable = false)
    private String name

    /** The version name of the operating system. */
    @Column(nullable = true)
    private String version

    /** Which codename represents an operating system **/
    @Column(nullable = true)
    private String codeName

    /** A general description. */
    @Column(nullable = true, length = 1000)
    private String description

    /** Meta information about the operating system, e.g. build number. */
    @Column(nullable = true, length = 1000)
    private String meta

    /** The type of the operating system, e.g. Unix or Win. */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="FK_TYPE")
    private OperatingSystemType type

    /**
     * Creates an empty operating system entity.
     */
    public OperatingSystem() {}

    /**
     * Constructor to createn an operating system
     * @param name - Name from operating system like Ubuntu 15.04
     * @param version - Version from operating system
     * @param codeName - Code name from os like Vivid Vervet for Ubuntu 15.04
     * @param description - An description from this os
     * @param meta - Meta information
     * @param type - Type from system
     */
    public OperatingSystem(String name, String version, String codeName, String description, String meta, OperatingSystemType type) {
        this.name = name
        this.version = version
        this.codeName = codeName
        this.description = description
        this.meta = meta
        this.type = type
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

    public String getVersion() {
        return version
    }

    public void setVersion(String version) {
        this.version = version
    }

    public String getCodeName() {
        return codeName
    }

    public void setCodeName(String codeName) {
        this.codeName = codeName
    }

    public String getDescription() {
        return description
    }

    public void setDescription(String description) {
        this.description = description
    }

    public String getMeta() {
        return meta
    }

    public void setMeta(String meta) {
        this.meta = meta
    }

    public OperatingSystemType getType() {
        return type
    }

    public void setType(OperatingSystemType type) {
        this.type = type
    }

    public boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        OperatingSystem that = (OperatingSystem) o

        if (name != that.name) return false

        return true
    }

    public int hashCode() {
        return name.hashCode()
    }

    @Override
    public String toString() {
        return name
    }
}