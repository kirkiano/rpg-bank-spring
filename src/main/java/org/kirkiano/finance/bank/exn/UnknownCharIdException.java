package org.kirkiano.finance.bank.exn;

import org.kirkiano.finance.bank.model.CharId;


/**
 * Exception indicating an unrecognized character ID
 */
public class UnknownCharIdException extends AccountException {

    /** Constructor
     *
     * @param charId unrecognized character ID
     */
    public UnknownCharIdException(CharId charId) {
        super("could not find " + charId);
        this.charId = charId;
    }

    /**
     * ID of non-existent account
     */
    public final CharId charId;
}
