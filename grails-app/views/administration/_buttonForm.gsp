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
        <div id="sendData">
            <button class="small" type="submit">
            ${buttonTitle}
            </button>
            <button class="small" type="button" onclick="switchAnimation($('#' + adminSettings.formContainer), $('#' + adminSettings.adminContainer));">
                ${message(code:C.DEFAULT_BUTTON_CLOSE_LABEL_CODE)}
            </button>
        </div>
        <div id="uploadButtons" class="hide">
            <button class="small" type="button" onclick="return abortXMLRequest()">
            Upload Abbrechen
            </button>
        </div>  
    </div>
</div>

