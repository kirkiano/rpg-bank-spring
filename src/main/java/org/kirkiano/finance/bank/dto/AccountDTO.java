package org.kirkiano.finance.bank.dto;

import org.kirkiano.finance.bank.model.CharId;
import org.kirkiano.finance.bank.model.Money;


/**
 * Data transfer object representing an account
 *
 * @param id Account ID
 * @param charId Owner's ID
 * @param balance Current balance
 */
public record AccountDTO(long id,
                         CharId charId,
                         Money balance) {}