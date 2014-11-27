/*
 * Copyright (c) 2013, 2014 Institute for Internet Security - if(is)
 *
 * This file is part of Hermes Malware Analysis System.
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl 5
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package net.ifis.ites.hermes.management

import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.util.Constants as C
import net.ifis.ites.hermes.util.ServerErrorCode as SEC
import net.ifis.ites.hermes.util.StatusMessage
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib
import org.springframework.dao.DataIntegrityViolationException
import java.util.GregorianCalendar
import java.sql.Timestamp

/**
 * Administration service to manage the administration frontend and backend.
 * 
 * @author Andreas Sekulski
 */
class AdministrationService {
    
    /**
     * Injected TagLib Service
     **/
    def g = new ApplicationTagLib()
    
    /**
     * Injected Management Service to save/update/delete entities
     **/
    def managementService
    
    /**
     * Injected transactional
     **/
    def transactional = false
    
    /**
     * Checks an entity object if an user has changed it.
     * 
     * @param domainObject Domain Object
     * @param version
     * 
     * @return StatusMessage an status message if object is change from an 
     * other user else null.
     **/
    public StatusMessage checkVersion(Object domainObject, Long version) { 
        if(domainObject?.version > version) {
            StatusMessage responseJSON = new StatusMessage()
            
            responseJSON.setMessage(g.message(
                    code: C.DEFAULT_OPTIMISTIC_LOCKING_FAILURE))
            
            responseJSON.setStatusCode(SEC.VERSION_ERROR_CODE)
            
            responseJSON.appendParameter('version', domainObject.version)
            
            return responseJSON
        }        
        
        return null
    }
    
    /**
     * Creates cross job data from given params
     * 
     * @params Map parameter to create jobs
     * 
     * @return StatusMessage the result of this transaction
     **/
    public StatusMessage createJobs(Map params) {
        int i = 0
        boolean isTransactionOK = true
        List<Job> jobs = new ArrayList()
        StatusMessage responseJSON = new StatusMessage()
        
        for(sample in params.samples) {
            for(vm in params.vms) {
                if(!(params.sensors) || params.sensors.isEmpty()) {
                    jobs.add(
                        new Job(
                            name : sample.name + "_" + vm.name + "_" + new Timestamp(new GregorianCalendar().getTimeInMillis()),
                            sample : sample,
                            virtualMachine : vm,
                            simulatedTime : params.simulatedTime,
                            startTime : params.startDate,
                            timeout : params.timeout,
                            priority : params.priority,
                            memoryDump : params.memoryDump,
                            owner : params.owner,
                            reboot : params.reboot
                        )
                    )
                } else {
                    for(sensor in params.sensors) {
                        jobs.add(
                            new Job(
                                name : sample.name + "_" + sensor.name + "_" + vm.name + "_" + new Timestamp(new GregorianCalendar().getTimeInMillis()),
                                sample : sample,
                                sensor: sensor,
                                virtualMachine : vm,
                                simulatedTime : params.simulatedTime,
                                startTime : params.startDate,
                                timeout : params.timeout,
                                priority : params.priority,
                                memoryDump : params.memoryDump,
                                owner : params.owner,
                                reboot : params.reboot
                            )
                        )
                    }
                }
            }
        }
        
        Job.withTransaction { status ->
            while(i < jobs.size() && isTransactionOK) {
                if(!jobs[i].save(flush:true)) {
                    isTransactionOK = false
                    status.setRollbackOnly()
                }
                i++
            }
        }
        
        if(isTransactionOK) {
            responseJSON.appendParameter('isUpdate', false)
            responseJSON.setStatusCode(SEC.OK_CODE)
            responseJSON.setMessage(g.message(code: C.DEFAULT_ALL_DATA_CREATE))
        } else {
            responseJSON = new StatusMessage()
            responseJSON.setStatusCode(SEC.NOT_FOUND_CODE)
            responseJSON.setMessage(g.message(code: C.DEFAULT_TRANSACTION_ERROR))  
        }

        return responseJSON
    }
    
    /**
     * Gets an mapping from all given hypervisors and his responds virtual
     * machines.
     **/
    public Map getVMSFromHypervisor() {
        def result
        List vms
        Map mapGroup = new HashMap()
        
        for(hypervisor in Hypervisor.list()) {
            vms = new ArrayList()
            result = VirtualMachine.findAllByHypervisor(hypervisor)
            for(vm in result) {
                vms.add(vm)
            }
            mapGroup.put(hypervisor, vms)
        }
        
        return mapGroup
    }
    
    /**
     * Gets all entity data from an given domain class that will be searched
     * with an index list.
     * 
     * @domain given domain class how should be get
     * @list identity list from all domain objects
     * 
     * return an list from all searched domain objects, can not found errors
     * contain if an domain object is not exist.
     **/
    public List getListData(Class domain, List list) {
        List objects = new ArrayList()
        Object entity
        for(int i = 0; i < list?.size(); i++) {
            entity = domain.get(list.get(i))
            if(!entity) {
               objects.add(g.message(code: C.DEFAULT_NOT_FOUND_MESSAGE_CODE, 
                       args: [getClassInternationalizing(domain), list.get(i)]))
            } else {
               objects.add(entity) 
            }
        }
        return objects
    }
    
    /**
     * Gets all errors in an String from an data set.
     * 
     * @params datas Dataset which contains errors
     * 
     * @result All erorrs as an String
     **/
    public String getErrors(List datas) {
        String errors = ""
        for(data in datas) {
            if(data instanceof String) {
                errors += data + '<br>'
            }
        }
        return errors
    }
    
    /**
     * Checks if given data contains errors
     * 
     * @params datas to check
     * 
     * @return True if data contains errors else false
     **/
    public boolean containsErrors(List datas) {
        boolean isDataInvalid = false
        
        for(data in datas) {
            if(data instanceof String) {
                isDataInvalid = true
            }
        }
        
        return isDataInvalid
    }
        
    /**
     * Try to save an domain object.
     * 
     * @param domainObject Domain Class entity to save
     * @param parameters to create
     * 
     * @return an StatusMessage if save is success else an error messsage.
     **/
    public StatusMessage create(Object domainObject, Map params) {
        
        StatusMessage responseJSON = new StatusMessage()
        
        if(domainObject instanceof Hypervisor 
            || domainObject instanceof OperatingSystem
            || domainObject instanceof User) {
            // Default grails save method
            domainObject.properties = params
            if(!domainObject.save(flush: true)) {
                responseJSON.setStatusCode(SEC.VALIDATION_EXCEPTION_CODE)
                responseJSON.setMessage(g.renderErrors(bean : domainObject).toString())
                return responseJSON
            }
        } else {
            // Management service create methods
            try {
                domainObject = createManagementService(domainObject, params);
            } catch(FileException fe) {
                // Error Fileupload
                responseJSON.setStatusCode(SEC.FILE_EXCEPTION_CODE)
                responseJSON.setMessage(g.renderErrors(bean : fe.transactionObject).toString())
                return responseJSON
            } catch(ValidationException ve) {
                // Invalid Data Error
                responseJSON.setStatusCode(SEC.VALIDATION_EXCEPTION_CODE)
                responseJSON.setMessage(g.renderErrors(bean : ve.transactionObject).toString())
                return responseJSON
            } catch(UnsupportedOperationException ue) {
                responseJSON.setStatusCode(SEC.NOT_SUPPORTED_CODE)
                responseJSON.setMessage(ue.getMessage())
                return responseJSON
            } catch(Exception e) {
                // Unknown Critical Error
                responseJSON.setStatusCode(SEC.UNKNOWN_SERVER_ERROR_CODE)
                responseJSON.setMessage(g.message(
                        code: C.DEFAULT_CRITICAL_SYSTEM_ERROR_CODE, 
                        args:[e.getMessage()]))
                return responseJSON
            }
        }
        
        responseJSON.appendParameter('isUpdate', false)
        responseJSON.setStatusCode(SEC.OK_CODE)
        responseJSON.setMessage(g.message(
                code: C.DEFAULT_CREATED_MESSAGE_CODE, 
                args: [getMessage(domainObject), domainObject]))
        
        return responseJSON
    }
    
    /**
     * Try to update an domain object.
     * 
     * @param domainObject Domain Class entity to save
     * @param parameters to update
     * 
     * @return an StatusMessage if save is success else an error messsage.
     **/
    public StatusMessage update(Object domainObject, Map params) {
        StatusMessage responseJSON = new StatusMessage()
        
        if(domainObject instanceof Hypervisor 
            || domainObject instanceof OperatingSystem
            || domainObject instanceof User) {
            
            domainObject.properties = params
            
            if(!domainObject.save(flush: true)) {
                responseJSON.setStatusCode(SEC.VALIDATION_EXCEPTION_CODE)
                responseJSON.setMessage(g.renderErrors(bean : domainObject).toString())
                return responseJSON
            }
        } else {
            try {
                domainObject = updateManagementService(domainObject, params)
            }catch(FileException fe) {
                // Error Fileupload
                responseJSON.setStatusCode(SEC.FILE_EXCEPTION_CODE)
                responseJSON.setMessage(g.renderErrors(bean : fe.transactionObject).toString())
                return responseJSON
            } catch(ValidationException ve) {
                // Invalid Data Error
                responseJSON.setStatusCode(SEC.VALIDATION_EXCEPTION_CODE)
                responseJSON.setMessage(g.renderErrors(bean : ve.transactionObject).toString())
                return responseJSON
            } catch(UnsupportedOperationException ue) {
                responseJSON.setStatusCode(SEC.NOT_SUPPORTED_CODE)
                responseJSON.setMessage(ue.getMessage())
                return responseJSON
            } catch(Exception e) {
                // Unknown Critical Error
                responseJSON.setStatusCode(SEC.UNKNOWN_SERVER_ERROR_CODE)
                responseJSON.setMessage(g.message(
                        code: C.DEFAULT_CRITICAL_SYSTEM_ERROR_CODE, 
                        args:[e.getMessage()]))
                return responseJSON
            }
        }
        
        responseJSON.appendParameter('isUpdate', true)
        responseJSON.appendParameter('id', domainObject.id)
        responseJSON.setStatusCode(SEC.OK_CODE)
        responseJSON.setMessage(g.message(
                code: C.DEFAULT_UPDATED_MESSAGE_CODE, 
                args: [getMessage(domainObject), domainObject]))
        
        return responseJSON
    }
    
    /**
     * Try to delete an domainObject
     * 
     * @param domainObject Domain class object how should be deleted
     * 
     * @return an StatusMessage from the success deletion or error.
     **/
    public StatusMessage delete(Object domainObject) {
        StatusMessage responseJSON = new StatusMessage()
        try {
            if(domainObject instanceof Hypervisor 
                || domainObject instanceof OperatingSystem
                || domainObject instanceof User) {
                domainObject.delete(flush: true)
            } else {
                deleteManagementService(domainObject)
            }
        } catch (DataIntegrityViolationException e) {
            responseJSON.setStatusCode(SEC.DATA_INTEGRATION_CODE)
            responseJSON.setMessage(g.message(
                    code: C.DEFAULT_DATA_INTEGRATION_ERROR_CODE, 
                    args: [getMessage(domainObject), domainObject]))    
            return responseJSON
        } catch(FileException fe) {
            responseJSON.setStatusCode(SEC.FILE_EXCEPTION_CODE)
            responseJSON.setMessage(g.renderErrors(bean : fe.transactionObject).toString())
            return responseJSON
        } catch(UnsupportedOperationException ue) {
            responseJSON.setStatusCode(SEC.NOT_SUPPORTED_CODE)
            responseJSON.setMessage(ue.getMessage())
            return responseJSON
        } catch(Exception e) {
            responseJSON.setStatusCode(SEC.UNKNOWN_SERVER_ERROR_CODE)
            responseJSON.setMessage(g.message(
                    code: C.DEFAULT_CRITICAL_SYSTEM_ERROR_CODE, 
                    args:[e.getMessage()]))     
            return responseJSON
        }
        
        responseJSON.setStatusCode(SEC.OK_CODE)
        responseJSON.setMessage(g.message(
                code: C.DEFAULT_DELETED_MESSAGE_CODE, 
                args: [getMessage(domainObject), domainObject]))
        
        return responseJSON
    }
    
    /**
     * Gets an object from an given domain object if exists else an illegal
     * argument exception will be thrown.
     * 
     * @param domainObject Domain class how should be get
     * @param id identity number from searched object
     **/
    public Object getEntity(Object domainObject, Long id) {
        Object entity
        if(domainObject == Hypervisor) {
            entity = Hypervisor.get(id)
        } else if(domainObject == OperatingSystem) {
            entity = OperatingSystem.get(id)
        } else if(domainObject == Sample) {
            entity = Sample.get(id)
        } else if(domainObject == Sensor) {
            entity = Sensor.get(id)
        } else if(domainObject == VirtualMachine) {
            entity = VirtualMachine.get(id)
        } else if(domainObject == Job) {
            entity = Job.get(id)
        } else if(domainObject == User) {
            entity = User.get(id)
        }
        
        if (!entity) {
            throw new IllegalArgumentException()
        }
        
        return entity
    }
    
    /**
     * Gets an I18N Message from the given class if exists else unknown class.
     * 
     * @param domain Class Object from given I18N Message
     * 
     * @return an I18N String
     **/
    private String getClassInternationalizing(Class domain) {
        if(domain.equals(Sensor)) {
            return g.message(code: C.SENSOR_LABEL_CODE)
        } else if(domain.equals(Sample)) {
            return g.message(code: C.SAMPLE_LABEL_CODE)
        } else if(domain.equals(VirtualMachine)) {
            return g.message(code: C.VM_LABEL_CODE)
        }
        
        return 'Unknown Class'
    }
    
    /**
     * Gets his message from an domain object.
     * 
     * @param domainObject class from domain class
     **/
    private String getMessage(Object domainObject) {
        if(domainObject instanceof Hypervisor) {
            return g.message(code: C.HYPERVISOR_LABEL_CODE)
        } else if(domainObject instanceof OperatingSystem) {
            return g.message(code: C.OPERATING_SYSTEM_LABEL_CODE)
        } else if(domainObject instanceof Sample) {
            return g.message(code: C.SAMPLE_LABEL_CODE)
        } else if(domainObject instanceof Sensor) {
            return g.message(code: C.SENSOR_LABEL_CODE)
        } else if(domainObject instanceof VirtualMachine) {
            return g.message(code: C.VM_LABEL_CODE)
        } else if(domainObject instanceof Job) {
            return g.message(code: C.JOB_LABEL_CODE)
        } else if(domainObject instanceof User) {
            return g.message(code: C.USER_LABEL_CODE)
        }
        return null
    }
        
    /**
     * Calls an management service method if implement else an not supported
     * exception will be call.
     * 
     * @param domainObject Domain class to update
     * @param params parmaters to update
     * 
     * @return the created object
     **/
    private Object createManagementService(Object domainObject, Map params) {
        if(domainObject instanceof Sample) {
            return managementService.createSample(
                params.name, 
                params.description, 
                params.file)
        } else if(domainObject instanceof Sensor) {
            return managementService.createSensor(
                params.name, 
                params.type, 
                params.description, 
                params.file)
        } else if(domainObject instanceof VirtualMachine) {
            return managementService.createVirtualMachine(
                params.name, 
                params.file, 
                params.description, 
                params.operatingSystemInstance, 
                params.hypervisorInstance, 
                params.osParams, 
                params.hypervisorParams)
        } else if(domainObject instanceof Job) {
            return managementService.createJob(
                params.name, 
                params.simulatedTime,
                params.timeout, 
                params.priority, 
                params.memoryDump,
                params.available, 
                params.vm ,
                params.sensorInstance, 
                params.sampleInstance, 
                params.sensorParams, 
                params.sampleParams)
        } else {
            throw new UnsupportedOperationException("Create Service not Supported for " + domainObject)
        }
    }

    /**
     * Calls an management service method if implement else an not supported
     * exception will be call.
     * 
     * @param domainObject Domain class to update
     * @param params parmaters to update
     * 
     * @return the updated object
     **/
    private Object updateManagementService(Object domainObject, Map params) {
        if(domainObject instanceof Sample) {
            return managementService.updateSample(
                domainObject,
                params.name, 
                params.description, 
                params.file)
        } else if(domainObject instanceof Sensor) {
            return managementService.updateSensor(
                domainObject, 
                params.name, 
                params.type, 
                params.description, 
                params.file)
        }  else if(domainObject instanceof VirtualMachine) {
            return managementService.updateVirtualMachine(
                domainObject,
                params.name, 
                params.file, 
                params.description, 
                params.operatingSystemInstance, 
                params.hypervisorInstance, 
                params.osParams, 
                params.hypervisorParams)
        } else if(domainObject instanceof Job) {
            return managementService.updateJob(
                domainObject,
                params.name, 
                params.description, 
                params.simulatedTime,
                params.timeout, 
                params.priority, 
                params.memoryDump, 
                params.available,
                params.vm ,
                params.sensorInstance, 
                params.sampleInstance, 
                params.sensorParams, 
                params.sampleParams)
        } else {
            throw new UnsupportedOperationException("Update Service not Supported for " + domainObject)
        }
    } 
    
    /**
     * Try to delete an domainObject with his implemented service method if
     * exists if not an unssuported exception will be thrown.
     * 
     * @param domainObject Domain Object to delete
     **/
    private void deleteManagementService(Object domainObject) {
        if(domainObject instanceof Sample) {
            managementService.deleteSample(domainObject)
        } else if(domainObject instanceof Sensor) {
            managementService.deleteSensor(domainObject)
        } else if(domainObject instanceof VirtualMachine) {
            managementService.deleteVirtualMachine(domainObject)
        } else if(domainObject instanceof Job) {
            managementService.deleteJob(domainObject)
        } else {
            throw new UnsupportedOperationException("Delete Service not Supported for " + domainObject)
        }
    }
}