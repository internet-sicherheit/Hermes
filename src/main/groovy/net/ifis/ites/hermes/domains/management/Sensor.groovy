package net.ifis.ites.hermes.domains.management

import org.springframework.web.multipart.MultipartFile

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * A sensor represents a sensor software component which can track
 * and log malicious software behaviour.
 *
 * @author Kevin Wittek
 */
@Entity
class Sensor {

    // ToDo: Better code this should be in an config file not in an entity
    private static final String DIR_SENSORS = "sensors/"

    @Id
    private Long id;

    /** The name of the sensor. */
    private String name

    /** A specific type. */
    @Column(nullable = true)
    private String type

    /** A more general description of the sensor's behaviour. */
    @Column(nullable = true, length = 1000)
    private String description

    /** The original name of the sensor  as it was named on the remote filesystem prior to upload. */
    private String originalFilename

    /** The md5 hash of the sensor file. */
    private String md5

    /**
     * Give the path to the file on the filesystem as a string.
     *
     * @return
     *      The path to the file.
     */
    public String getFileLocation() {
        getDir() + originalFilename
    }

    /**
     * Give the url for downloading the sensor file.
     *
     * @return
     */
    public String getFileUrl() {
        // ToDo : Better Code
        grailsApplication.config.hermes.urlDownload + DIR_SENSORS + id + "/" + originalFilename
    }

    /**
     * Writes the given file into the sensor upload directory.
     * The file is stored inside a directory named after the id.
     *
     * @param file
     *      The file that is written to the upload directory.
     *
     * @return
     *      The file that has been written.
     *
     * @throws IOException
     *      If an error occurs while writing the file.
     */
    public File writeSensorFile(MultipartFile file) throws IOException {
        // ToDo : Maybe in an Service ?
        File uploadDir = new File(grailsApplication.config.hermes.dirUpload + DIR_SENSORS + id + "/")
        File sampleFile = FileStorage.writeFile(file, uploadDir, originalFilename)

        return sampleFile
    }

    /**
     * Deletes the sample file associated with this sample.
     * Should only be called after deleting the sample!
     *
     * @return
     *      True if deleting was successful, false else.
     */
    public boolean deleteFile() {
        new File(getDir()).deleteDir()
    }

    public String getName() {
        return name
    }

    public void setName(String name) {
        this.name = name
    }

    public String getType() {
        return type
    }

    public void setType(String type) {
        this.type = type
    }

    public String getDescription() {
        return description
    }

    public void setDescription(String description) {
        this.description = description
    }

    public String getOriginalFilename() {
        return originalFilename
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename
    }

    public String getMd5() {
        return md5
    }

    public void setMd5(String md5) {
        this.md5 = md5
    }

    public boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Sensor sensor = (Sensor) o

        if (id != sensor.id) return false
        if (name != sensor.name) return false

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
        return name
    }

    /**
     * Give the path to the directory which contains the sensor file.
     *
     * @return
     *      The path to the directory as a string.
     */
    protected String getDir() {
        // ToDo Path
        grailsApplication.config.hermes.dirUpload + DIR_SENSORS + id + "/"
    }
}