package org.kirkiano.finance.bank.controller;

import java.util.List;
import static java.util.stream.Collectors.toList;

import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.kirkiano.finance.bank.config.Constants;
import org.kirkiano.finance.bank.exn.UnknownCharIdException;
import org.kirkiano.finance.bank.model.Account;
import org.kirkiano.finance.bank.model.CharId;
import org.kirkiano.finance.bank.dto.AccountDTO;
import org.kirkiano.finance.bank.dto.AccountMapper;
import org.kirkiano.finance.bank.exn.NoSuchAccountIdException;
import org.kirkiano.finance.bank.service.AccountService;


/**
 * REST controller that fetches accounts
 */
@Slf4j
@RestController
@RequestMapping(path = "/v${api.version}/${api.accounts}",
                produces = APPLICATION_JSON_VALUE)
public class GetAccountController extends BaseController {

    @Autowired
    GetAccountController(AccountService accountService,
                         AccountMapper mapper)
    {
        super(accountService, mapper);
    }


    /**
     * Return a page from the set of all bank accounts.
     *
     * @param pageNumber page number
     * @param pageLength number of accounts per page
     * @param sortBy account field on which to sort
     * @param sortOrder sort order. If this value is not {@code DESC},
     *                  the sort order will be understood to be ascending.
     * @return accounts
     */
    @Operation(summary = "Get all bank accounts, in pages")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
                     description = "OK"),
        @ApiResponse(responseCode = "400",
                     description = "Parse error"),
        @ApiResponse(responseCode = "422",
                     description = "Unrecognized sort field")
    })
    @GetMapping
    public List<AccountDTO> getAccountsPage(
        @RequestParam(defaultValue = "0") int pageNumber,
        @RequestParam(defaultValue = "10") int pageLength,
        @RequestParam(defaultValue = Constants.BALANCE_KEY) String sortBy,
        @RequestParam(defaultValue = "DESC") String sortOrder
    )
    {
        boolean isDescending = sortOrder.equals("DESC");
        return this.accountService.getAccountsPage(pageNumber,
                                                   pageLength,
                                                   sortBy,
                                                   isDescending)
            .stream()
            .map(mapper::toDTO)
            .collect(toList());
    }


    /**
     * Fetch an account by account ID
     *
     * @param id account ID
     * @return account
     * @throws NoSuchAccountIdException In case of non-existent account
     */
    @Operation(summary = "Get account with given ID",
               description = "Account must exist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad ID"),
        @ApiResponse(responseCode = "404", description = "No such account")
    })
    @GetMapping(value = "/{id}")
    public AccountDTO getAccountById(@PathVariable long id)
        throws NoSuchAccountIdException
    {
        return mapper.toDTO(this.accountService.getAccountById(id));
    }


    /**
     * Fetch an account by character ID
     *
     * @param charId character ID
     * @return account
     * @throws UnknownCharIdException when {@code charId} is not found
     */
    @Operation(summary = "Get account associated with given character ID",
               description = "Account must exist")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Bad ID"),
        @ApiResponse(responseCode = "404", description = "No such account")
    })
    @GetMapping("/of/{" + Constants.CHAR_ID_KEY + "}")
    public AccountDTO getAccountByCharId(
        @PathVariable(Constants.CHAR_ID_KEY) CharId charId
    )
        throws UnknownCharIdException
    {
        Account a = this.accountService.getAccountByCharId(charId);
        return mapper.toDTO(a);
    }
}
