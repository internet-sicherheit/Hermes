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
<%@ page import="net.ifis.ites.hermes.management.OperatingSystem" %>

<div class="row">
  <div class="large-12 columns">
    <h4> ${message(code:C.OPERATING_SYSTEM_LABEL_CODE)} </h4>
    <g:if test="${!OperatingSystem.list().isEmpty()}">
      <label> 
        <div class="inline">
            <img src="${assetPath(src: 'icons/database_add.png')}" 
               onclick="switchForm(this,
                    '${createLink(
                        url: [
                            controller : 'OperatingSystem', 
                            action : 'template', 
                            params : [name : 'OS',instance : instance?.os]
                        ])}',
                    false,
                    '${assetPath(src: 'icons/database_delete.png')}',
                    $(osForm));" />
          ${message(code:C.OPERATING_SYSTEM_LABEL_CODE)}
        </div>
        <span class="required-indicator">*</span>
      </label>
    
      <div id="osForm">
        <g:select id="os" 
          name="os.id" 
          from="${OperatingSystem.list()}" 
          optionKey="id" 
          required="" 
          value="${instance?.os?.id}"/>
        
        <small class="error">
          ${message(code:C.DEFAULT_FIELD_ERROR_EMPTY_MESSAGE, 
            args:[message(code : C.OPERATING_SYSTEM_LABEL_CODE)])}
        </small>
      </div>
    </g:if>
    <g:else>
        <g:render template="../operatingSystem/OS" model="['instance': instance?.os]" />
    </g:else>
  </div>
</div> 