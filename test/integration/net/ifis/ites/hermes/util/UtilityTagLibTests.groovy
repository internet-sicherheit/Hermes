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

package net.ifis.ites.hermes.util

import net.ifis.ites.hermes.util.Constants as C
import org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib

/**
 * Taglib tests for the utility tag libs.
 * 
 * @author Andreas Sekulski
 **/
class UtilityTagLibTests extends GroovyTestCase {
    
    def utilityTagLib = new UtilityTagLib()
    
    def g = new ApplicationTagLib()

    void testHTMLTag() {
        String output
        
        String assertTagA = '<a href="Test">Test</a>'
        String assertTagImg = '<img src="Test">'
        
        output = utilityTagLib.htmlTag(tag :'a',
            attributes : ["href=\"Test\""],
            containsEndTag : true,
            innerHtml : 'Test')
        
        assert output.equals(assertTagA)
        
        output = utilityTagLib.htmlTag(tag :'img',
            attributes : ["src=\"Test\""],
            containsEndTag : false)
        
        assert output.equals(assertTagImg)
        
        try {
            utilityTagLib.htmlTag()
            fail("IllegalArgumentException nicht geworfen") 
        } catch(IllegalArgumentException ie) {
            assert ie.getMessage().equals(g.message(
                code : C.DEFAULT_ILLEGAL_VALUE_CODE, 
                args: ['tag']))
        }   
        
    }
    
    void testCreateBooleanData() {
        String output = utilityTagLib.createBooleanData([value: true])
        assert output.equals(g.message(code: 'default.boolean.true'))
        
        output = utilityTagLib.createBooleanData([value: false])
        assert output.equals(g.message(code: 'default.boolean.false'))

        try {
            utilityTagLib.createBooleanData()
            fail("IllegalArgumentException nicht geworfen") 
        } catch(IllegalArgumentException ie) {
            assert ie.getMessage().equals(g.message(
                code : C.DEFAULT_ILLEGAL_VALUE_CODE, 
                args: ['value']))
        }      
        
        try {
            utilityTagLib.createBooleanData(value: '12345')
            fail("IllegalArgumentException nicht geworfen") 
        } catch(IllegalArgumentException ie) {
            assert ie.getMessage().equals(g.message(
                code : C.DEFAULT_ILLEGAL_VALUE_CODE, 
                args: ['12345']))
        }  
    }
}
