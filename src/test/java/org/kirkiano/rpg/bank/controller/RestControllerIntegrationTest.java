package org.kirkiano.finance.bank.controller;

import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.util.UriComponentsBuilder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.kirkiano.finance.bank.dto.AccountMapper;


/**
 * Abstract base class of integration tests
 */
@AutoConfigureMockMvc
public abstract class RestControllerIntegrationTest extends BaseTest {

    /**
     * Default constructor
     */
    protected RestControllerIntegrationTest() {}

    ///////////////////////////////////////////////////////
    // mock http requests

    protected RESTResponse mockRESTcall(Request request) throws Exception {
        return new RESTResponse(mvc.perform(request.getInner()));
    }

    ///////////////////////////////////////////////////////
    // examine JSON

    /**
     * Checks that the JSON in context has the given field with the given value
     *
     * @param key The field
     * @param value The value
     * @return A {@link ResultMatcher}, for easy chaining
     */
    public static ResultMatcher jsonPred0(String key, Object value) {
        // TODO: use string template (STR) when it's no longer in preview
        String fmt = MessageFormat.format("$.{0}", key);
        return jsonPath(fmt).value(value);
    }

    /**
     * Like {@link #jsonPred0}, but assumes that the JSON in context is
     * an array, and selects the JSON object at the given index.
     *
     * @param index Index into the array
     * @param key The field
     * @param value The value
     * @return A {@link ResultMatcher}, for easy chaining
     */
    public static ResultMatcher jsonPred1(int index, String key, Object value)
    {
        // TODO: use string template (STR) when it's no longer in preview
        String fmt = MessageFormat.format("$[{0}].{1}", index, key);
        return jsonPath(fmt).value(value);
    }

    ///////////////////////////////////////////////////////
    // API endpoints

    /**
     * Builder of the segment of the endpoint URL that serves accounts
     *
     * @return builer
     */
    protected UriComponentsBuilder accountsURL() {
        return getVersionURLBuilder().pathSegment(_accountsURL);
    }

    /**
     * Builder of the segment of the endpoint URL that shows the API version,
     * prefixed with the letter 'v'.
     *
     * @return path
     */
    private UriComponentsBuilder getVersionURLBuilder() {
        return UriComponentsBuilder.newInstance()
            .pathSegment("v" + _version);
    }

    ///////////////////////////////////////////////////////
    // private

    @Value("${api.accounts}")
    private String _accountsURL;

    @Value("${api.version}")
    private String _version;

    /**
     * Convenient account mapper
     */
    @Autowired
    protected AccountMapper accountMapper;

    @Autowired
    private MockMvc mvc;
}
