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

/**
 * Server error codes class
 *
 * @author Andreas Sekulski
 */
final class ServerErrorCode {
    
    /**
     * OK message code
     **/
    public static final int OK_CODE = 200
    
    /**
     * Not supported error code
     **/
    public static final int NOT_SUPPORTED_CODE = 501
    
    /**
     * Not found code
     **/
    public static final int NOT_FOUND_CODE = 511
    
    /**
     * Data integration code if an delete entity can not be deleted
     **/
    public static final int DATA_INTEGRATION_CODE = 512
    
    /**
     * File exception code
     **/
    public static final int FILE_EXCEPTION_CODE = 513
    
    /**
     * Validation exception code
     **/
    public static final int VALIDATION_EXCEPTION_CODE = 514
    
    /**
     * Usermanagement error code
     **/
    public static final int USERMANAGEMENT_ERROR_CODE = 515
    
    /**
     * Version error code
     **/
    public static final int VERSION_ERROR_CODE = 516
    
    /**
     * Invalid Parameter code
     **/
    public static final int INVALID_ARGUMENT_CODE = 517
        
    /**
     * Unknown server error
     **/
    public static final int UNKNOWN_SERVER_ERROR_CODE = 599
}