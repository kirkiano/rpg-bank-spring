package org.kirkiano.finance.bank.exn;


/**
 * Thrown on an attempt to give an account a negative balance.
 */
public class NegativeBalanceException extends AccountException {
    /**
     * Constructor
     */
    public NegativeBalanceException() {
        super("negative balance");
    }
}
