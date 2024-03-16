package org.kirkiano.finance.bank.model.loan;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.kirkiano.finance.bank.exn.NegativeAPRException;


@Getter
@MappedSuperclass
@NoArgsConstructor
public class InterestBearingLoan extends Loan implements IPersonalLoan {

    public static InterestBearingLoan create(APR apr) throws NegativeAPRException {
        return new InterestBearingLoan(apr);
    }

    public APR getAPR() {
        return this.apr;
    }

    ///////////////////////////////////////////////////////
    // private

    InterestBearingLoan(APR apr) throws NegativeAPRException {
        apr.assertValid();
        this.apr = apr;
    }

    @Convert(converter = APRDoubleConverter.class)
    @Column(nullable = false)
    protected APR apr;

}
