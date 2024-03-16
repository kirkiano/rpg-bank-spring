package org.kirkiano.finance.bank.dto;

import org.springframework.stereotype.Component;

import org.kirkiano.finance.bank.model.loan.PersonalLoan;


/**
 * Converts between a PersonalLoan persistent model and its DTO
 */
@Component
public class PersonalLoanMapper {

    /**
     * Default constructor
     */
    public PersonalLoanMapper() {}

    /**
     * Convert an {@link PersonalLoan} to an {@link PersonalLoanDTO}
     *
     * @param loan PersonalLoan persistent object
     * @return The corresponding data transfer object
     */
    public PersonalLoanDTO toDTO(PersonalLoan loan) {
        return new PersonalLoanDTO(loan.getId(),
                                   loan.getAPR());

    }
}