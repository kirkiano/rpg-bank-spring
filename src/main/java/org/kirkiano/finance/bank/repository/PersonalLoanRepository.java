package org.kirkiano.finance.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.kirkiano.finance.bank.model.loan.PersonalLoan;


/**
 * Data store of personal loans
 */
// @RepositoryRestResource
@Repository
public interface PersonalLoanRepository extends
    JpaRepository<PersonalLoan, Long>
{}