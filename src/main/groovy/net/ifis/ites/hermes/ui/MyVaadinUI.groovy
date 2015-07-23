package net.ifis.ites.hermes.ui

import com.vaadin.server.VaadinRequest
import com.vaadin.ui.Label
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import net.ifis.ites.hermes.domains.management.Hypervisor
import net.ifis.ites.hermes.domains.management.OperatingSystem
import net.ifis.ites.hermes.domains.management.OperatingSystemType
import net.ifis.ites.hermes.domains.management.VirtualMachine
import com.vaadin.spring.annotation.SpringUI
import net.ifis.ites.hermes.services.CRUDService
import net.ifis.ites.hermes.services.HypervisorService
import net.ifis.ites.hermes.services.OperatingSystemTypeService
import net.ifis.ites.hermes.utils.exception.InvalidEntityException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@SpringUI
class MyVaadinUI extends UI {

    @Autowired
    private CRUDService<Hypervisor> hypervisorService

    @Autowired
    private CRUDService<OperatingSystemType> operatingSystemTypeService

    @Autowired
    private CRUDService<OperatingSystem> operatingSystemService

    @Autowired
    private CRUDService<VirtualMachine> virtualMachineService

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        // Mock data
        Hypervisor hypervisor = mockHypervisor("KVM")
        OperatingSystemType ost = mockOST("UNIX")
        OperatingSystemType ostWindows = mockOST("Windows")

        OperatingSystem os1 = operatingSystemService.saveEntity(
                new OperatingSystem(
                        "Ubuntu",
                        "15.04",
                        "Vivid Vervet",
                        null,
                        null,
                        null))

        OperatingSystem os2 = operatingSystemService.saveEntity(
                new OperatingSystem(
                        "Windows 8.1",
                        "8.1",
                        "Blue",
                        null,
                        null,
                        ostWindows))

        OperatingSystem os3 = operatingSystemService.saveEntity(
                new OperatingSystem(
                        "Windows 8.1",
                        "8.1",
                        "Blue",
                        null,
                        null,
                        ost))
        
        VirtualMachine vm = virtualMachineService.saveEntity(
                new VirtualMachine("VM1", null, os1, hypervisor)
        )

        VerticalLayout layout = new VerticalLayout()
        layout.addComponent(new Label("Hypervisors : " + hypervisorService.getCount()))
        layout.addComponent(new Label("OS : " + operatingSystemService.getCount()))
        layout.addComponent(new Label("OS_Type : " + operatingSystemTypeService.getCount()))
        layout.addComponent(new Label("VM's : " + virtualMachineService.getCount()))

        setContent(layout);
    }

    private Hypervisor mockHypervisor(String name) {

        Hypervisor hypervisor

        try {
            hypervisor = ((HypervisorService)hypervisorService).saveEntity(new Hypervisor(name))
        } catch (InvalidEntityException e) {
            // Only for nice visualization ;-)
            //Notification.show("Hypervisor exists already!")
        } finally {
            hypervisor = ((HypervisorService)hypervisorService).getHypervisorByName(name)
        }

        return hypervisor
    }

    private OperatingSystemType mockOST(String name) {

        OperatingSystemType ost

        try {
            ost = operatingSystemTypeService.saveEntity(new OperatingSystemType(name))
        } catch (InvalidEntityException e) {
            // Only for nice visualization ;-)
            // Notification.show("OST exists already")
        } finally {
            ost = ((OperatingSystemTypeService)operatingSystemTypeService).getOSTByName(name)
        }

        return ost
    }
}