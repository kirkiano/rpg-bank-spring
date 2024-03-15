package org.kirkiano.finance.bank.model;

import java.text.MessageFormat;

import jakarta.persistence.*;
import lombok.*;

import org.kirkiano.finance.bank.config.Constants;
import org.kirkiano.finance.bank.exn.NegativeBalanceException;


/**
 * JPA model of bank account
 */
@Entity(name = "Account")
@Table(name = "account")
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Account extends BaseEntity {

    /**
     * Instantiate
     *
     * @param charId Owner's ID
     * @param balance Initial balance
     * @return The account
     * @throws NegativeBalanceException Guards against a negative initial balance
     */
    public static Account create(CharId charId, Money balance)
        throws NegativeBalanceException
    {
        if (balance.isNegative()) throw new NegativeBalanceException();
        return new Account(charId, balance);
    }

    ///////////////////////////////////////////////////////
    // toString, hashCode, equality

    public String toString() {
        String fmt = "Account ID {0} for {1} with balance {2}";
        return MessageFormat.format(
            fmt, this.getId(), this.getCharId(), this.getBalance());
    }

    public boolean equals(Object that) {
        if (!(that instanceof Account other)) return false;
        return this.getId().equals(other.getId()) &&
            this.getCharId().equals(other.getCharId()) &&
            this.getBalance().equals(other.getBalance());
    }

    public int hashCode() {
        return this.getId().hashCode();
    }

    ///////////////////////////////////////////////////////
    // mutation

    /**
     * Change the account balance.
     *
     * @param delta The requested change. A negative value indicates withdrawal.
     * @return the same account (enables method chaining)
     * @throws NegativeBalanceException Guards against overdraft
     */
    public Account changeBalance(Money delta)
        throws NegativeBalanceException
    {
        var newBalance = Money.add(this.balance, delta);
        if (newBalance.isNegative()) throw new NegativeBalanceException();
        else this.balance = newBalance;
        return this;
    }

    ///////////////////////////////////////////////////////
    // private

    private Account(CharId charId, Money balance) {
        this.charId = charId;
        this.balance = balance;
    }


    @Convert(converter = CharIdLongConverter.class)
    @Column(name = "cid",
            nullable = false,
            unique = true)
    private CharId charId;


    @Convert(converter = MoneyLongConverter.class)
    @Column(name = Constants.BALANCE_KEY,
            nullable = false)
    private Money balance;

}
