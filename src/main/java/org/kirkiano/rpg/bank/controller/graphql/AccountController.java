package org.kirkiano.rpg.bank.controller.graphql;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import org.kirkiano.rpg.bank.controller.BaseController;
import org.kirkiano.rpg.bank.dto.AccountDTO;
import org.kirkiano.rpg.bank.dto.AccountMapper;
import org.kirkiano.rpg.bank.service.AccountService;


@Controller
public class AccountController extends BaseController {

    @Autowired
    AccountController(AccountService accountService,
                      AccountMapper mapper)
    {
        super(accountService, mapper);
    }

    @QueryMapping
    public List<AccountDTO> accounts() {
        return this.accountService
            .getAllAccounts()
            .stream()
            .map(this.mapper::toDTO)
            .collect(Collectors.toList());
    }

    @QueryMapping
    public List<AccountDTO> accountsPage(@Argument int pageNumber,
                                         @Argument int pageLength,
                                         @Argument String sortBy,
                                         @Argument boolean isDescending)
    {
        return this.accountService
            .getAccountsPage(pageNumber, pageLength, sortBy, isDescending)
            .stream()
            .map(this.mapper::toDTO)
            .collect(Collectors.toList());
    }
}
