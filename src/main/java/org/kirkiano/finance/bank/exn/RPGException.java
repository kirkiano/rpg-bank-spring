package org.kirkiano.finance.bank.exn;


/**
 * Base class of all application exceptions
 */
public abstract class RPGException extends Exception {
    /**
     * Constructor
     * @param msg error message
     */
    RPGException(String msg) {
        super(msg);
    }
}