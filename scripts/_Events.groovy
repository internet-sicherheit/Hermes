/*
 * Copyright (c) 2013, 2014 Institute for Internet Security - if(is)
 *
 * This file is part of Hermes Malware Analysis System.
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl 5
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

eventCompileStart = {


    String gitBranches = (/git branch/.execute().text)
    String gitActiveBranch
    gitBranches.eachLine { line ->
        if (line.startsWith("*")) {
            gitActiveBranch = line.substring(2)
        }
    }

    metadata.'app.gitRevision' = (/git rev-parse HEAD/.execute().text)
    metadata.'app.gitBranch' = gitActiveBranch
    metadata.'app.buildDate' = new Date().format("MM/dd/yyyy HH:mm:ss")
    metadata.'app.buildProfile' = grailsEnv
    metadata.persist()
}
