package org.kirkiano.finance.bank.model.loan;

import org.kirkiano.finance.bank.common.AssertValid;
import org.kirkiano.finance.bank.exn.NegativeAPRException;
import org.kirkiano.finance.bank.model.common.math.IDividedBy;
import org.kirkiano.finance.bank.model.common.math.IDoubleValue;
import org.kirkiano.finance.bank.model.common.math.IMultiply;
import org.kirkiano.finance.bank.model.common.math.IPercentage;


/**
 * Annual percentage rate.
 */
public class APR implements
    AssertValid<NegativeAPRException>,
    IDoubleValue,
    IPercentage,
    IMultiply<Double, APR>,
    IDividedBy<Integer, APR>
{
    /**
     * See `from` for a safer constructor
     * @param value NOT multiplied by 100. E.g., 0.1 means 10%.
     */
    public APR(Double value) {
        this.value = value;
    }

    /**
     * This is the safer way to construct an APR
     *
     * @param value NOT multiplied by 100. E.g., 0.1 means 10%.
     * @return APR
     * @throws NegativeAPRException if value < 0.0
     */
    public static APR from(Double value) throws NegativeAPRException {
        final var apr = new APR(value);
        apr.assertValid();
        return apr;
    }

    public void assertValid() throws NegativeAPRException {
        if (value < 0.0) throw new NegativeAPRException(value);
    }

    public Double doubleValue() {
        return this.value;
    }

    public APR multiply(Double other) {
        return new APR(this.value * other);
    }

    public APR dividedBy(Integer other) {
        return new APR(this.value / other);
    }

    private final Double value;

}
