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

grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.war.file = "target/${appName}.war"

grails.project.fork = [
    //test: false,
    test: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 128], // configure settings for the test-app JVM
    run: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 128], // configure settings for the run-app JVM
    war: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 128], // configure settings for the run-war JVM
    console: [maxMemory: 256, minMemory: 64, debug: false, maxPerm: 128]// configure settings for the Console UI JVM
]

disable.auto.recompile=false // Recompilation of Java Sources
grails.gsp.enable.reload=true // Recompilation of GSPs
grails.reload.enabled // Enable agent reloading of class files (disabled by default on 2.3)

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.
        
        // Grails 2.3.7 has no environment support in BuildConfig, so we need both databases
        runtime 'postgresql:postgresql:9.1-901.jdbc4'
        runtime 'hsqldb:hsqldb:1.8.0.10'
    }

    plugins {    
        // plugins for the build system only
        build ':tomcat:7.0.54'
        
        // plugins for the compile step
        compile ":build-test-data:2.2.2"
        compile ":cache:1.1.8"
        compile ":quartz:1.0.2"
        compile ":spring-security-core:2.0-RC4"
        
        
        // plugins needed at runtime but not for compilation
        runtime ":hibernate:3.6.10.16" // or ':hibernate4:4.3.5.2'
        runtime ":database-migration:1.4.0"
        runtime ":jquery:1.11.1"
        compile ":asset-pipeline:1.9.6"

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.5"
        //test ":code-coverage:1.2.6"
    }
}
