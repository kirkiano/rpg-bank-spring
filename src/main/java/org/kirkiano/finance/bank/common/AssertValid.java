package org.kirkiano.finance.bank.common;


/**
 * Objects that can assert that they are valid
 * @param <E>
 */
public interface AssertValid<E extends Throwable> {
    void assertValid() throws E;
}
