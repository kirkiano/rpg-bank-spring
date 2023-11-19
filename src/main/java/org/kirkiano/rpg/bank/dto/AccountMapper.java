package org.kirkiano.rpg.bank.dto;

import org.springframework.stereotype.Component;

import org.kirkiano.rpg.bank.model.Account;


/**
 * A converter between model objects and data transfer objects
 */
@Component
public class AccountMapper {

    /**
     * Default constructor
     */
    public AccountMapper() {}

    /**
     * Convert an {@link Account} to an {@link AccountDTO}
     *
     * @param account The Account model instance
     * @return The corresponding data transfer object
     */
    public AccountDTO toDTO(Account account) {
        return new AccountDTO(account.getId(),
                              account.getCharId(),
                              account.getBalance());
    }
}