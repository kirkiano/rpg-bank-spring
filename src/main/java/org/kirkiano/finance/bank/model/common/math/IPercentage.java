package org.kirkiano.finance.bank.model.common.math;


public interface IPercentage extends IDoubleValue {

    public default String toPercentString() {
        return 100 * this.doubleValue() + "%";
    }

}
