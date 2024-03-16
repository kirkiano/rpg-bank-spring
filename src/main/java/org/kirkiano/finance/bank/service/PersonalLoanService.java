package org.kirkiano.finance.bank.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.kirkiano.finance.bank.exn.*;
import org.kirkiano.finance.bank.model.loan.APR;
import org.kirkiano.finance.bank.model.loan.PersonalLoan;
import org.kirkiano.finance.bank.repository.PersonalLoanRepository;


/**
 * Account service
 */
@Slf4j
@Service
public class PersonalLoanService {

    /**
     * Constructor
     *
     * @param loanRepo personal loan repo (an injected dependency).
     */
    @Autowired
    public PersonalLoanService(PersonalLoanRepository loanRepo) {
        this.loanRepo = loanRepo;
    }


    /**
     * Create a personal loan
     *
     * @param apr annual percentage rate
     *
     * @return PersonalLoan
     */
    public PersonalLoan createPersonalLoan(APR apr)
        throws NegativeAPRException
    {
        try {
            var loan = PersonalLoan.create(apr);
            this.loanRepo.save(loan);
            return loan;
        }
        catch (NegativeAPRException ex) {
            log.warn("Attempted to create personal loan with negative APR {}",
                      apr);
            throw ex;
        }
    }

    /**
     * Retrieve all personal loans
     *
     * @return the loans
     */
    public List<PersonalLoan> getAllPersonalLoans() {
        return this.loanRepo.findAll();
    }


    ///////////////////////////////////////////////////////
    // private

    private final PersonalLoanRepository loanRepo;
}