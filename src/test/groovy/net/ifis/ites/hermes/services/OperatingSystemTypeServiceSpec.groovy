package net.ifis.ites.hermes.services

import net.ifis.ites.hermes.HermesApplication
import net.ifis.ites.hermes.domains.management.OperatingSystemType
import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.MessageSource
import org.springframework.test.context.ActiveProfiles
import org.springframework.validation.FieldError
import spock.lang.Specification
import spock.lang.Unroll

import javax.transaction.Transactional

/**
 * Spock Test for an operating system type entity object.
 *
 * @author Andreas Sekulski
 */
@Transactional
@ActiveProfiles("test")
@SpringApplicationConfiguration(classes = HermesApplication)
class OperatingSystemTypeServiceSpec extends Specification {

    @Autowired
    private CRUDService<OperatingSystemType> service

    @Autowired
    private MessageSource messageSource;

    /** Test all domain parameter for valid arguments **/
    @Test
    @Unroll
    def "test save valid parameter #pTest"() {
        setup:
        String name = pName
        def ost = new OperatingSystemType(name: name)
        int count = service.getCount()

        expect:
        def result = service.saveEntity(ost)

        if (result != null) {
            assert service.getEntityByID(result.getId()).equals(ost)
        }

        assert service.getCount() == count + 1

        where :
        pName   | pTest
        "Name"  | "Name is set to 'Name'"
        "ABCD"  | "ABCD is set to 'Name'"
    }

    /** Test all domain parameter for invalid arguments **/
    @Test
    @Unroll
    def "test save invalid parameter #pTest"() {
        setup:
        String assertMessage = messageSource.getMessage("empty.string", null, null, null)
        String rejectedMessage
        String name = pName
        def ost = new OperatingSystemType(name: name)
        int count = service.getCount()

        expect:
        try {
            service.saveEntity(ost)
            Assert.fail("InvalidEntityException not thrown")
        } catch (InvalidEntityException e) {
            List<FieldError> errors = e.getErrors()

            for(FieldError error : errors) {
                rejectedMessage =  messageSource.getMessage(error.getCode(), null, null, null)
                assert assertMessage.equals(rejectedMessage)
            }
        }

        assert service.getCount() == count

        where :
        pName   | pTest
        // Invalid name argument tests
        null    | "Name is null"
        ""      | "Name is empty"
        "     " | "Name is empty"
    }

    @Test
    def "test name unique property expects thrown InvalidEntityException"() {
        setup:
        String uniqueName = "Name"
        service.saveEntity(new OperatingSystemType(name: uniqueName))
        assert service.getCount() > 0
        def uniqueOST = new OperatingSystemType(name : uniqueName)

        expect:
        try {
            service.saveEntity(uniqueOST)
            Assert.fail("InvalidEntityException not thrown")
        } catch (InvalidEntityException e) {
            assert e.getMessage().equals(messageSource.getMessage("constraint.unique.error", [uniqueName] as Object[], null, null))
        }
    }

    @Test
    def "test findHypervisorByName"() {
        setup:
        service.saveEntity(new OperatingSystemType(name : "Buhu"))

        expect:
        assert ((OperatingSystemTypeService) (service)).existsOSTByName("BUHU") == false
        assert ((OperatingSystemTypeService) (service)).existsOSTByName("Buhu") == true
    }

    @Test
    def "test getHypervisorByName"() {
        setup:
        service.saveEntity(new OperatingSystemType(name : "Buhu"))

        expect:
        assert ((OperatingSystemTypeService) (service)).getOSTByName("BUHU") == null
        assert ((OperatingSystemTypeService) (service)).getOSTByName("Buhu") != true
    }
}