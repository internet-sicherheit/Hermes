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

<div class="large-6 columns border scrollbar job">
    <h3 class="text-center"> ${job.name} </h3>
    <ul class="list-style-none">
        <li>${message(code:C.JOB_CHOOSE_DATE_LABEL_CODE)} : <g:formatDate format='${message(code:C.DEFAULT_DATE_TIME_FORMAT_CODE)}' date='${job.simulatedTime}'/></li>
        <li>${message(code:C.JOB_TIMEOUT_LABEL_CODE)} : ${job.timeout}</li>
        <li>${message(code:C.JOB_MEMORYDUMP_LABEL_CODE)} : ${job.memoryDump}</li>
    </ul>
</div>
<div class="large-6 columns border scrollbar sensor">
    <h3 class="text-center"> ${job.sensor?.name} </h3>
    <ul class="list-style-none">
        <li>${message(code:C.DEFAULT_DESCRIPTION_LABEL_CODE)} : ${job.sensor?.description}</li>
        <li>${message(code:C.DEFAULT_TYPE_LABEL_CODE)} : ${job.sensor?.type}</li>
        <li>${message(code:C.DEFAULT_FILENAME_LABEL_CODE)} : ${job.sensor?.originalFilename}</li>
        <li>${message(code:C.DEFAULT_MD5_LABEL)} : ${job.sensor?.md5}</li>
    </ul>
</div>

<div class="large-12 columns border scrollbar sample"><h3 class="text-center"> ${job.sample.name} </h3>
    <ul class="list-style-none">
        <li>${message(code:C.DEFAULT_DESCRIPTION_LABEL_CODE)} : ${job.sample.description}</li>
        <li>${message(code:C.DEFAULT_TYPE_LABEL_CODE)} : ${job.sample.fileContentType}</li>
        <li>${message(code:C.DEFAULT_FILENAME_LABEL_CODE)} : ${job.sample.originalFilename}</li>
        <li>${message(code:C.DEFAULT_FILETYPE_LABEL_CODE)} : ${job.sample.fileExtension}</li>
        <li>${message(code:C.DEFAULT_MD5_LABEL)} :<br>${job.sample.md5}</li>
        <li>${message(code:C.DEFAULT_SHA1_LABEL)} :<br>${job.sample.sha1}</li>
        <li>${message(code:C.DEFAULT_SHA256_LABEL)} :<br>${job.sample.sha256}</li>
        <li>${message(code:C.DEFAULT_SHA512_LABEL)} :<br>${job.sample.sha512}</li>
    </ul>
</div>