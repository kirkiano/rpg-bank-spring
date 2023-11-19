package org.kirkiano.rpg.bank.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.kirkiano.rpg.bank.dto.*;
import org.kirkiano.rpg.bank.exn.AccountAlreadyExistsException;
import org.kirkiano.rpg.bank.exn.NegativeBalanceException;
import org.kirkiano.rpg.bank.model.Account;
import org.kirkiano.rpg.bank.model.CharId;
import org.kirkiano.rpg.bank.model.Money;
import org.kirkiano.rpg.bank.service.AccountService;


/**
 * REST controller that creates accounts
 */
@Slf4j
@RestController
@RequestMapping(path = "/v${api.version}/${api.accounts}",
                produces = APPLICATION_JSON_VALUE)
public class CreateAccountController extends BaseController {

    @SuppressWarnings("unused")
    @Autowired
    CreateAccountController(AccountService accountService,
                            AccountMapper mapper)
    {
        super(accountService, mapper);
    }


    /**
     * Create a bank account
     *
     * @param createAccount CreateAccountDTO
     * @return Account
     * @throws AccountAlreadyExistsException In case account already exists
     * @throws NegativeBalanceException Guards against negative initial balance
     */
    @Operation(summary = "Create new bank account",
               description = "Must provide a character ID, which must not" +
                             "already be associated with an existing account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description  = "OK"),

        @ApiResponse(responseCode = "400",
                     description  = "Char ID bad/missing"),

        @ApiResponse(responseCode = "422",
                     description  = "Account already exists")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE,
                 produces = APPLICATION_JSON_VALUE)
    public AccountDTO createAccount(
        @Valid @RequestBody CreateAccountDTO createAccount
    )
        throws AccountAlreadyExistsException,
               NegativeBalanceException
    {
        CharId charId = createAccount.charId();
        Money balance = createAccount.balance();
        log.trace("About to create: {}, {}", charId, balance);
        Account account = this.accountService.createAccount(charId, balance);
        AccountDTO accountDTO = mapper.toDTO(account);
        log.info("Created {}", accountDTO);
        return accountDTO;
    }
}
