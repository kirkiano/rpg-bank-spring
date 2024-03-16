package org.kirkiano.finance.bank.model.common.math;


public interface IMultiply<T, SELF extends IMultiply<T, SELF>> {

    SELF multiply(T other);
}
