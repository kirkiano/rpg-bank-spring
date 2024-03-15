package org.kirkiano.finance.bank.controller;

import java.util.Map;

import org.junit.Test;
import com.jayway.jsonpath.JsonPath;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;

import org.kirkiano.finance.bank.config.Constants;
import org.kirkiano.finance.bank.controller.error.Error;
import org.kirkiano.finance.bank.controller.error.ErrorCode;
import org.kirkiano.finance.bank.dto.AccountDTO;
import org.kirkiano.finance.bank.dto.CreateAccountDTO;
import org.kirkiano.finance.bank.exn.UnknownCharIdException;
import org.kirkiano.finance.bank.model.Account;
import org.kirkiano.finance.bank.model.Money;
import org.kirkiano.finance.bank.model.CharId;


/**
 * Integration tests for {@link CreateAccountController}
 */
public class CreateAccountRestControllerIntegrationTest
    extends RestControllerIntegrationTest
{
    /**
     * Default constructor
     */
    public CreateAccountRestControllerIntegrationTest() {}


    /**
     * POST {@code /v${api.version}/${api.accounts}} with a malformed body
     * (here, missing commas) should return {@link HttpStatus#BAD_REQUEST}
     * with {@link Error.UnparseableJson}.
     *
     * @throws Exception on error
     */
    @Test
    public void createAccountWithMissingCommas_shouldError() throws Exception {
        String badJSON = "{\"" + Constants.CHAR_ID_KEY + "\": 154875" +
                          "\"" + Constants.BALANCE_KEY + "\": 18}";
        var request = Request.post(accountsURL(), badJSON);
        var error = new Error.UnparseableJson(1, 19, -1, 18);

        // Assign to variable once (here, 'ignored'), otherwise the IDE
        // will complain that the return value of .andExpect(ErrorCode)
        // is never used.
        var ignored = mockRESTcall(request)
            .andExpect(HttpStatus.BAD_REQUEST)
            .andExpect(error);
    }


    /**
     * POST {@code /v${api.version}/${api.accounts}} without no "charId"
     * field should return {@link HttpStatus#UNPROCESSABLE_ENTITY} with
     * {@link Error.CharIdRequired}.
     *
     * @throws Exception on error
     */
    @Test
    public void createAccountWithMissingCharIdField_shouldError()
        throws Exception
    {
        String invalidCreateAccountJson =
            "{\"" + Constants.BALANCE_KEY + "\": 6}";
        var request = Request.post(accountsURL(), invalidCreateAccountJson);
        var error = new Error.CharIdRequired();

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);
    }


    /**
     * POST {@code /v${api.version}/${api.accounts}} without a null character
     * ID should return {@link HttpStatus#UNPROCESSABLE_ENTITY} with
     * {@link Error.CharIdRequired}.
     *
     * @throws Exception on error
     */
    @Test
    public void createAccountWithNullCharId_shouldError() throws Exception {
        String invalidCreateAccountJson =
            "{\"" + Constants.CHAR_ID_KEY + "\": null," +
             "\"" + Constants.BALANCE_KEY + "\":   42}";
        var request = Request.post(accountsURL(), invalidCreateAccountJson);
        var error = new Error.CharIdRequired();

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);
    }


    /**
     * POST {@code /v${api.version}/${api.accounts}} with a non-integer
     * character ID should return {@link HttpStatus#UNPROCESSABLE_ENTITY}
     * with {@link Error.Type}.
     *
     * @throws Exception on error
     */
    @Test
    public void createAccountWithBadCharIdValue_shouldError()
        throws Exception
    {
        var badCharIdValue = "foo";
        var faultyCreateDTO = Map.of(Constants.CHAR_ID_KEY, badCharIdValue);
        var request = Request.post(accountsURL(), faultyCreateDTO);
        var error = Error.Type.create(Constants.CHAR_ID_KEY,
                                      badCharIdValue,
                                      CharId.class);

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);
    }


    // TODO: consider a positive minimum for creating an account
    // Make it a global value defined in application.properties
    // and read in as an environment variable.
    /**
     * POST {@code /v${api.version}/${api.accounts}} with a negative balance
     * should return {@link HttpStatus#UNPROCESSABLE_ENTITY} with
     * {@link Error.InsufficientFunds} and should create no account.
     *
     * @throws Exception on error
     */
    @Test
    public void createAccountWithNegativeBalance_shouldError()
        throws Exception
    {
        CharId charId = charIdGen.get();
        var createDTO = new CreateAccountDTO(charId, Money.from(-1));
        var request = Request.post(accountsURL(), createDTO);
        var error = new Error.InsufficientFunds();

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);

        assertThrows(UnknownCharIdException.class,
                     () -> this.accountService.getAccountByCharId(charId));
    }


    /**
     * Duplicate POST {@code /v${api.version}/${api.accounts}} should return
     * {@link HttpStatus#UNPROCESSABLE_ENTITY} and
     * {@link ErrorCode#AccountAlreadyExists}.
     *
     * @throws Exception on error
     */
    @Test
    public void duplicateAccountCreation_shouldError() throws Exception {
        CharId charId = charIdGen.get();
        var createDTO = new CreateAccountDTO(charId, null);
        var request = Request.post(accountsURL(), createDTO);
        var error = new Error.AccountAlreadyExists(charId);

        mockRESTcall(request)
            .andExpect(HttpStatus.OK);

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);
    }


    /**
     * Well formed POST {@code /v${api.version}/${api.accounts}} with
     * valid parameters should return {@link HttpStatus#OK} and an
     * {@link AccountDTO} reflecting the requested parameters,
     * and should create an account having those parameters,
     * including the account ID.
     *
     * @throws Exception on error
     */
    @Test
    public void validAccountCreation_shouldSucceed() throws Exception {
        CharId charId = charIdGen.get();
        var balance = Money.from(100);
        var createDTO = new CreateAccountDTO(charId, balance);
        testCreateAccount(createDTO);
    }


    /**
     * Like {@link #validAccountCreation_shouldSucceed}, but tests
     * that a missing balance is interpreted as zero balance.
     *
     * @throws Exception on error
     */
    @Test
    public void createAccountWithNoBalance_shouldMeanZeroBalance()
        throws Exception
    {
        CharId charId = charIdGen.get();
        var createDTO = new CreateAccountDTO(charId, null);
        testCreateAccount(createDTO);
    }

    ///////////////////////////////////////////////////////
    // private

    /**
     * Helper function for account-creation tests
     *
     * @param dto account to be created
     * @throws Exception on error
     */
    private void testCreateAccount(CreateAccountDTO dto) throws Exception {
        CharId charId = dto.charId();
        Money balance = dto.balance() == null ? Money.ZERO : dto.balance();
        var request = Request.post(accountsURL(), dto);

        String responseS = mockRESTcall(request)
            .andExpect(HttpStatus.OK)
            .andExpectAccountDTO(charId, balance)
            .getResponseAsString();

        Integer aidInteger = JsonPath.parse(responseS).read("$.id");
        long aid = Long.valueOf(aidInteger);
        assertDoesNotThrow(() -> this.accountService.getAccountById(aid));

        Account account = this.accountService.getAccountById(aid);
        assertEquals(charId, account.getCharId());
        assertEquals(balance, account.getBalance());
    }

}