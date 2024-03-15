package org.kirkiano.finance.bank.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import org.kirkiano.finance.bank.controller.error.Error;
import org.kirkiano.finance.bank.controller.error.ErrorCode;
import org.kirkiano.finance.bank.config.Constants;
import org.kirkiano.finance.bank.model.CharId;
import org.kirkiano.finance.bank.dto.AccountDTO;
import org.kirkiano.finance.bank.model.Account;
import org.kirkiano.finance.bank.model.Money;


/**
 * Integration tests for {@link GetAccountController}
 */
public class GetAccountRestControllerIntegrationTest
    extends RestControllerIntegrationTest
{

    /**
     * Default constructor
     */
    public GetAccountRestControllerIntegrationTest() {}


    /**
     * GET {@code /v${api.version}/${api.accounts}?pageNumber={notAnInteger},...}
     * should return {@link HttpStatus#BAD_REQUEST} with
     * {@link ErrorCode#TypeError}.
     *
     * @throws Exception on error
     */
    @Test
    public void getAccountsWithUnparseablePageNumber_shouldError()
        throws Exception
    {
        var notANum = "foo";
        var url = accountsURL().queryParam(Constants.PAGE_NUMBER_KEY, notANum);
        var request = Request.get(url);
        var error = Error.Type.create(Constants.PAGE_NUMBER_KEY,
                                      notANum,
                                      Integer.class);

        mockRESTcall(request)
            .andExpect(HttpStatus.BAD_REQUEST)
            .andExpect(error);
    }


    /**
     * GET {@code /v${api.version}/${api.accounts}?pageNumber={pageNumber}&pageLength={unparseablePageLength}}
     * should return
     * {@link HttpStatus#BAD_REQUEST} with {@link ErrorCode#TypeError}.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccountWithUnparseablePageLength_shouldError()
        throws Exception
    {
        var notANum = "notANumber";
        var url = accountsURL().queryParam(Constants.PAGE_LENGTH_KEY, notANum);
        var request = Request.get(url);
        var error = Error.Type.create(Constants.PAGE_LENGTH_KEY,
                                      notANum,
                                      Integer.class);

        mockRESTcall(request)
            .andExpect(HttpStatus.BAD_REQUEST)
            .andExpect(error);
    }


    /**
     * GET {@code /v${api.version}/${api.accounts}?...&sortBy={wrongField}}
     * should return {@link HttpStatus#UNPROCESSABLE_ENTITY} with
     * {@link Error.UnknownProperty}.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccountsWithWrongSortField_shouldError() throws Exception {
        String wrongField = "foo";
        var url = accountsURL().queryParam(Constants.SORTBY_KEY, wrongField);
        var request = Request.get(url);
        var error = new Error.UnknownProperty(null, wrongField);

        mockRESTcall(request)
            .andExpect(HttpStatus.UNPROCESSABLE_ENTITY)
            .andExpect(error);
    }


    /**
     * GET {@code /v${api.version}/${api.accounts}/{unparseableAccountId}}
     * should return {@link HttpStatus#BAD_REQUEST} with
     * {@link ErrorCode#TypeError}.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccountWithUnparseableId_shouldError() throws Exception {
        var stringInsteadOfNumber = "notANumber";
        var url = accountsURL().pathSegment(stringInsteadOfNumber);
        var request = Request.get(url);
        var error = Error.Type.create("id",
                                      stringInsteadOfNumber,
                                      Long.class);

        mockRESTcall(request)
            .andExpect(HttpStatus.BAD_REQUEST)
            .andExpect(error);
    }

    /**
     * GET {@code /v${api.version}/${api.accounts}/{invalidAccountId}}
     * should return {@link HttpStatus#NOT_FOUND} and
     * {@link ErrorCode#NoSuchAccountId}.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccountByInvalidId_shouldError() throws Exception {
        // Given any positive ID, it is conceivable that some other
        // concurrent tests will create an account with that ID. Therefore
        // use here an ID that is guaranteed not to conflict with them,
        // ie, use a negative one.
        long badId = -1L;
        var url = accountsURL().pathSegment(Long.toString(badId));
        var request = Request.get(url);
        var error = new Error.NoSuchAccountId(badId);

        mockRESTcall(request)
            .andExpect(HttpStatus.NOT_FOUND)
            .andExpect(error);
    }


    /**
     * GET {@code /v${api.version}/${api.accounts}/of/{unparseableCharId}}
     * should return {@link HttpStatus#BAD_REQUEST} with
     * {@link ErrorCode#TypeError}.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccountByUnparseableCharId_shouldError() throws Exception {
        var notACharId = "foo";
        var url = accountsURL().pathSegment("of", notACharId);
        var request = Request.get(url);
        var error = Error.Type.create(Constants.CHAR_ID_KEY,
                                      notACharId,
                                      CharId.class);
        mockRESTcall(request)
            .andExpect(HttpStatus.BAD_REQUEST)
            .andExpect(error);
    }


    /**
     * GET {@code /v${api.version}/${api.accounts}/of/{invalidCharId}}
     * should return {@link HttpStatus#NOT_FOUND} with
     * {@link Error.CharIdNotFound}.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccountByAbsentCharId_shouldError() throws Exception {
        var invalidCharId = new CharId(-1L);
        var url = accountsURL()
            .pathSegment("of")
            .pathSegment(Long.toString(invalidCharId.value()));
        var request = Request.get(url);
        var error = new Error.CharIdNotFound(invalidCharId);

        mockRESTcall(request)
            .andExpect(HttpStatus.NOT_FOUND)
            .andExpect(error);
    }


    /**
     * GET {@code /v${api.version}/${api.accounts}} should always succeed.
     *
     * @throws Exception upon error
     */
    @Test
    public void getAccounts_shouldSucceed() throws Exception {
        Account account = mockAccount(charIdGen.get(), Money.from(1234));
        AccountDTO accountDTO = accountMapper.toDTO(account);
        var request = Request.get(accountsURL());

        // Assign to variable once (here, 'ignored'), otherwise the IDE
        // will complain that the return value of .andExpectListIncluding
        // is never used.
        var ignored = mockRESTcall(request)
            .andExpect(HttpStatus.OK)
            // Since other tests may also have created accounts,
            // don't assume that the list returned in the response
            // will contain only the account created here. Instead,
            // test that it contains it.
            .andExpectListIncluding(accountDTO);
    }

}