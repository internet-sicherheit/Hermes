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

<asset:javascript src="manifests/views/job/create.js"/>

<form data-abide onsubmit="return $(this).sendFormData({url : '${url}'});">    
    <g:hiddenField name="jobID" value="${instance?.id}" />
    <g:hiddenField name="jobVersion" value="${instance?.version}" />
    
    <h1 class='text-center'>
        ${formTitel}
    </h1>
    
    <div class="row" data-equalizer>
        <div class="large-6 columns panel" data-equalizer-watch>
            <g:render template="createForm" />
        </div>
            
        <div class="large-6 columns panel" data-equalizer-watch>
            <g:render template="multiselectHypervisorVM" />
        </div>
    </div>
        
    <div class="row" data-equalizer>
        <div class="large-6 columns panel" data-equalizer-watch>
            <g:render template="multiselectSample" />
        </div>
        <div class="large-6 columns panel" data-equalizer-watch>        
            <g:render template="multiselectSensor" />
        </div>
    </div>

    <g:render template="../administration/uploader" />    
    
    <div class="row">
        <ul class="button-group">
            <li><button type="submit">${buttonTitle}</button></li>           
            <li><a href="#" onclick="switchAnimation($('#' + adminSettings.formContainer), $('#' + adminSettings.adminContainer));" class="button">${message(code:C.DEFAULT_BUTTON_CLOSE_LABEL_CODE)}</a></li>
        </ul>
    </div>
</form>