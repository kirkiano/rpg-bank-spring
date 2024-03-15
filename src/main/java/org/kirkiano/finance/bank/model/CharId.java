package org.kirkiano.finance.bank.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Embeddable;


/**
 * Character ID newtype
 */
@Embeddable
public class CharId implements Cloneable, Serializable {

    /**
     * Default constructor
     */
    protected CharId() {}


    /**
     * Constructor
     *
     * @param val ID value
     */
    public CharId(long val) {
        this.val = val;
    }

    ///////////////////////////////////////////////////////
    // instance methods

    public String toString() {
        return "CID:" + this.val;
    }

    public boolean equals(Object that) {
        return that instanceof CharId && ((CharId) that).val == this.val;
    }

    public int hashCode() {
        return Long.valueOf(this.val).hashCode();
    }

    /**
     * Get the value of this character ID. Ordinarily this would be called
     * {@code getValue()}, but this type's previous implementation was in
     * terms of a Java record, whose getter was of course {@code value()}.
     * This past method name is retained here, because the record-approach
     * may become advantageous again in future.
     *
     * @return raw ID value
     */
    // for compatibility with a possible future (and past) implementation
    // using a Java record
    public long value() {
        return this.val;
    }

    @Override
    public CharId clone() {
        try {
            return (CharId) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Failed to clone " + this);
        }
    }

    /**
     * Destructively increments the value of this ID
     *
     * @return self, for easy chaining
     */
    public CharId increment() {
        this.val++;
        return this;
    }

    ///////////////////////////////////////////////////////
    // protected

    /**
     * Raw character ID value
     */
    @JsonValue
    private long val;

}