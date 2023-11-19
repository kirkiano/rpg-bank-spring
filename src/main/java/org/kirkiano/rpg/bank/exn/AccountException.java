package org.kirkiano.rpg.bank.exn;


/**
 * Abstract exception regarding an account
 */
public abstract class AccountException extends RPGException {
    AccountException(String message) {
        super("Account exception: " + message);
    }
}