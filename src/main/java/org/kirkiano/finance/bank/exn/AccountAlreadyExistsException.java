package org.kirkiano.finance.bank.exn;

import org.kirkiano.finance.bank.model.CharId;


/**
 * Thrown to forbid a character from having more than one account
 */
public class AccountAlreadyExistsException extends AccountException {
    /**
     * Constructor
     *
     * @param charId ID of the character that already owns an account
     */
    public AccountAlreadyExistsException(CharId charId) {
        super("account already exists for " + charId);
        this.charId = charId;
    }

    /**
     * Account owner's ID
     */
    public final CharId charId;
}