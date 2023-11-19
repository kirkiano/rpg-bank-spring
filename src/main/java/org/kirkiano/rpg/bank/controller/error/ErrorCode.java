package org.kirkiano.rpg.bank.controller.error;

import lombok.Getter;


/**
 * Error codes to be included in HTTP REST error responses
 */
@Getter
public enum ErrorCode {

    /**
     * Unspecific error
     */
    General(0, "General error"),

    /**
     * Client's request could not be parsed
     */
    Unparseable(20, "Unparseable"),

    /**
     * Client's request referred to an unknown property
     */
    UnknownProperty(100, "Unknown property"),

    /**
     * Client's request contained a type error
     */
    TypeError(110, "Type error"),

    /**
     * Client tried to create a second account for a character
     */
    AccountAlreadyExists(1000, "Account already exists"),

    /**
     * Client requested information about a nonexistent account
     */
    NoSuchAccountId(1001, "No such account ID"),

    /**
     * Client omitted a required {@link org.kirkiano.rpg.bank.model.CharId}
     */
    CharIdRequired(1002, "Character ID required"),

    /**
     * Client requested information about a nonexistent character
     */
    CharIdNotFound(1003, "Character ID not found"),

    /**
     * Client attempted to overdraw from an account
     */
    InsufficientFunds(1010, "Insufficient funds")

    ; // <--- putting the terminating semicolon here makes it
    // easier to add more enum constants

    /**
     * {@link ErrorCode} enum constructor
     * @param number the code's numeric value
     * @param name an explanatory string
     */
    ErrorCode(int number, String name) {
        this.number = number;
        this.name = name;
    }

    private final int number;
    private final String name;
}