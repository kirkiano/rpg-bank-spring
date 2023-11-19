package org.kirkiano.rpg.bank.dto;

import jakarta.validation.constraints.NotNull;

import org.kirkiano.rpg.bank.model.Money;


/**
 * Data transfer object submitted by client to request a change
 * to a bank account's balance
 *
 * @param delta The requested change. A negative number means withdrawal.
 */
public record ChangeBalanceDTO(@NotNull Money delta) {}