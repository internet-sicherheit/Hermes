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

<div class="row">
  <div class="large-12 columns">
    <label>
      ${message(code:C.DEFAULT_NAME_LABEL_CODE)}
      <span class="required-indicator">*</span>
    </label>
    <input type="text" id='vmName' name="vmName" value='${instance?.name}' required>
    <small class="error">
      ${message(code:C.DEFAULT_FIELD_ERROR_EMPTY_MESSAGE, args:[message(code : C.DEFAULT_NAME_LABEL_CODE)])}
    </small>
  </div>
</div>

<div class="row">
  <div class="large-12 columns">
    <label>
      ${message(code:C.DEFAULT_DESCRIPTION_LABEL_CODE)}
    </label>
    <input type="text" id='vmDescription' name="vmDescription" value='${instance?.description}'>
  </div>
</div>

<div class="row">
    <div id="fileUploaderVM" class="large-12 columns">
        
        <div class="row">
            <div class="large-4 columns">${message(code:C.DEFAULT_FILENAME_LABEL_CODE)} : <span id="fileVMname">${instance?.originalFilename}</span></div>
            <div class="large-4 columns">${message(code:C.DEFAULT_FILESIZE_LABEL)} : <span id="fileVMsize"></span></div>
            <div class="large-4 columns">${message(code:C.DEFAULT_FILETYPE_LABEL_CODE)} : <span id="fileVMtype"></span></div>
        </div>
        
        <label for="fileVM">
            ${message(code:C.DEFAULT_FILE_UPLOAD_LABEL_CODE)}
            <g:if test="${dataUploadRequired}">
                <span class="required-indicator"> *</span>
            </g:if>
        </label>
        <g:if test="${dataUploadRequired}">
            <input name="fileVM" id="fileVM" type="file" required="required">
        </g:if>
        <g:else>
            <input name="fileVM" id="fileVM" type="file">
        </g:else>
        
        <small class="error">
            ${message(code:C.DEFAULT_FIELD_ERROR_EMPTY_MESSAGE, args:[message(code:C.DEFAULT_FILE_UPLOAD_LABEL_CODE)])}
        </small>
    </div>
</div>

<script>
    $(document).ready(function() {
        $('#fileUploaderVM').uploader({
            id: "#fileVM"
        });
    });
</script>

<g:render template="switchOS" />
<g:render template="switchHypervisor" />