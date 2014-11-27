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

<html>
    <head>
        <title>Jobstatus</title>
        <meta name='layout' content='mainLayout'/>
        <asset:javascript src="manifests/jobstatus.js"/>
        <asset:stylesheet href="manifests/jobstatus.css"/>
        <script>
            $(function() {
                $('#tree').initTree(
                    {
                    url: {
                        init: '${createLink(url: [controller : 'Jobstatus', action : 'renderDynatree'])}',
                        dtInit: '${createLink(url: [controller : 'Jobstatus', action : 'initDataTable'])}',
                        addJobs: '${createLink(url: [controller : 'Jobstatus', action : 'renderFilterJobs'])}',
                        addChild: '${createLink(url: [controller : 'Jobstatus', action : 'renderChildren'])}',
                        showJobDetail: '${createLink(url: [controller : 'Jobstatus', action : 'jobdetail'])}',
                        showLogfile: '${createLink(url: [controller : 'Jobstatus', action : 'detail'])}',
                        allPublishedJob: '${createLink(url: [controller : 'Jobstatus', action : 'allJobs'])}'
                        }
                    }
                ); 
            }); 
        </script>
    </head>
    <body>   
	
        <div class="off-canvas-wrap full-height">
            <div class="inner-wrap full-height">
                <aside class="left-off-canvas-menu">
                    <ul class="off-canvas-list">
                        <li><a id="refreshTreeButton" class="tiny button expand">${message(code:C.DEFAULT_BUTTON_EDIT_LABEL_CODE)}</a></li>
                    </ul>
                    <div id="tree" class="scrollbar"></div>
                </aside>
                
                <section class="main-section full-height">
                    
                    <nav id="filterNav" class="tab-bar border-bottom border-left border-right">
                        <section id="filterSection" class="left-small">
                            <a class="left-off-canvas-toggle" >
                                <span> 
                                    <g:img dir="assets/icons/" file="filter.png" class="filter-icon"/>
                                </span>
                            </a>
                        </section>

                        <section class="middle tab-bar-section">
                            <dl class="sub-nav">
                                <dd class="active"><a href="" class="border-button active" onclick="return showPublishedJobs(this, true);">Filter</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">All</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Active</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Pending</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Published</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Submitted</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Processing</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Success</a></dd>
                                <dd><a href="" class="border-button" onclick="return showPublishedJobs(this, false);">Failure</a></dd>
                            </dl>
                        </section>
                    </nav>
                    
                    <a id="refreshJobsButton" onclick="return refreshPublishedJobs();" class="tiny button expand">${message(code:C.DEFAULT_BUTTON_EDIT_LABEL_CODE)}</a>
                    <div id="publishJobNav" class="panel scrollbar"></div>
                </section>
            
                <a class="exit-off-canvas"></a>
            </div>
        </div>
    </body>
</html>