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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.ifis.ites.hermes

import org.springframework.context.MessageSource
import org.springframework.context.support.ResourceBundleMessageSource

/**
 *
 * @author asekulsk
 */
final class InitMessageSource {
    public static MessageSource getI18n() {
        URL url = new File('grails-app/i18n').toURI().toURL()
        def messageSource = new ResourceBundleMessageSource()
        messageSource.bundleClassLoader = new URLClassLoader(url)
        messageSource.basename = 'messages'
        return messageSource
    }
}