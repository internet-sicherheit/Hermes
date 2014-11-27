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

package net.ifis.ites.hermes.util

/**
 * Central constant class for I18N
 * @author Andreas Sekulski
 */
final class Constants {
    
    /** Access denied error message - I18n **/
    public static final String ACCESS_DENIED = 'default.access.denied.message'
    
    /** Default Site Name Label - I18N **/
    public static final String SITE_NAME = 'default.site.name'
	
    /** Default Title desing message - I18N **/
    public static final String DEFAULT_TITLE_MESSAGE = 'default.title.message'
	
    /** Default cuckoo sensor name - I18N **/
    public static final String DEFAULT_CUCKOO_SENSOR = 'default.cuckoo.sensor.name'
    
    /** Default empty value - I18N **/
    public static final String DEFAULT_TITLE_EMPTY = 'default.title.empty'
    
    /** Label format IP - I18N **/
    public static final String DEFAULT_IP_FORMAT_LABEL = 'default.format.ip'
	
    /** Default Adminsite Label - I18N **/
    public static final String DEFAULT_ADMIN_SITE_TITLE = 'default.site.name.administration'
    
    /** Default Login Label - I18N **/
    public static final String DEFAULT_LOGIN_LABEL = 'spring.security.ui.login.login'
    
    /** Default Reboot Label - I18N **/
    public static final String DEFAULT_JOB_REBOOT_LABEL = 'job.reboot.label'
    
    // ----- Exception Constants Begin -----
    
    /** Default Illegal Argument Exception - I18N **/
    public static final String DEFAULT_ILLEGAL_VALUE_CODE = 'default.illegal.argument'
    
    // ----- Exception Constants Ends -----
    
    // ----- Error Constants Begin -----
    
    /** Default Illegal selection in view - I18N **/
    public static final String DEFAULT_ILLEGAL_SELECTION = 'default.illegal.selection'
    
    /** Default Error Message when last SU will be deleted - I18N **/
    public static final String DEFAULT_SU_EXCEPTION = 'default.su.exception'
    
    /** Default Error Message when last SU will be updated - I18N **/
    public static final String DEFAULT_SU_UPDATED_EXCEPTION = 'default.su.updated.exception'
    
    /** Default Error Message when File not uploaded - I18N **/
    public static final String DEFAULT_FILE_EXCEPTION = 'default.file.exception'

    /** Default Error Message when Template not found - I18N **/
    public static final String DEFAULT_TEMPLATE_ERROR = 'default.template.not.found.error'
    
    /** Default Error Message when Field is Empty - I18N **/
    public static final String DEFAULT_FIELD_ERROR_EMPTY_MESSAGE = 'default.field.error.empty.message'
    
    /** Default Error Message when Field not an E-Mail - I18N **/
    public static final String DEFAULT_FIELD_ERROR_EMAIL_MESSAGE = 'default.field.error.email.message'
    
    /** Default Error Messagen when Field not an Password - I18N **/
    public static final String DEFAULT_FIELD_ERROR_PASSWORD_MESSAGE = 'default.field.error.password.message'
    
    /** Default Error Messagen when Field not in Correct Format - I18N **/
    public static final String DEFAULT_FIELD_ERROR_FORMAT_MESSAGE = 'default.field.error.format.message'
    
    /** Default all data create message - I18N **/
    public static final String DEFAULT_ALL_DATA_CREATE = 'default.all.data.create'
    
    /** Default transaction error message - I18N **/
    public static final String DEFUALT_TRANSACTION_ERROR = 'default.all.data.crashed'
    
    /** Default Created Message - I18N **/
    public static final String DEFAULT_CREATED_MESSAGE_CODE = 'default.created.message'
    
    /** Default Not Created Message - I18N **/
    public static final String DEFAULT_NOT_CREATED_MESSAGE_CODE = 'default.not.created.message'
    
    /** Default Updated Message - I18N **/
    public static final String DEFAULT_UPDATED_MESSAGE_CODE = 'default.updated.message'
    
    /** Default Not Updated Message - I18N **/
    public static final String DEFAULT_NOT_UPDATED_MESSAGE_CODE = 'default.not.updated.message'
    
    /** Default Deleted Message - I18N **/
    public static final String DEFAULT_DELETED_MESSAGE_CODE = 'default.deleted.message'
    
    /** Default Not Deleted Message - I18N **/
    public static final String DEFAULT_DATA_INTEGRATION_ERROR_CODE = 'default.data.integration.error'
    
    /** Default Update Twice Message when two Users update an Entity - I18N **/
    public static final String DEFAULT_OPTIMISTIC_LOCKING_FAILURE = 'default.optimistic.locking.failure'
    
    /** Default Critical System Error Message - I18N **/
    public static final String DEFAULT_CRITICAL_SYSTEM_ERROR_CODE = 'default.critical.system.error'
    
    /** Default Not Found Message - I18N **/
    public static final String DEFAULT_NOT_FOUND_MESSAGE_CODE = 'default.not.found.message'
    
    /** Default Not Found Entity Message - I18N **/
    public static final String DEFAULT_NOT_FOUND_ENTITY_MESSAGE_CODE = 'default.not.found.entity'
    
    /** Default Not Supported Error - I18N **/
    public static final String DEFAULT_NOT_SUPPORTED_ERROR = 'default.not.supported.error'
    
    /** Default User not loged in Error - I18N **/
    public static final String DEFAULT_USER_NOT_LOGED_IN_ERROR = 'default.user.not.loged.in.error'
    
    /** Default User has not right Error - I18N **/
    public static final String DEFAULT_USER_NOT_RIGHT_ERROR = 'default.user.has.not.right'
    
    /** Default Job Started Message - I18N **/
    public static final String DEFAULT_JOB_STARTED_MESSAGE = 'default.job.started.message'
    
    /** Default Job Started Label - I18N **/
    public static final String DEFAULT_JOB_STARTED_LABEL = 'default.job.started.label'
    
    /** Default Job Inactive Label - I18N **/
    public static final String DEFAULT_JOB_INACTIVE_LABEL = 'default.job.inactive.label'
    
    /** Default Yes Message Label - I18N **/
    public static final String DEFAULT_YES_MESSAGE = 'default.yes.message'
    
    /** Default No Message Label - I18N **/
    public static final String DEFAULT_NO_MESSAGE = 'default.no.message'
    
    // ----- Error Constants End -----
    
    // ----- Domain Classes Constants Begin -----
    
    /** Default Footer Label - I18N **/
    public static final String DEFAULT_FOOTER_LABEL = 'default.footer.label'
    
    /** Default Menu Label - I18N **/
    public static final String DEFAULT_MENU_LABEL_CODE = 'default.menu.label'
    
    /** Default Administration Label - I18N **/
    public static final String DEFAULT_ADMINISTRATION_LABEL_CODE = 'default.administration.label'
    
    /** Default Admin Label - I18N **/
    public static final String DEFAULT_ADMIN_LABEL_CODE = 'default.admin.label'
    
    /** Default File Upload Label - I18N **/
    public static final String DEFAULT_FILE_UPLOAD_LABEL_CODE = 'default.upload.label'
    
    /** Default Name Label - I18N **/
    public static final String DEFAULT_NAME_LABEL_CODE = 'default.name.label'
    
    /** Default Description Label - I18N **/
    public static final String DEFAULT_DESCRIPTION_LABEL_CODE = 'default.description.label'
    
    /** Default Meta Label - I18N **/
    public static final String DEFAULT_META_LABEL_CODE = 'default.meta.label'
    
    /** Default Type Label - I18N **/
    public static final String DEFAULT_TYPE_LABEL_CODE = 'default.type.label'
    
    /** Default Filetype Label - I18N **/
    public static final String DEFAULT_FILETYPE_LABEL_CODE = 'default.filetype.label'
    
    /** Default Filesize Label - I18N **/
    public static final String DEFAULT_FILESIZE_LABEL = 'default.filesize.label'
    
    /** Default Filename Label - I18N **/
    public static final String DEFAULT_FILENAME_LABEL_CODE = 'default.filename.label'
    
    /** Default Network Adress Name - I18N **/
    public static final String DEFAULT_IP_LABEL = 'default.ip.label'
    
    /** Default State Name - I18N **/
    public static final String DEFAULT_STATE_LABEL = 'default.state.label'
    
    /** Default MD5 Name - I18N **/
    public static final String DEFAULT_MD5_LABEL = 'default.md5.label'
    
    /** Default SHA1 Name - I18N **/
    public static final String DEFAULT_SHA1_LABEL = 'default.sha1.label'
    
    /** Default SHA256 Name - I18N **/
    public static final String DEFAULT_SHA256_LABEL = 'default.sha256.label'
    
    /** Default SHA512 Name - I18N **/
    public static final String DEFAULT_SHA512_LABEL = 'default.sha512.label'
    
    /** Default SHA512 Name - I18N **/
    public static final String DEFAULT_CPU_AVG_LABEL = 'default.cpu.load.avg'
    
    /** Default Node Label Name - I18N **/
    public static final String NODE_LABEL_CODE = 'node.label'
    
    /** Default Hypervisor Label Name - I18N **/
    public static final String HYPERVISOR_LABEL_CODE = 'hypervisor.label'
    
    /** Default Operating System Label Name - I18N  **/
    public static final String OPERATING_SYSTEM_LABEL_CODE = 'os.label'
    
    /** Default Job Label Name - I18N **/
    public static final String JOB_LABEL_CODE = 'job.label'
    
    /** Default Early Start Date - I18N **/
    public static final String DEFAULT_EARLY_START_DATE = 'default.early.start.date'
    
    /** Default Date Label Name - I18N **/
    public static final String DATE_LABEL_CODE = 'default.date.label'
    
    /** Default Date Job Label Name - I18N **/
    public static final String JOB_CHOOSE_DATE_LABEL_CODE = 'job.jobChooseDate.label'
       
    /** Default Timeout Label Name - I18N **/
    public static final String JOB_TIMEOUT_LABEL_CODE = 'job.timeout.label'
    
    /** Default Memory Dump Label Name - I18N **/
    public static final String JOB_MEMORYDUMP_LABEL_CODE = 'job.memoryDump.label'
    
    /** Default Priority Label Name - I18N **/
    public static final String JOB_PRIORITY_LABEL_CODE = 'job.priority.label'
    
    /** Default Publish Label Name - I18N **/
    public static final String JOB_PUBLISH_LABEL_CODE = 'job.publish.label'  
    
    /** Default Job Cross Create Name - I18N **/
    public static final String JOB_CROSS_CREATE = 'job.cross.create'
    
    /** Default Choose Parameter - I18N **/
    public static final String OPTION_VALUE_LOW_CODE = 'job.priority.value.low'
    
    /** Default Choose Parameter - I18N **/
    public static final String OPTION_VALUE_MEDIUM_CODE = 'job.priority.value.medium'
    
    /** Default Choose Parameter - I18N **/
    public static final String OPTION_VALUE_HIGH_CODE = 'job.priority.value.high'  
    
    /** Default Sensor Label Name - I18N **/
    public static final String SENSOR_LABEL_CODE = 'sensor.label'
    
    /** Default Sample Label Name - I18N **/
    public static final String SAMPLE_LABEL_CODE = 'sample.label'
    
    /** Default VM Label Name - I18N **/
    public static final String VM_LABEL_CODE = 'vm.label'
    
    /** Default User Label Name - I18N **/
    public static final String USER_LABEL_CODE = 'user.label'
    
    /** Default Username Label - I18N **/
    public static final String DEFAULT_USERNAME_LABEL = 'user.username.label'
    
    /** Default Userpassword Label - I18N **/
    public static final String DEFAULT_USER_PASSWORD_LABEL = 'user.password.label'
    
    /** Default Userpassword Label - I18N **/
    public static final String DEFAULT_USER_REMEMBER_ME = 'user.remember.me'
    
    /** Default User Enabled Label - I18N **/
    public static final String DEFAULT_USER_ENABLED_LABEL = 'user.enabled.label'
    
    /** Default User Enabled Label - I18N **/
    public static final String DEFAULT_USER_EMAIL_LABEL = 'user.email.label'
    
    /** Default Password Change Label - I18N **/
    public static final String DEFAULT_PASSWORD_CHANGE_LABEL = 'user.password.change.label'
    
    /** Default Account Locked Label - I18N **/
    public static final String DEFAULT_ACCOUNT_LOCKED_LABEL = 'user.accountLocked.label'
    
    // ----- Domain Classes Constants End -----
    
    // ----- Format Constants Begin -----
    
    /** Default Date Time Format - I18N **/
    public static final String DEFAULT_DATE_TIME_FORMAT_CODE = 'default.date.time.format'
    
    /** Default Time Format - I18N **/
    public static final String DEFAULT_TIME_FORMAT_CODE = 'default.time.format'
    
    // ----- Format Constants End -----
    
    // ----- Formular Constants Begin -----
    
    /** Default ID Field **/
    public static final String ENTITY_FIELD_ID = 'id'
    
    /** Default Version Field **/
    public static final String ENTITY_FIELD_VERSION = 'version'
    
    /** Default Create Label - I18N **/
    public static final String DEFAULT_CREATE_LABEL_CODE = 'default.create.label'
    
    /** Default Button Create Label - I18N **/
    public static final String DEFAULT_BUTTON_CREATE_LABEL_CODE = 'default.button.create.label'
    
    /** Default Edit Label - I18N **/
    public static final String DEFAULT_EDIT_LABEL_CODE = 'default.edit.label'
    
    /** Default Edit Button Label - I18N **/
    public static final String DEFAULT_BUTTON_EDIT_LABEL_CODE = 'default.button.edit.label'
        
    /** Default Delete Button Label - I18N **/
    public static final String DEFAULT_BUTTON_DELETE_LABEL_CODE = 'default.button.delete.label'
    
    /** Default Close Button Label - I18N **/
    public static final String DEFAULT_BUTTON_CLOSE_LABEL_CODE = 'default.button.close.label'
    
    /** Default Add Data Table Label - I18N **/
    public static final String DEFAULT_ADD_DATA_TABLE_CODE = 'default.add.data.table.message'
    
    /** Default Edit Data Table Label - I18N **/
    public static final String DEFAULT_UPDATE_DATA_TABLE_CODE = 'default.update.data.table.message'
    
    /** Default Delete Message Label - I18N **/
    public static final String DEFAULT_BUTTON_DELETE_MESSAGE = 'default.button.delete.confirm.message'
    
    // ----- Formular Constants End -----
    
    // ----- Navigation Constants Begin -----
    
    /** Default Role Label - I18N **/
    public static final String DEFAULT_ROLE_LABEL = 'role.label'
    
    /** Default Superuser Label - I18N **/
    public static final String DEFAULT_ROLE_SUPERSUER = 'role.superuser.label'
        
    /** Default Usermanagement Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_USER = 'role.usermanagement.label'
    
    /** Default Samplemanagement Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_SAMPLE = 'role.samplemanagement.label'
    
    /** Default Jobmanagement Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_JOB = 'role.jobmanagement.label'
    
    /** Default Sensormanagement Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_SENSOR = 'role.sensormanagement.label'
    
    /** Default OS Management Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_OS = 'role.osmanagement.label'
    
    /** Default Hypervisormanagement Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_HYPERVISOR = 'role.hypervisormanagement.label'
    
    /** Default VM Management Label - I18N **/
    public static final String DEFAULT_ROLE_MANAGEMENT_VM = 'role.vmmanagement.label'
    
    // ----- Navigation Constants End -----
    
    // ----- JS Constants Begin -----
    
    /** Default Category Jobstatus - I18N **/
    public static final String JS_DATA_TABLE_CATEGORY_JOBSTATUS = 'js.data.table.category.jobstatus'
    
    /** Default Action Field - I18N **/
    public static final String JS_DATA_TABLE_ACTION_LABEL = 'js.data.table.action.label'
    
    /** Default User Status Label - I18N **/
    public static final String JS_DATA_TABLE_USER_STATUS_LABEL = 'js.data.table.user.status.label'
    
    /** Default Logfile Label - I18N **/
    public static final String JS_DATA_TABLE_LOGFILE_LABEL =  'js.data.table.logfile.label'
    
    /** Default Row Class Label - I18N **/
    public static final String JS_DATA_TABLE_ROW_CLASS_LABEL =  'js.data.table.row.class'
    
    /** Default Upload Abort Label - I18N **/
    public static final String JS_UPLOAD_ABORT_BUTTON = 'js.upload.abort.button'
    
    /** Default Upload Message Label - I18N **/
    public static final String JS_UPLOAD_MESSAGE = 'js.upload.message'
    
    /** Default Start Date Message - I18N **/
    public static final String JS_START_DATE = 'js.data.table.start.date'
    
    /** Default JS Job Label - I18N **/
    public static final String JS_JOB_LABEL = 'js.job.label'
    
    /** Default JS Filter Label - I18N **/
    public static final String JS_FILTER_LABEL = 'js.filter.label'
    
    /** Default JS PJ Label - I18N **/
    public static final String JS_PJ_LABEL = 'js.pj.label'
     
    // ----- JS Constants End -----  
    
    // ----- Spring Security Constants Begin -----  
    
    /** Default Spring Security Login - I18N **/
    public static final String SPRING_SECURITY_LOGIN = 'spring.security.ui.login.signin'
    
    /** Default Spring Security Logout - I18N **/
    public static final String SPRING_SECURITY_LOGOUT =  'spring.security.ui.login.logout'
    
    // ----- Spring Security Constants End -----  
}