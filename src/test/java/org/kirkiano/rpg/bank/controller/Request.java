package org.kirkiano.finance.bank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;


public class Request {

    /**
     * Makes a GET request with media type {@code application/json}
     *
     * @param url The URL to target, not counting parameters and fragments
     * @param uriVariables Any parameters to append to {@code URLpath}
     * @return Request
     */
    public static Request get(UriComponentsBuilder url,
                              Object... uriVariables)
    {
        return new Request(MockMvcRequestBuilders::get, url, uriVariables);
    }

    /**
     * Creates a POST request have media type {@code application/json}
     *
     * @param url The URL to target, not counting parameters and fragments
     * @param body Request body
     * @param uriVariables Any parameters to append to {@code URLpath}
     * @return Request
     * @throws JsonProcessingException on error
     */
    public static Request post(UriComponentsBuilder url,
                               Object body,
                               Object... uriVariables)
        throws JsonProcessingException
    {
        return new Request(MockMvcRequestBuilders::post,
                           url, body, uriVariables);
    }

    /**
     * Creates a PATCH request have media type {@code application/json}
     *
     * @param url The URL to target, not counting parameters and fragments
     * @param body Request body
     * @param uriVariables Any parameters to append to {@code URLpath}
     * @return Request
     * @throws JsonProcessingException on error
     */
    public static Request patch(UriComponentsBuilder url,
                                Object body,
                                Object... uriVariables)
        throws JsonProcessingException
    {
        return new Request(MockMvcRequestBuilders::patch,
                           url, body, uriVariables);
    }

    /**
     * Creates a GraphQL request
     *
     * @param body Request body
     * @return Request
     */
    public static Request graphql(String body) {
        var url = UriComponentsBuilder.fromPath("/graphql");
        return new Request(MockMvcRequestBuilders::post, url, body);
    }

    ///////////////////////////////////////////////////////

    @FunctionalInterface
    private interface Maker {
        MockHttpServletRequestBuilder make(String urlTemplate,
                                           Object... uriVariables);
    }

    public MockHttpServletRequestBuilder getInner() {
        return this.request;
    }

    private Request(Maker maker,
                    UriComponentsBuilder url,
                    String body,
                    Object... uriVariables)
    {
        this(maker, url, uriVariables);
        this.request = this.request.content(body);
    }

    private Request(Maker maker,
                    UriComponentsBuilder url,
                    Object body,
                    Object... uriVariables)
        throws JsonProcessingException
    {
        this(maker, url, uriVariables);
        String bodyString = body instanceof String ? // defensive
            (String) body : new ObjectMapper().writeValueAsString(body);
        this.request = this.request.content(bodyString);
    }

    private Request(Maker maker,
                    UriComponentsBuilder url,
                    Object... uriVariables)
    {
        this(maker.make(url.toUriString(), uriVariables));
    }

    private Request(MockHttpServletRequestBuilder request) {
        this.request = request.contentType(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder request;
}
