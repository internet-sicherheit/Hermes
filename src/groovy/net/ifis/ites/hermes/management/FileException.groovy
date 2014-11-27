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

package net.ifis.ites.hermes.management

/**
 * Represents exceptions which are thrown if errors occur during file operations.
 *
 * Extends RuntimeException so it can be used to automatically rollback transactions.
 * Might contain an object instance for displaying errors or further processing.
 *
 * @author Kevin Wittek
 */
class FileException extends RollbackException {

    FileException(String msg, object) {
        super(msg, object)
    }

    FileException(String msg, Throwable t, object) {
        super(msg, t, object)
    }

}
