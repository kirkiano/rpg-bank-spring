package org.kirkiano.finance.bank.dto;

import org.kirkiano.finance.bank.model.loan.APR;


/**
 * Data transfer object representing a personal loan
 *
 * @param id ID of the personal loan
 * @param apr annual percentage rate
 */
public record PersonalLoanDTO(long id,
                              APR apr)
{}