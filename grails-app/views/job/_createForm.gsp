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

<%@ page import="net.ifis.ites.hermes.util.Constants as C" %>

<label for="jobPriority">
    ${message(code:C.JOB_PRIORITY_LABEL_CODE)}
    <span class="required-indicator">*</span>
</label>

<select id="jobPriority" name="jobPriority" required>
    <option value="0">
        ${message(code:C.OPTION_VALUE_LOW_CODE)}
    </option>

    <option value="1">
        ${message(code:C.OPTION_VALUE_MEDIUM_CODE)}
    </option>

    <option value="2">
        ${message(code:C.OPTION_VALUE_HIGH_CODE)}
    </option>
</select>

<div class="large-6 columns">
    <div>
        <label>${message(code:C.JOB_TIMEOUT_LABEL_CODE)}</label>
        <input type="input" 
               id='jobTimeout' 
               name="jobTimeout" 
               value='${instance?.timeout}' 
               placeholder="${message(code:C.DEFAULT_TIME_FORMAT_CODE)}"
               pattern="\d{1,2}:\d{1,2}:\d{1,2}">

        <small class="error">
          ${message(code:C.DEFAULT_FIELD_ERROR_FORMAT_MESSAGE, 
            args:[message(code : C.JOB_TIMEOUT_LABEL_CODE), message(code:C.DEFAULT_TIME_FORMAT_CODE)])}
        </small>
    </div>
    
    <br>

    <div>
        <label>${message(code:C.JOB_CHOOSE_DATE_LABEL_CODE)}</label>

        <input type="input" 
               id="jobChooseDate" 
               name="jobChooseDate" 
               value="<g:formatDate 
               format='${message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)}' 
               date='${instance?.simulatedTime}'/>"
               placeholder="${message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)}">

        <small class="error">
          ${message(code:C.DEFAULT_FIELD_ERROR_FORMAT_MESSAGE, 
            args:[message(code : C.JOB_CHOOSE_DATE_LABEL_CODE), message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)])}
        </small>
    </div>
    
    <br>
    
    <div>
        <label>${message(code:C.DEFAULT_EARLY_START_DATE)}</label>

        <input type="input" 
               id="jobEarlyStartDate" 
               name="jobEarlyStartDate" 
               value="<g:formatDate 
               format='${message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)}' 
               date='${instance?.simulatedTime}'/>"
               placeholder="${message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)}">

        <small class="error">
          ${message(code:C.DEFAULT_FIELD_ERROR_FORMAT_MESSAGE, 
            args:[message(code : C.JOB_CHOOSE_DATE_LABEL_CODE), message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)])}
        </small>
    </div>
</div>

<div class="large-6 columns">
    
    <div>
        <label>${message(code:"job.reboot.label")}</label>
        <input type="input" 
               id='jobReboot' 
               name="jobReboot" 
               value='${instance?.jobReboot}' 
               placeholder="${message(code:C.DEFAULT_TIME_FORMAT_CODE)}"
               pattern="\d{1,2}:\d{1,2}:\d{1,2}">
    </div>
    
    <div>
        <label>
            <g:checkBox name="jobMemoryDump" value="${instance?.memoryDump}" />
            ${message(code:C.JOB_MEMORYDUMP_LABEL_CODE)}
        </label>
        <label>
            <g:checkBox name="jobPublic" value="${!instance?.owner}" />
            <g:message code="job.public.label" />
        </label>
    </div>
</div>