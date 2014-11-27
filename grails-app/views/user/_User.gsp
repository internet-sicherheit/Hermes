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

<%@ page import="net.ifis.ites.hermes.security.Role" %>
<%! import net.ifis.ites.hermes.util.Constants as C  %> 

<div class="row">
  <div class="large-12 columns">
    <g:if test="${instance?.enabled == false}">
      <input type="radio" name="isEnabled" id="isEnabled" value=true> 
      ${message(code:C.DEFAULT_USER_ENABLED_LABEL)}
      <input type="radio" name="isEnabled" id="isEnabled" value=false CHECKED> 
      ${message(code:C.DEFAULT_ACCOUNT_LOCKED_LABEL)}
    </g:if>
    <g:else>
      <input type="radio" name="isEnabled" id="isEnabled" value=true CHECKED> 
      ${message(code:C.DEFAULT_USER_ENABLED_LABEL)}
      <input type="radio" name="isEnabled" id="isEnabled" value=false > 
      ${message(code:C.DEFAULT_ACCOUNT_LOCKED_LABEL)}
    </g:else>
  </div>
</div>

<div class="row">
  <div class="large-12 columns">
    <label>
      ${message(code:C.DEFAULT_USERNAME_LABEL)}
      <span class="required-indicator">*</span>
    </label>
    <input type="text" id='username' name="username" value='${instance?.username}' required>
    <small class="error">
      ${message(code:C.DEFAULT_FIELD_ERROR_EMPTY_MESSAGE, 
        args:[message(code : C.DEFAULT_USERNAME_LABEL)])}
    </small>
  </div>
</div>

<div class="row">
  <div class="large-12 columns">
    <label>
      ${message(code:C.DEFAULT_USER_EMAIL_LABEL)}
      <span class="required-indicator">*</span>
    </label>
    <input type="email" id='email' name="email" value='${instance?.email}' required>
    <small class="error">
      ${message(code:C.DEFAULT_FIELD_ERROR_EMAIL_MESSAGE, 
        args:[message(code : C.DEFAULT_USER_EMAIL_LABEL)])}
    </small>
  </div>
</div>

<g:if test="${instance?.password == null}">
  <g:render template="Password" />
</g:if> 
<g:else>
  <div class="row">
    <div class="large-12 columns" id='pwReset'>
      <a href="" onClick="return showPasswordField()" class="button">
        ${message(code:C.DEFAULT_PASSWORD_CHANGE_LABEL)}
      </a>
    </div>
</div>
</g:else>

<sec:ifAnyGranted roles="ROLE_SUPERUSER">
    <div class="row">
      <div class="large-12 columns">
        <g:if test="${instance.isAttached()}">
          <g:each in="${Role.getAll()}">
            <g:if test="${instance.containsRole(it)}">
                <input type="checkbox" name="roles" id="roles" value="${it.id}" checked="checked">
                <g:message code="${it.getMessageCode()}"/> 
                <br>
            </g:if>
            <g:else>
              <input type="checkbox" name="roles" id="roles" value="${it.id}">
              <g:message code="${it.getMessageCode()}"/> 
              <br>
            </g:else>
          </g:each>
        </g:if>
        <g:else>
          <g:each in="${Role.getAll()}">
            <input type="checkbox" name="roles" id="roles" value="${it.id}">
            <g:message code="${it.getMessageCode()}"/>
            <br>
          </g:each>
        </g:else>
      </div>
    </div>    
</sec:ifAnyGranted>