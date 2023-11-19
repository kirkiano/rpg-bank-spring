package org.kirkiano.rpg.bank.exn;

import org.kirkiano.rpg.bank.model.CharId;


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
