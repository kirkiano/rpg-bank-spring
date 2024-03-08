package org.kirkiano.rpg.bank.controller.graphql;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import org.kirkiano.rpg.bank.controller.CreateAccountController;
import org.kirkiano.rpg.bank.controller.Request;
import org.kirkiano.rpg.bank.controller.RestControllerIntegrationTest;


/**
 * Integration tests for {@link CreateAccountController}
 */
public class GetAccountGraphQLControllerIntegrationTest
    extends RestControllerIntegrationTest
{
    /**
     * Default constructor
     */
    public GetAccountGraphQLControllerIntegrationTest() {}

    /**
     * Like {@linkk #validAccountCreation_shouldSucceed}, but tests
     * that a missing balance is interpreted as zero balance.
     *
     * @throws Exception on error
     */
    @Test
    public void getAllAccountsShouldAlwaysSucceed()
        throws Exception
    {
//        String body = "{\"query\": \"query allAccounts { accounts(" +
//            "pageNumber: 0, " +
//            "pageLength: 10, " +
//            "sortBy: \\\"id\\\"," +
//            "isDescending: false" +
//            ") { id }}\"}";
        String body = "{\"query\": \"query allAccounts { accounts { id }}\"}";
        var req = Request.graphql(body);
        var ignored = mockRESTcall(req)
            .andExpect(HttpStatus.OK);
    }

}