package org.kirkiano.rpg.bank.dto;

import org.kirkiano.rpg.bank.model.Money;


/**
 * Data transfer object returned by server to inform client of account's
 * current balance
 *
 * @param balance Balance value
 */
public record BalanceDTO(Money balance) {}