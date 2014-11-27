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

import java.lang.StringBuffer
import net.ifis.ites.hermes.util.Constants as C

/**
 * Utility Tag lib to generate html tags, fileuploader or boolean values etc.
 *
 * @author Andreas Sekulski
 **/
class UtilityTagLib {
    
    /**
     * Tag to generate all html code.
     * 
     * @param tag The HTML Tag
     * @param containsEndTag (Optional) Indicator for an Endtag (True) else 
     * without (False). Default True
     * @param attributes (Optional) Attribute parameter as an List from the 
     * HTML Tag
     * @param innerHtml (Optional) The inner html text from an tag
     * 
     * @throws IllegalArgumentException if tag attribute is null
     * 
     * @return an generated HTML Tag
     **/
    def htmlTag = { attrs ->
        String startTag
        String endTag
        
        boolean containsEndTag = attrs.containsEndTag
        String tag = attrs.tag
        String innerHtml = attrs.innerHtml
        List attributes = attrs.attributes
        
        if(attrs.tag == null) {
            throw new IllegalArgumentException(
                message(code: C.DEFAULT_ILLEGAL_VALUE_CODE, args:['tag']))
        }
        
        if(attrs.containsEndTag == null) {
            containsEndTag = true
        }
        
        startTag = '<' + tag
        endTag = '</' + tag + '>'
        
        for (attribute in attributes) {
            startTag += ' ' + attribute
        }
        
        startTag += '>'
        
        if(containsEndTag) {
            out << startTag + innerHtml + endTag
        } else  {
            out << startTag
        }
    }
    
    /**
     * Tag to generate boolean data information. 
     *  
     * @param value attribute to set boolean true or false
     * 
     * @throws IllegalArgumentException if value attribute is null or not
     * true or false
     * 
     * @return an generated HTML true or false tag
     **/
    def createBooleanData = { attrs ->
        
        if(attrs.value == null) {
            throw new IllegalArgumentException(
                message(code: C.DEFAULT_ILLEGAL_VALUE_CODE, args:['value']))
        }
        
        if(!(attrs.value instanceof Boolean)) {
            throw new IllegalArgumentException(
                message(
                    code: C.DEFAULT_ILLEGAL_VALUE_CODE, args:[attrs.value]))
        }
        
        boolean isEntityTrue = attrs.value
        
        if(isEntityTrue) {
            out << message(code: 'default.boolean.true')
        } else {
            out << message(code: 'default.boolean.false')
        }  
    }
}