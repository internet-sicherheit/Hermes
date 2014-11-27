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
 * Class to create an Dynanode for an Dynatree
 *
 * @author Andreas Sekulski
 */
class Dynanode {
    
    /**
     * Key (ID) from an Node
     **/
    String key
    
    /**
     * Value from an Node
     **/
    String value
    
    /**
     * If Node is Selected (checked) true or false
     **/
    Boolean isSelected
    
    /**
     * If Node should be displayed as an folder true or not false
     **/
    Boolean isFolder
    
    /**
     * If Node is an lazy node that data should be get from an ajax call
     **/
    Boolean isLazy
    
    /**
     * If Node contains childrens that should be draw
     **/
    List<Dynanode> childrens
    
    /**
     * Indicator for an key value from an dynatree in an json
     **/
    private static final String DYNA_KEY = 'key'
    
    /**
     * Indicator for an title (value) from an dynatree in an json
     **/
    private static final String DYNA_TITLE = 'title'
    
    /**
     * Indicator if node is selected or not in an json
     **/
    private static final String DYNA_IS_SELECTED = 'select'
    
    /**
     * Indicator if node is an folder for css in an json
     **/
    private static final String DYNA_IS_FOLDER = 'isFolder'
    
    /**
     * Indicator if node contains lazy loading or not in an json
     **/
    private static final String DYNA_IS_LAZY = 'isLazy'
    
    /**
     * Indicator if node contains an children in an json
     **/
    private static final String DYNA_CHILDREN = 'children'
    
    /**
     * Indicator if node should be expand in an json
     **/
    private static final String DYNA_EXPAND = 'expand'
    
    /**
     * Constructor to create an Dynanode.
     * 
     * Optional construct params can be null.
     * 
     * @params key - keyname (id) from this node
     * @params value - valure from node
     * @params isSelected - node is selected (true) or not (false) (Optional)
     * @params isFolder - node is an folder container (true) or not (false) (Optional)
     * @params isLazy - node contains lazy loading (true) or not (false) (Optional)
     * @params childrens - if node contains any children (Optional)
     * 
     **/
    public Dynanode(String key, String value , Boolean isSelected, Boolean isFolder, Boolean isLazy, List childrens) {       
        if(key) {
            this.key = key
        }
        
        if(value) {
            this.value = value
        }
        
        if(isSelected) {
            this.isSelected = isSelected
        }
        
        if(isFolder) {
            this.isFolder = isFolder
        }
        
        if(isLazy) {
            this.isLazy = isLazy
        }
        
        if(childrens) {
            this.childrens = childrens
        } else {
            this.childrens = new ArrayList()
        }
    }
    
    
    /**
     * Get the parameters from an dynanode as map.
     * 
     * @return the parameters from an dynanode.
     **/
    public Map getParams() {
        Map parameters = new HashMap()
        
        if(this.key) {
            parameters.put(DYNA_KEY, this.key)
        }
        
        if(this.value) {
            parameters.put(DYNA_TITLE, this.value)
        }
        
        if(this.isSelected) {
            parameters.put(DYNA_IS_SELECTED, this.isSelected)
        }
        
        if(this.isFolder) {
            parameters.put(DYNA_IS_FOLDER, this.isFolder)
        }
        
        if(this.isLazy) {
            parameters.put(DYNA_IS_LAZY, this.isLazy)
        } else {
            parameters.put(DYNA_EXPAND, true)
        }
        
        if(this.childrens) {
            List childrenParams = new ArrayList()
            for(children in childrens) {
               childrenParams.add(children.getParams())
            }
            parameters.put(DYNA_CHILDREN, childrenParams) 
        }
        
        return parameters
    }	
    
    /**
     * Method to add an child to the node
     * 
     * @params node an node that should be append as an children element
     */
    public void addChildren(Dynanode node) {
        this.childrens.add(node)
    }
}