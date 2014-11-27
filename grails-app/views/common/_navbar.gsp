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

<%--
Document      : _navbar.gsp
Created on    : 22.03.2013
Last Update   : 19.09.2013
Author        : Andreas Sekulski
Description   : GSP Template Site to Render an Administration Navigation Bar
Javascript Src: All needed JS Function are in the Administration.gsp
--%>

<%! import net.ifis.ites.hermes.util.Constants as C  %> 

<nav id="top-bar" class="top-bar" data-topbar>
  <ul class="title-area">
    <li class="name"></li>
    <li class="toggle-topbar menu-icon"><a href="#"><span>${message(code:C.DEFAULT_MENU_LABEL_CODE)}</span></a></li>
  </ul>
  <section class="top-bar-section">
    <!-- Left Nav Section -->
    <ul class="left">
        
      <sec:ifAnyGranted roles="ROLE_USERMANAGEMENT,ROLE_JOBMANAGEMENT,ROLE_SENSORMANAGEMENT,ROLE_SAMPLEMANAGEMENT,ROLE_VMMANAGEMENT,ROLE_OSMANAGEMENT,ROLE_HYPERVISORMANAGEMENT,ROLE_SUPERUSER">  
        <li class="divider"></li>
        <li id="navAdmin">
          <a href="${createLink(controller: 'administration', action: 'index')}">
            ${message(code:C.DEFAULT_ADMINISTRATION_LABEL_CODE)}
          </a>
        </li> 
      </sec:ifAnyGranted>
      
      <li class="divider"></li>
      <li id="navJobstatus">
        <a href="${createLink(controller: 'Jobstatus', action: 'jobstatus')}">
          ${message(code:C.JS_DATA_TABLE_CATEGORY_JOBSTATUS)}
        </a>
      </li>
      
      <li class="divider"></li>
      <li id="navNode">
        <a href="${createLink(controller: 'Node')}">
          ${message(code:C.NODE_LABEL_CODE)}
        </a>
      </li>
      
      <li class="divider"></li>
      
    </ul>
    <!-- Right Nav Section -->
    <ul class="right">
      <li class="has-form">
        <a class="button" href="" data-dropdown="userProfile">
            <g:message code="${C.SPRING_SECURITY_LOGIN}" args="${sec.username()}"/>
        </a>
      </li>
    </ul>
  </section>
</nav>

<ul id="userProfile" class="f-dropdown content" data-dropdown-content>
  <g:link controller="Logout" action="index">${message(code:C.SPRING_SECURITY_LOGOUT)}</g:link>
</ul>
