package org.kirkiano.rpg.bank.dto;

import org.kirkiano.rpg.bank.model.CharId;
import org.kirkiano.rpg.bank.model.Money;


/**
 * Data transfer object returned by server to represent an existing account
 *
 * @param id Account ID
 * @param charId Owner's ID
 * @param balance Current balance
 */
public record AccountDTO(long id,
                         CharId charId,
                         Money balance) {}