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
<%@ page import="net.ifis.ites.hermes.management.Sample" %>

<label>
    ${message(code:C.SAMPLE_LABEL_CODE)}
    <span class="required-indicator">*</span>
</label>

<small class="error">
    ${message(code:C.DEFAULT_ILLEGAL_SELECTION, args:[message(code:C.SAMPLE_LABEL_CODE)])}
</small>

<select id='sample-multi' name="sample-multi" multiple='multiple' required>
    <g:each in="${Sample.list()}">
        <option value='${it.id}'>${it.name}</option>
    </g:each>
</select>