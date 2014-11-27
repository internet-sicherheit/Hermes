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

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

/**
 * 
 * Test class for data container dynanode.
 * 
 * @author Andreas Sekulski
 */
@TestMixin(GrailsUnitTestMixin)
class DynanodeTests {
    
    private static final String KEY = "Key"
    
    private static final String VALUE = "Value"
    
    void testEmptyDynanode() {
        Dynanode node = new Dynanode(KEY, VALUE, null, null, null, null)
        assert node.key == KEY
        assert node.value == VALUE
        assert node.childrens.size() == 0
        assert node.isFolder == null
        assert node.isLazy == null
        assert node.isSelected == null
    }
    
    void testFillDynanode() {
        Dynanode node = new Dynanode(KEY, VALUE, true, true, true, new ArrayList())
        assert node.key == KEY
        assert node.value == VALUE
        assert node.childrens.size() == 0
        assert node.isFolder == true
        assert node.isLazy == true
        assert node.isSelected == true
    }
}
