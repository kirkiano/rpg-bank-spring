package org.kirkiano.rpg.bank.exn;


/**
 * Exception indicating a non-existent account
 */
public class NoSuchAccountIdException extends AccountException {

    /** Constructor
     *
     * @param id ID of non-existent account
     */
    public NoSuchAccountIdException(long id) {
        super("invalid account ID: " + id);
        this.id = id;
    }

    /**
     * ID of non-existent account
     */
    public final long id;
}
