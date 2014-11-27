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

package net.ifis.ites.hermes.controller

import java.sql.Time
import java.text.SimpleDateFormat
import net.ifis.ites.hermes.management.*
import net.ifis.ites.hermes.security.User
import net.ifis.ites.hermes.util.Constants as C
import org.springframework.web.multipart.MultipartFile
import org.springframework.context.i18n.LocaleContextHolder 
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * FactoryParamsService to control the communication between controller and
 * view.
 * 
 * @author Andreas Sekulski
 **/
class FactoryParamsService {
    
    /**
     * Injected TagLib Service
     **/
    def g = new ApplicationTagLib()
    
    /**
     * Injected SpringSecurityService for the Mail Notification
     **/
    def springSecurityService
    
    /**
     * Entity Field Name to set the Response Parameter
     **/
    private static final String ENTITY_FIELD_NAME = 'name'
    
    /**
     * Entity Field Description to set the Response Parameter
     **/
    private static final String ENTITY_FIELD_DESCRIPTION = 'description'
    
    /**
     * Entity Field Type to set the Response Parameter
     **/
    private static final String ENTITY_FIELD_TYPE = 'type'
    
    /**
     * Entity Field Meta to set the Response Parameter
     **/
    private static final String ENTITY_FIELD_META = 'meta'
    
    /**
     * Entity Field File to set the Response Parameter
     **/
    private static final String ENTITY_FIELD_FILE = 'file'
    
    /**
     * Convert post parameters from the view if the class is exist in the
     * factory else an UnsupportedOperationException will be throws.
     *
     * @params entity class which data should be converted
     * @params params post parameters that will be send from the view
     * 
     * @throws UnsupportedOperationException when the class is not known to
     * convert parametes.
     * 
     * @return update parameters as an map
     **/
    public Map convertParams(Object entity, Map params) throws UnsupportedOperationException {
        if(entity == Hypervisor) {
            return createHypervisorParams(params)
        } else if(entity == OperatingSystem) {
            return createOSParams(params)
        } else if(entity == Sample) {
            return createSampleParams(params)
        } else if(entity == Sensor) {
            return createSensorParams(params)
        } else if(entity == VirtualMachine) {
            return createVMParams(params)
        } else if(entity == Job) {
            return createJobParams(params)
        }  else if(entity == User) {
            return createUserParams(params)
        }
        
        throw new UnsupportedOperationException(g.message(code : C.DEFAULT_NOT_SUPPORTED_ERROR))
    }
    
    /**
     * Creates User entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return User parameters as an map.
     **/
    private Map createUserParams(Map params) {
        Map entityParams = new HashMap()
        
        addLong(entityParams, params.get('userID'), 
            C.ENTITY_FIELD_ID)
        
        addLong(entityParams, params.get('userVersion'), 
            C.ENTITY_FIELD_VERSION)
        
        addString(entityParams, params.get('username'), 'username')
        
        addString(entityParams, params.get('password'), 'password')
        
        if(entityParams.password.equals('')) {
            entityParams.remove('password')
        }
        
        addString(entityParams, params.get('email'), 'email')
        
        if(params.isEnabled == null) {
            entityParams.put('enabled', false)
            entityParams.put('accountLocked', true)
        } else {
            if(params.isEnabled.equals('true')) {
                entityParams.put('enabled', true)
                entityParams.put('accountLocked', false)
            } else {
                entityParams.put('enabled', false)
                entityParams.put('accountLocked', true)
            }
        }
        
        addBoolean(entityParams, params.get('accountExpired'), 'accountExpired')
        
        addBoolean(entityParams, params.get('passwordExpired'), 'passwordExpired')
        
        entityParams.put('roles', params.roles)
                
        return entityParams 
    }
    
    /**
     * Creates Hypervisor entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return Hypervisor parameters as an map.
     **/
    private Map createHypervisorParams(Map params) {
        Map entityParams = new HashMap()
               
        addLong(entityParams, params.get('hypervisorID'), 
            C.ENTITY_FIELD_ID)
        
        addLong(entityParams, params.get('hypervisorVersion'), 
            C.ENTITY_FIELD_VERSION)
        
        addString(entityParams, params.get('nameHypervisor'), 
            ENTITY_FIELD_NAME)
        
        return entityParams
    }
    
    /**
     * Creates Operating System entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return Operating System parameters as an map.
     **/
    private Map createOSParams(Map params) {
        Map entityParams = new HashMap()
        addLong(entityParams, params.get('operatingSystemID'), 
            C.ENTITY_FIELD_ID)
        
        addLong(entityParams, params.get('operatingSystemVersion'), 
            C.ENTITY_FIELD_VERSION)
        
        addString(entityParams, params.get('osName'), ENTITY_FIELD_NAME)
        
        addString(entityParams, params.get('osDescription'), 
            ENTITY_FIELD_DESCRIPTION)
        
        addString(entityParams, params.get('osType'), 
            ENTITY_FIELD_TYPE)
        
        addString(entityParams, params.get('osMeta'), 
            ENTITY_FIELD_META)            
        
        return entityParams
    }
    
    /**
     * Creates Sample entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return Sample parameters as an map.
     **/
    private Map createSampleParams(Map params) {
        Map entityParams = new HashMap()
        addLong(entityParams, params.get('sampleID'), 
            C.ENTITY_FIELD_ID)
        
        addLong(entityParams, params.get('sampleVersion'), 
            C.ENTITY_FIELD_VERSION)
        
        addString(entityParams, params.get('sampleName'), 
            ENTITY_FIELD_NAME)
        
        addString(entityParams, params.get('sampleDescription'),
            ENTITY_FIELD_DESCRIPTION)
        
        addFile(entityParams, params.get('fileSample'), 
            ENTITY_FIELD_FILE)
        
        return entityParams
    }
    
    /**
     * Creates Sensor entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return Sensor parameters as an map.
     **/
    private Map createSensorParams(Map params) {
        Map entityParams = new HashMap()
        addLong(entityParams, params.get('sensorID'), 
            C.ENTITY_FIELD_ID)
        
        addLong(entityParams, params.get('sensorVersion'), 
            C.ENTITY_FIELD_VERSION)
        
        addString(entityParams, params.get('sensorName'), 
            ENTITY_FIELD_NAME)
        
        addString(entityParams, params.get('sensorDescription'), 
            ENTITY_FIELD_DESCRIPTION)
        
        addString(entityParams, params.get('sensorType'), 
            ENTITY_FIELD_TYPE)
        
        addFile(entityParams, params.get('fileSensor'), 
            ENTITY_FIELD_FILE)
        
        return entityParams
    }
    
    /**
     * Creates Virtual Machine entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return Virtual Machine parameters as an map.
     **/
    private Map createVMParams(Map params) {
        Map entityParams = new HashMap()
        
        addLong(entityParams, params.get('vmID'), C.ENTITY_FIELD_ID)
        
        addLong(entityParams, params.get('vmVersion')
            , C.ENTITY_FIELD_VERSION)
        
        addString(entityParams, params.get('vmName'), ENTITY_FIELD_NAME)
        
        addString(entityParams, params.get('vmDescription')
            , ENTITY_FIELD_DESCRIPTION)
        
        addFile(entityParams, params.get('fileVM'), ENTITY_FIELD_FILE)
        
        if(params.os != null) {
            entityParams.put("operatingSystemInstance", OperatingSystem.get(params.os.id))
        } else {
            entityParams.put('osParams', createOSParams(params))
        }
        
        if(params.hypervisor != null) {
            entityParams.put("hypervisorInstance", Hypervisor.get(params.hypervisor.id))
        } else {
            entityParams.put('hypervisorParams', createHypervisorParams(params))
        }
                 
        return entityParams
    }

    /**
     * Creates Job entity parameters from post params.
     * 
     * @params params post parameters that will be send from the view
     * 
     * @return Job parameters as an map.
     **/
    private Map createJobParams(Map params) {
        Map entityParams = new HashMap()
        boolean isPublic

        addLong(entityParams, params.get('jobID'), C.ENTITY_FIELD_ID)
        addLong(entityParams, params.get('jobVersion'), C.ENTITY_FIELD_VERSION)

        addInteger(entityParams, params.get('jobPriority'), 'priority')
        
        addTime(entityParams, params.get('jobTimeout'), 'timeout')
        addTime(entityParams, params.get('jobReboot'), 'reboot')
        addDateTime(entityParams, params.get('jobChooseDate'), 'simulatedTime')
        
        if(params.get('jobEarlyStartDate')) {
            addDateTime(entityParams, params.get('jobEarlyStartDate'), 'startDate')
        }
        
        addBoolean(entityParams, params.get('jobMemoryDump'), 'memoryDump')

        entityParams.put("owner", (params.get('jobPublic') ? null : springSecurityService.getCurrentUser()))

        addList(entityParams, params."sensor-multi", 'sensors')
        addList(entityParams, params."sample-multi", 'samples')
        addList(entityParams, params."vm-hypervisor-multi", 'vms')

        return entityParams
    }
    
    /**
     * Converts an list of set of specific selected ids if exists else an empty
     * list and saves it in entityParams.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     **/
    private void addList(Map entityParams, Object parameter, String fieldName) {
        List idList = new ArrayList()
        
        if(parameter && parameter instanceof String) {
            idList.add(new Long(parameter))
        } else if(parameter) {
            for(id in parameter) {
                idList.add(new Long(id))
            }
        }
        
        entityParams.put(fieldName, idList)
    }
    
    /**
     * Converts an post string parameter to an long value and saves it to 
     * the given fieldname if parameter exists.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addLong(Map entityParams, String parameter, String fieldName) {
        if(parameter != null && !parameter.isEmpty()) {
            entityParams.put(fieldName, new Long(parameter))
        }
    }
    
    /**
     * Converts an post string parameter to an integer value and saves it to 
     * the given fieldname if parameter exists.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addInteger(Map entityParams, String parameter, 
        String fieldName) {
        if(parameter != null && !parameter.isEmpty()) {
            entityParams.put(fieldName, new Integer(parameter))
        } else {
            entityParams.put(fieldName, 0)
        }
    }
    
    /**
     * Converts an post string parameter to an boolean value and saves it to 
     * the given fieldname if parameter exists.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addBoolean(Map entityParams, String parameter, 
        String fieldName) {
        
        if(parameter != null) {
            entityParams.put(fieldName, true)
        } else {
            entityParams.put(fieldName, false)
        }
    }
    
    /**
     * Converts an post string parameter to an string value and saves it to 
     * the given fieldname if parameter exists.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addString(Map entityParams, String parameter, 
        String fieldName) {
        if(parameter != null) {
            entityParams.put(fieldName, parameter)
        } else {
            entityParams.put(fieldName, "")
        }
    }
    
    /**
     * Saves an file to the given fieldname if parameter exists and is an
     * instance of an multipartfile.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addFile(Map entityParams, Object parameter, 
        String fieldName) {
        if(parameter != null && parameter instanceof MultipartFile) {
            entityParams.put(fieldName, parameter)
        }
    }
    
    /**
     * Saves an time to the given fieldname if parameter exists.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addTime(Map entityParams, String parameter, 
        String fieldName) {
        Time emptyTime = new Time(0,0,0)
        
        SimpleDateFormat timeFormatter = new SimpleDateFormat(
            g.message(code : C.DEFAULT_TIME_FORMAT_CODE))
        
        if(parameter != null && !parameter.equals("")) {
            entityParams.put(fieldName, new Time(
                    timeFormatter.parse(parameter).getTime()))
        } else {
            entityParams.put(fieldName, emptyTime)
        }         
    }
    
    /**
     * Saves an datetime to the given fieldname if parameter exists.
     * When Parameter is not given the current Date will be saved.
     * 
     * @params entityParams mapping instance to save the post parameter
     * @params parameter post parameter
     * @params fieldName saves instance name for the map
     * 
     **/
    private void addDateTime(Map entityParams, String parameter, 
        String fieldName) {
        String messageCode = g.message(code : C.DEFAULT_DATE_TIME_FORMAT_CODE)

        SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(messageCode)
        if(parameter != null && !parameter.equals("")) {
            entityParams.put(fieldName, dateTimeFormatter.parse(parameter))
        } else {
            Calendar cal = Calendar.getInstance()           
            entityParams.put(fieldName, dateTimeFormatter.parse(dateTimeFormatter.format(cal.getTime())))
        }
    }
}