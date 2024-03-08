package org.kirkiano.rpg.bank.controller;

import org.kirkiano.rpg.bank.dto.AccountMapper;
import org.kirkiano.rpg.bank.service.AccountService;


public class BaseController {

    public BaseController(AccountService accountService,
                          AccountMapper mapper)
    {
        this.accountService = accountService;
        this.mapper = mapper;
    }

    /**
     * Account service
     */
    protected final AccountService accountService;

    /**
     * Object for mapping between models and DTOs
     */
    protected final AccountMapper mapper;

}
