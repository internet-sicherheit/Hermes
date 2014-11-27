%{--
  - Copyright (c) 2013, 2014 Institute for Internet Security - if(is)
  -
  - This file is part of Hermes Malware Analysis System.
  -
  - Licensed under the EUPL, Version 1.1 or – as soon they
  - will be approved by the European Commission - subsequent
  - versions of the EUPL (the "Licence");
  - You may not use this work except in compliance with the
  - Licence.
  - You may obtain a copy of the Licence at:
  -
  - http://ec.europa.eu/idabc/eupl 5
  -
  - Unless required by applicable law or agreed to in
  - writing, software distributed under the Licence is
  - distributed on an "AS IS" basis,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
  - express or implied.
  - See the Licence for the specific language governing
  - permissions and limitations under the Licence.
  --}%

<%! import net.ifis.ites.hermes.util.Constants as C  %> 

<html>
    <head>
        <meta name="layout" content="mainLayout">
        <title>${message(code:C.DEFAULT_ADMIN_SITE_TITLE)}</title>
        <asset:javascript src="manifests/administration.js"/>
        <asset:stylesheet href="manifests/administration.css"/>
    </head>
    <body>  
        
        <nav id="subNavAdmin" class="top-bar" data-topbar>
            <ul class="title-area">
                <li class="name"></li>
                <li class="toggle-topbar menu-icon"><a href="#"><span>${message(code:C.DEFAULT_ADMIN_LABEL_CODE)}</span></a></li>
            </ul>
            
            <section class="top-bar-section">
              <ul class="left">
                <sec:ifAnyGranted roles="ROLE_USERMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                        <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.USER_LABEL_CODE)}',
                                    url: {
                                        create: '${createLink(url: [controller : 'User', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'User', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'User', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'User', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                        >
                            ${message(code:C.DEFAULT_ROLE_MANAGEMENT_USER)}
                        </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                
                <sec:ifAnyGranted roles="ROLE_HYPERVISORMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                        <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.HYPERVISOR_LABEL_CODE)}',
                                    url: {
                                        create: '${createLink(url: [controller : 'Hypervisor', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'Hypervisor', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'Hypervisor', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'Hypervisor', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                        >
                        ${message(code:C.DEFAULT_ROLE_MANAGEMENT_HYPERVISOR)}
                      </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                
                <sec:ifAnyGranted roles="ROLE_OSMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                        <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.OPERATING_SYSTEM_LABEL_CODE)}',
                                    url: {
                                        create: '${createLink(url: [controller : 'OperatingSystem', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'OperatingSystem', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'OperatingSystem', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'OperatingSystem', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                        >
                        ${message(code:C.DEFAULT_ROLE_MANAGEMENT_OS)}
                      </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                
                <sec:ifAnyGranted roles="ROLE_VMMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                        <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.VM_LABEL_CODE)}',
                                    url: {
                                        create: '${createLink(url: [controller : 'VirtualMachine', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'VirtualMachine', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'VirtualMachine', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'VirtualMachine', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                        >
                          ${message(code:C.DEFAULT_ROLE_MANAGEMENT_VM)}
                        </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                
                <sec:ifAnyGranted roles="ROLE_SAMPLEMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                      <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.SAMPLE_LABEL_CODE)}',
                                    detail: ['${message(code:C.DEFAULT_MD5_LABEL)}', '${message(code:C.DEFAULT_SHA1_LABEL)}', '${message(code:C.DEFAULT_SHA256_LABEL)}', '${message(code:C.DEFAULT_SHA512_LABEL)}'],
                                    url: {
                                        create: '${createLink(url: [controller : 'Sample', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'Sample', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'Sample', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'Sample', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                    >
                        ${message(code:C.DEFAULT_ROLE_MANAGEMENT_SAMPLE)}
                      </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                
                <sec:ifAnyGranted roles="ROLE_SENSORMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                      <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.SENSOR_LABEL_CODE)}',
                                    detail: ['${message(code:C.DEFAULT_MD5_LABEL)}'],
                                    url: {
                                        create: '${createLink(url: [controller : 'Sensor', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'Sensor', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'Sensor', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'Sensor', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                        >
                        ${message(code:C.DEFAULT_ROLE_MANAGEMENT_SENSOR)}
                      </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                
                <sec:ifAnyGranted roles="ROLE_JOBMANAGEMENT,ROLE_SUPERUSER">
                    <li>
                        <a href="" onClick="
                            $(this).initAdminView(
                                {
                                    category: '${message(code:C.JOB_LABEL_CODE)}',
                                    url: {
                                        create: '${createLink(url: [controller : 'Job', action : 'create'])}',
                                        update: '${createLink(url: [controller : 'Job', action : 'edit'])}',
                                        template: '${createLink(url: [controller : 'Job', action : 'template'])}',
                                        initDataTable: '${createLink(url: [controller : 'Job', action : 'initDataTable'])}'
                                    }
                                }); return false;"
                        >
                          ${message(code:C.DEFAULT_ROLE_MANAGEMENT_JOB)}
                        </a>
                    </li>
                    
                    <li class="divider"></li>
                </sec:ifAnyGranted>
                                
                <li class="divider"></li>
              </ul>
            </section>
        </nav>
        
        <div id="adminContainer" class="scrollbar">
            <div id="message-container"></div>

            <div id="admin-container">
                <div class="panel" id="admin-dt">
                    Bitte Kategorie auswählen
                </div>
                <a href="" class="small button" onclick="return openForm(false);">Datensatz Hinzufügen</a> 
                <a href="" class="small button" onclick="return refreshDataTable(adminSettings.dt, adminSettings.category);">Datensatz Aktualisieren</a> 
            </div>

            <div id="admin-form"></div>
        </div>
    </body>
</html>