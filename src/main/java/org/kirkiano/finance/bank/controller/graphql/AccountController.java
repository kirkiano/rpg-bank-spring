package org.kirkiano.finance.bank.controller.graphql;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import org.kirkiano.finance.bank.controller.BaseController;
import org.kirkiano.finance.bank.dto.AccountDTO;
import org.kirkiano.finance.bank.dto.AccountMapper;
import org.kirkiano.finance.bank.exn.AccountAlreadyExistsException;
import org.kirkiano.finance.bank.exn.NegativeBalanceException;
import org.kirkiano.finance.bank.model.CharId;
import org.kirkiano.finance.bank.model.Money;
import org.kirkiano.finance.bank.service.AccountService;


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

    @MutationMapping
    public AccountDTO createAnAccount(@Argument long charId)
        throws AccountAlreadyExistsException,
               NegativeBalanceException
    {
        var cid = new CharId(charId);
        var account = this.accountService.createAccount(cid, Money.ZERO);
        return this.mapper.toDTO(account);
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
