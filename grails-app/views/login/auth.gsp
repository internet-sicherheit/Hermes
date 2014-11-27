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
<!DOCTYPE html>
<html>
  <head>
    <meta name='layout' content='mainLayout'/>
    <title>${message(code:C.SITE_NAME)}</title>
    <asset:javascript src="manifests/login.js"/>
    <asset:stylesheet href="manifests/login.css"/>
  </head>
  <body>
      <div id="login-Container" class='panel'>
        <h1 class='text-center'>${g.message(code:C.SITE_NAME)}</h1>

        <form method='POST' onsubmit="$(this).login();return false;" action="${request.contextPath}/j_spring_security_check" autocomplete='off'>
          <div class='row'>
            <div class='small-9 columns large-centered'>
              <input type='text' name='j_username' placeholder='${g.message(code:C.DEFAULT_USERNAME_LABEL)}' autofocus='true'>
            </div>
          </div>

          <div class='row'>
            <div class='small-9 columns large-centered'>
              <input type='password' name='j_password' id='password' placeholder='${g.message(code:C.DEFAULT_USER_PASSWORD_LABEL)}'>
            </div>
          </div>

          <div class='row'>
            <div class='small-9 columns large-centered'>
              <ul class='button-group even-2'>
                <li class='text-center login'>
                  <input type='checkbox' name='${rememberMeParameter}' id='remember_me'>
                  ${g.message(code:C.DEFAULT_USER_REMEMBER_ME)}
                </li>
                <li>
                  <button type="submit">${g.message(code:C.DEFAULT_LOGIN_LABEL)}</button>                    
                </li>
              </ul>
            </div>
          </div>   
        </form>
     </div>
  </body>
</html>