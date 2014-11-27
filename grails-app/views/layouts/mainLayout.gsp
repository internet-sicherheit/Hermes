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
        <title><g:layoutTitle default="${message(code:C.SITE_NAME)}"/></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
        
        <asset:javascript src="manifests/core.js"/>
	<asset:stylesheet href="manifests/core.css"/>
        
        <g:external uri="favicon.ico"/>
        <g:layoutHead/>
    </head>

    <body>
        <div id="container" class="panel">
            <sec:ifLoggedIn>
                <g:render template="/common/navbar" />
            </sec:ifLoggedIn>  
            
            <g:layoutBody />

            <sec:ifLoggedIn>
                <div id='footer'>

                    <span>${message(code: C.DEFAULT_FOOTER_LABEL)}</span>
                    
                    <a href='<g:createLink controller="Publishing" action="brewCoffee"/>' class="right coffe"><img src="${assetPath(src: 'icons/coffee.png')}"></a>

                    <span class="right">
                        Version <g:meta name="app.version"/> |
                        Built with Grails <g:meta name="app.grails.version"/> |
                        git-revision <g:meta name="app.gitRevision"/> |
                        git-branch <g:meta name="app.gitBranch"/>
                    </span>
                </div>
            </sec:ifLoggedIn>  
        </div>
    </body>
</html>