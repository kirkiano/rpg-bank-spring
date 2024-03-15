package org.kirkiano.finance.bank.dto;

import org.kirkiano.finance.bank.model.Money;


/**
 * Data transfer object returned by server to inform client of account's
 * current balance
 *
 * @param balance Balance value
 */
public record BalanceDTO(Money balance) {}