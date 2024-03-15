package org.kirkiano.finance.bank.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Money type
 */
public class Money extends Number implements Serializable {

    /**
     * Instance representing zero {@link Money}
     */
    public static final Money ZERO = Money.from(0);

    /**
     * Make money out of an amount.
     *
     * @param val amount
     * @return Money
     */
    public static Money from(long val) {
        return new Money(val);
    }

    /**
     * Adds two {@link Money}s, leaving them unmutated.
     *
     * @param m1 First {@link Money}
     * @param m2 Second {@link Money}
     * @return Sum {@link Money}
     */
    public static Money add(Money m1, Money m2) {
        return Money.from(m1.longValue() + m2.longValue());
    }

    ///////////////////////////////////////////////////////
    // toString, equals, hashCode

    @Override
    public String toString() {
        return "$" + this.val;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Money && val == ((Money) other).val;
    }

    ///////////////////////////////////////////////////////
    // instance methods

    @Override
    public int intValue() {
        return (int) val;
    }

    @Override
    public long longValue() {
        return val;
    }

    @Override
    public float floatValue() {
        return (float) val;
    }

    @Override
    public double doubleValue() {
        return (double) val;
    }

    /**
     * Indicates negativity
     *
     * @return whether the amount is negative
     */
    public boolean isNegative() {
        return this.val < 0;
    }

    /////////////////////////////////////////////////////
    // private

    private Money(long val) {
        this.val = val;
    }

    /**
     * Amount of this {@link Money}. Constitutes the JSON value.
     */
    @JsonValue
    private final long val;
}