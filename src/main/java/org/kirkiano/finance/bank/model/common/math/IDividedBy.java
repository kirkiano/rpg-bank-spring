package org.kirkiano.finance.bank.model.common.math;


public interface IDividedBy<T, THIS extends IDividedBy<T, THIS>> {
    THIS dividedBy(T other);
}
