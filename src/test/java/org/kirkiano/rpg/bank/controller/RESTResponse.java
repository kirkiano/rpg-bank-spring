package org.kirkiano.rpg.bank.controller;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.ResultActions;
import static com.fasterxml.jackson.databind.node.JsonNodeType.ARRAY;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.kirkiano.rpg.bank.config.Constants;
import org.kirkiano.rpg.bank.controller.error.Error;
import org.kirkiano.rpg.bank.controller.error.ErrorCode;
import org.kirkiano.rpg.bank.model.CharId;
import org.kirkiano.rpg.bank.model.Money;
import static org.kirkiano.rpg.bank.controller.RestControllerIntegrationTest.jsonPred0;


/**
 * Result of a REST call
 */
public class RESTResponse {

    /**
     * Constructor
     *
     * @param response the inner response
     * @throws Exception when content type is not
     *                   {@link MediaType#APPLICATION_JSON}
     */
    public RESTResponse(ResultActions response) throws Exception {
        this.response = response;
        this.objectMapper = new ObjectMapper();
        var appJson = MediaType.APPLICATION_JSON;
        var isJson = content().contentTypeCompatibleWith(appJson);
        // response.andExpect(isJson);
    }

    /**
     * Assert that the body of this response entails the given
     * error code.
     *
     * @param code the error code
     * @return self
     * @throws Exception on any kind of error, including assertion failure
     */
    @SuppressWarnings("unused")
    public RESTResponse andExpect(ErrorCode code) throws Exception {
        JsonNode err = this.getFirstErrorJsonNode();

        JsonNode codeName = err.get(Constants.ERROR_CODE_NAME);
        assertNotNull(codeName);
        assertEquals(codeName.asText(), code.getName());

        JsonNode codeNumber = err.get(Constants.ERROR_CODE_NUMBER);
        assertNotNull(codeNumber);
        assertEquals(codeNumber.asInt(), code.getNumber());

        return this;
    }

    /**
     * Assert that the body of this response consists of the given
     * {@link Error}.
     *
     * @param errExpected the expected error
     * @return self
     * @throws Exception on any kind of error, including assertion failure
     */
    public RESTResponse andExpect(Error errExpected) throws Exception {
        JsonNode firstError = this.getFirstErrorJsonNode();
        var errActual = objectMapper.treeToValue(firstError,
                                                 errExpected.getClass());
        assertEquals(errActual, errExpected);
        return this;
    }

    /**
     * Assert that the body of this response is a JSON object containing
     * a given key and value.
     *
     * @param key the key
     * @param value the value
     * @return self
     * @throws Exception on any kind of error, including assertion failure
     */
    public RESTResponse andExpect(String key, Object value) throws Exception {
        this.response.andExpect(jsonPred0(key, value));
        return this;
    }

    /**
     * Assert that this response has the given HTTP status.
     *
     * @param status the HTTP status
     * @return self
     * @throws Exception on any kind of error, including assertion failure
     */
    public RESTResponse andExpect(HttpStatus status) throws Exception {
        this.response.andExpect(
            result -> AssertionErrors.assertEquals(
                "Status",
                status.value(),
                result.getResponse().getStatus()
            )
        );
        return this;
    }

    /**
     * Assert that the body of this response is a serialized
     * {@link org.kirkiano.rpg.bank.dto.AccountDTO} with the
     * given parameters.
     *
     * @param charId ID of the account's owner
     * @param balance the account balance
     * @return self
     * @throws Exception on any kind of error, including assertion failure
     */
    public RESTResponse andExpectAccountDTO(CharId charId, Money balance)
        throws Exception
    {
        return this.andExpect(Constants.CHAR_ID_KEY, charId.value())
                   .andExpect(Constants.BALANCE_KEY, balance.intValue());
    }

    /**
     * Expect the response to consist of the given object
     * @param expected the Object expected
     * @return self
     * @throws Exception if expectation fails, or any other exception is thrown
     */
    public RESTResponse andExpect(Object expected) throws Exception {
        Class<?> clazz = expected.getClass();
        Object converted = objectMapper.treeToValue(this.getJsonNode(), clazz);
        if (!converted.equals(expected)) fail();
        return this;
    }

    /**
     * Assert that the body of this response is a JSON array that
     * includes the given object.
     *
     * @param expected the given object
     * @return self
     * @throws Exception on any kind of error, including assertion failure
     */
    public RESTResponse andExpectListIncluding(Object expected)
        throws Exception
    {
        Class<?> clazz = expected.getClass();
        JsonNode thisJson = this.getJsonNode();
        if (thisJson.getNodeType() != ARRAY) fail();
        for (final JsonNode node: thisJson) {
            Object converted = objectMapper.treeToValue(node, clazz);
            if (converted.equals(expected)) return this;
        }
        fail();
        return this; // satisfies the compiler

        /* LEAVE THIS HANGING AROUND FOR A WHILE
        CollectionType collType = objectMapper.getTypeFactory()
            .constructCollectionType(List.class, expected.getClass());
        List<T> elements = objectMapper.readValue(this.getResponseAsString(), collType);
        assertTrue(elements.contains(expected));
        */
    }

    /**
     * String representation of the body of this response
     * @return the string
     * @throws UnsupportedEncodingException in case data could not be decoded
     */
    public String getResponseAsString()
        throws UnsupportedEncodingException
    {
        return this.response.andReturn().getResponse().getContentAsString();
    }

    ///////////////////////////////////////////////////////
    // private

    private JsonNode getFirstErrorJsonNode()
        throws UnsupportedEncodingException,
               JsonProcessingException
    {
        JsonNode errors = this.getErrorsJsonNode();
        JsonNode firstError = errors.get(0);
        assertNotNull(firstError);
        return firstError;
    }

    private JsonNode getErrorsJsonNode()
        throws UnsupportedEncodingException,
               JsonProcessingException
    {
        JsonNode thisJson = this.getJsonNode();
        JsonNode errors = thisJson.get("errors");
        assertNotNull(errors);
        return errors;
    }

    private JsonNode getJsonNode()
        throws UnsupportedEncodingException,
               JsonProcessingException
    {
        return this.objectMapper.readTree(this.getResponseAsString());
    }
    private final ResultActions response;

    private final ObjectMapper objectMapper;
}
