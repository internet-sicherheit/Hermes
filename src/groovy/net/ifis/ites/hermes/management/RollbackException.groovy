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
 * A RollbackException should be used to initiate the rollback of a transaction.
 * It contains the object that was the main actor of the transaction so the controller
 * is able to process its params.
 *
 * @author Kevin Wittek
 */
abstract class RollbackException extends RuntimeException {

    /** The main object that was part of the transaction. */
    def transactionObject

    RollbackException(String msg, object) {
        super(msg)
        transactionObject = object
    }


    RollbackException(String msg, Throwable t, object) {
        super(msg, t)
        transactionObject = object
    }


}
