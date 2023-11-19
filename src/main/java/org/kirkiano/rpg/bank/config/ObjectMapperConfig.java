package org.kirkiano.rpg.bank.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Config to prettify JSON
 */
@Configuration
public class ObjectMapperConfig {

    /**
     * Constructor
     */
    public ObjectMapperConfig() {}


    /**
     * Prettify JSON, for example in HTTP responses
     *
     * @return object mapper bean
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }
}
