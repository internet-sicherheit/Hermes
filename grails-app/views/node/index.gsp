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

<%@ page contentType="text/html;charset=UTF-8" %>
<%! import net.ifis.ites.hermes.util.Constants as C  %> 

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name='layout' content='mainLayout'/>
        <title>${message(code:C.NODE_LABEL_CODE)}</title>
        <asset:javascript src="manifests/node.js"/>
        <asset:stylesheet href="manifests/node.css"/>
        <script> 
            $(document).ready(function(){
                $('#node-container').initNodeView({
                    url: {
                        dtInit: '${createLink(url: [controller : 'Node', action : 'initDataTable'])}',
                        plotter: '${createLink(url: [controller : 'Node', action : 'nodeWorkload'])}',
                        initPlotter: '${createLink(url: [controller : 'Node', action : 'initWorkload'])}'
                    }
                });
            });
        </script>
</head>
<body>
    <div id="message-container"></div>

    <div id="node-container">
        <div id="dt_table" class="panel scrollbar fullheight"></div>
        <div id="node_detail" class="panel scrollbar fullheight">

            <div class="row" data-equalizer>
                <div class="large-3 columns panel" data-equalizer-watch>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns" id="name">
                            Node :
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns" id="state">
                            Nodestate :
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns" id="ip">
                            IP : XXX.XXX.XXX.XXX
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-18 columns" id="hypervisor">
                            Hypervisor :
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns" id="job">
                           Job :
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns" id="sample">
                            Sample :
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns" id="sensor">
                            Sensor :
                        </div>              
                    </div>
                    <hr>
                    <div class="row">
                        <div class="large-16 columns">
                            <a href="" class="tiny button nopadding nomargin" onclick="hidePlotter();return false;">Schließen</a>
                        </div>              
                    </div>
                    <hr>
                </div>
                <div class="large-9 columns panel" data-equalizer-watch>
                    <div id="realchart"></div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>