package org.kirkiano.finance.bank.exn;


/**
 * Thrown on an attempt to set a negative annual percentage rate
 */
public class NegativeAPRException extends AccountException {
    /**
     * Constructor
     */
    public NegativeAPRException(Double bad) {
        super(bad + "is a negative APR");
        this.value = bad;
    }

    public final Double value;
}