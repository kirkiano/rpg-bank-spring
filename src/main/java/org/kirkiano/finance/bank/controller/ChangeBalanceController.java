package org.kirkiano.finance.bank.controller;

import lombok.extern.slf4j.Slf4j;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.kirkiano.finance.bank.dto.AccountMapper;
import org.kirkiano.finance.bank.dto.BalanceDTO;
import org.kirkiano.finance.bank.dto.ChangeBalanceDTO;
import org.kirkiano.finance.bank.exn.NegativeBalanceException;
import org.kirkiano.finance.bank.exn.NoSuchAccountIdException;
import org.kirkiano.finance.bank.model.Money;
import org.kirkiano.finance.bank.service.AccountService;


/**
 * REST controller for changing account balances
 */
@Slf4j
@RestController
@RequestMapping(path = "/v${api.version}/${api.accounts}",
                produces = APPLICATION_JSON_VALUE,
                consumes = APPLICATION_JSON_VALUE)
public class ChangeBalanceController extends BaseController {

    @Autowired
    ChangeBalanceController(AccountService accountService,
                            AccountMapper mapper)
    {
        super(accountService, mapper);
    }


    /**
     * Change balance. In case of overdraft, throw
     * {@link NegativeBalanceException}.
     *
     * @param id account ID
     * @param dto request to change balance
     * @return new balance
     * @throws NoSuchAccountIdException In case account does not exist
     * @throws NegativeBalanceException Guards against overdraft
     */
    @Operation(summary = "Change the balance of a bank account",
               description = "Must not result in overdraft")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Malformed request"),
        @ApiResponse(responseCode = "404", description = "No such account"),
        @ApiResponse(responseCode = "422", description = "Overdraft attempt")
    })
    @PatchMapping(value = "/{id}")
    public BalanceDTO changeBalance(@PathVariable long id,
                                    @Valid @RequestBody ChangeBalanceDTO dto)
        throws NoSuchAccountIdException,
               NegativeBalanceException
    {
        Money newBalance = this.accountService.changeBalance(id, dto.delta());
        return new BalanceDTO(newBalance);
    }

}
