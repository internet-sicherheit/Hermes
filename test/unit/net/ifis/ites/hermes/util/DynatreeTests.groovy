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
 * Test class for an dynatree.
 * 
 * @author Andreas Sekulski
 */
@TestMixin(GrailsUnitTestMixin)
class DynatreeTests {

    void testEmptyDynatree() {
        Dynatree tree = new Dynatree()
        assert tree.root.size() == 0
    }
    
    void testAddNodeToDynatree() {
        Dynatree tree = new Dynatree()
        Dynanode node = new Dynanode("key", "value", null, null, null, null)
        tree.addNode(node)
        assert tree.root.size() == 1
    }
    
    void testAddChildToNodeDynatree() {
        Dynatree tree = new Dynatree()
        Dynanode node = new Dynanode("key", "value", null, null, null, null)
        Dynanode child = new Dynanode("key2", "value2", null, null, null, null)
        tree.addNode(node)
        tree.addChildToNode(node, child)
        assert tree.root.size() == 1
        Dynanode referenceNode = tree.root.get(0)
        assert referenceNode.childrens.size() == 1
        assert referenceNode.childrens.get(0).equals(child)
    }
}