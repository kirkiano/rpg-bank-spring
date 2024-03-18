package org.kirkiano.finance.bank.controller.graphql;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import org.kirkiano.finance.bank.controller.graphql.scalar.CharIdScalar;
import org.kirkiano.finance.bank.controller.graphql.scalar.MoneyScalar;


@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder
            .scalar(MoneyScalar.INSTANCE)
            .scalar(CharIdScalar.INSTANCE);
    }
}
