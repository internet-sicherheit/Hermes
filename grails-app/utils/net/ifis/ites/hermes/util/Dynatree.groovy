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

import grails.converters.JSON

/**
 * Class to create an Dynatree
 * 
 * @author asekulsk
 */
class Dynatree {
    
    /**
     * Root of an Dynatree
     **/
    ArrayList<Dynanode> root
    
    /**
     * Constructor to create an empty dynatree
     **/
    public Dynatree() {
        root = new ArrayList<Dynanode>()
    }
    
    /**
     * Add an Dynanode to the tree
     * 
     * @params node An added Node
     **/
    public void addNode(Dynanode node) {
        root.add(node)
    }
    
    /**
     * Set an child to an node.
     * 
     * @params node The node that contains the child
     * @params child The child that will be append to the node
     * 
     **/
    public void addChildToNode(Dynanode node, Dynanode child) {
        node.addChildren(child)
    }
    
    /**
     * Gets an JSON from the Dynatree.
     **/
    public JSON getJSON() {
        ArrayList json = new ArrayList()
        
        for(node in root) {
            json.add(node.getParams())
        }
        
        return json as JSON
    }
}