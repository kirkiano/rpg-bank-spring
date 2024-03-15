package org.kirkiano.finance.bank.controller;

import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.kirkiano.finance.bank.config.Constants;
import org.kirkiano.finance.bank.controller.error.ErrorCode;
import org.kirkiano.finance.bank.dto.BalanceDTO;
import org.kirkiano.finance.bank.dto.ChangeBalanceDTO;
import org.kirkiano.finance.bank.model.Account;
import org.kirkiano.finance.bank.model.Money;
import org.kirkiano.finance.bank.controller.error.Error;


/**
 * Integration tests for
 * {@link org.kirkiano.finance.bank.controller.ChangeBalanceController}
 */
public class ChangeBalanceRestControllerIntegrationTest
    extends RestControllerIntegrationTest
{

    /**
     * Default constructor
     */
    public ChangeBalanceRestControllerIntegrationTest() {}


    /**
     * PATCH {@code /v${api.version}/${api.accounts}/{id}} with unparseable
     * body should return {@link HttpStatus#BAD_REQUEST} with
     * {@link ErrorCode#Unparseable}.
     *
     * @throws Exception on error
     */
    @Test
    public void depositWithUnparseableDelta_shouldError() throws Exception {
        var badJSON = "{\"delta\" 154875}";
        var someAccountId = Long.toString(1L);
        var url = accountsURL().pathSegment(someAccountId);
        var request = Request.patch(url, badJSON);
        var error = new Error.UnparseableJson(1,11,-1,10);

        mockRESTcall(request)
            .andExpect(HttpStatus.BAD_REQUEST)
            .andExpect(error);
    }


    /**
     * PATCH {@code /v${api.version}/${api.accounts}/{id}} with faulty delta
     * should return {@link HttpStatus#UNPROCESSABLE_ENTITY} with
     * {@link Error.Type}.
     *
     * @throws Exception on error
     */
    @Test
    public void depositWithFaultyDelta_shouldError() throws Exception {
        var badDelta = "foo";
        var someAccountId = Long.toString(1L);
        var faultyChangeBalanceDTO = Map.of(Constants.DELTA, badDelta);
        var url = accountsURL().pathSegment(someAccountId);
        var request = Request.patch(url, faultyChangeBalanceDTO);
        var error = Error.Type.create(Constants.DELTA,
                                      badDelta,
                                      Long.class);

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);
    }


    /**
     * PATCH {@code /v${api.version}/${api.accounts}/{id}} on an unknown
     * account ID should return {@link HttpStatus#NOT_FOUND} with
     * {@link Error.NoSuchAccountId}.
     *
     * @throws Exception on error
     */
    @Test
    public void depositWithUnknownId_shouldError() throws Exception {
        long invalidId = -1L;
        var changeBalanceDTO = new ChangeBalanceDTO(Money.from(1));
        var url = accountsURL().pathSegment(Long.toString(invalidId));
        var request = Request.patch(url, changeBalanceDTO);
        var error = new Error.NoSuchAccountId(invalidId);

        mockRESTcall(request)
            .andExpect(HttpStatus.NOT_FOUND)
            .andExpect(error);
    }


    /**
     * PATCH {@code /v${api.version}/${api.accounts}/{id}} on a valid account
     * ID should, for an excessive withdrawal, return
     * {@link HttpStatus#UNPROCESSABLE_ENTITY} with
     * {@link Error.InsufficientFunds} and should leave the
     * account balance unchanged.
     *
     * @throws Exception on error
     */
    @Test
    public void overdraw_shouldError() throws Exception {
        var balance0 = Money.from(10);
        var amount = Money.from(-11);

        Account account0 = mockAccount(charIdGen.get(), balance0);
        long aid = account0.getId();

        var changeBalanceDTO = new ChangeBalanceDTO(amount);
        var url = accountsURL().pathSegment(Long.toString(aid));
        var request = Request.patch(url, changeBalanceDTO);
        var error = new Error.InsufficientFunds();

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);

        Account account1 = accountService.getAccountById(aid);
        assertEquals(account1.getBalance(), balance0);
    }

    /**
     * PATCH {@code /v${api.version}/${api.accounts}/{id}} on a valid account
     * ID should, for a deposit, return {@link HttpStatus#OK} and a
     * {@link BalanceDTO} showing the new balance.
     *
     * @throws Exception on error
     */
    @Test
    public void deposit_shouldSucceed() throws Exception {
        var deposit = Money.from(1);
        var balance0 = Money.from(10);
        var balance1 = Money.add(balance0, deposit);

        Account account0 = mockAccount(charIdGen.get(), balance0);
        long aid = account0.getId();

        var changeBalanceDTO = new ChangeBalanceDTO(deposit);
        var url = accountsURL().pathSegment(Long.toString(aid));
        var balanceDTO = new BalanceDTO(balance1);
        var request = Request.patch(url, changeBalanceDTO);

        // Assign to variable once (here, 'ignored'), otherwise the IDE
        // will complain that the return value of .andExpect(Object)
        // is never used.
        var ignored = mockRESTcall(request)
            .andExpect(HttpStatus.OK)
            .andExpect(balanceDTO);

        Account account1 = accountService.getAccountById(aid);
        assertEquals(account1.getBalance(), balance1);
    }

}
