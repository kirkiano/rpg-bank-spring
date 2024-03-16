package org.kirkiano.finance.bank.model.loan;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.kirkiano.finance.bank.exn.NegativeAPRException;


/**
 * JPA model of personal loan
 */
@Entity(name = "PersonalLoan")
@Table(name = "personal_loan")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonalLoan extends InterestBearingLoan
    implements IPersonalLoan
{

    public static PersonalLoan create(APR apr) throws NegativeAPRException {
        return new PersonalLoan(apr);
    }

    ///////////////////////////////////////////////////////
    // private

    private PersonalLoan(APR apr) throws NegativeAPRException {
        super(apr);
    }

}
