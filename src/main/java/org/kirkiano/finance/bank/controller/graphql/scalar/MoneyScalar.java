package org.kirkiano.finance.bank.controller.graphql.scalar;

import java.util.Locale;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.schema.*;
import jakarta.validation.constraints.NotNull;

import org.kirkiano.finance.bank.model.Money;


// For an example, see
// https://github.com/graphql-java/graphql-java-extended-scalars/blob/master/src/main/java/graphql/scalars/datetime/DateTimeScalar.java
public class MoneyScalar {

    private static final Coercing<Money, Float> coercing =
        new Coercing<Money, Float>() {

            public Float serialize(
                Object data,
                @NotNull @SuppressWarnings("unused") GraphQLContext ctxt,
                @NotNull @SuppressWarnings("unused") Locale locale
            ) throws CoercingSerializeException
            {
                return (data instanceof Money) ?
                    ((Money) data).floatValue() : null;
            }

            public Money parseValue(
                Object input,
                @SuppressWarnings("unused") GraphQLContext ctxt,
                @SuppressWarnings("unused") Locale locale
            ) throws CoercingParseValueException
            {
                return (input instanceof Float) ?
                    Money.from((long) input) : // TODO: switch Money to float
                    null;
            }

            public Money parseLiteral(
                Object input,
                @SuppressWarnings("unused") CoercedVariables vars,
                @SuppressWarnings("unused") GraphQLContext ctxt,
                @SuppressWarnings("unused") Locale locale
            ) throws CoercingParseLiteralException
            {
                return (input instanceof Float) ?
                    Money.from((long) input) : // TODO: switch Money to float
                    null;
            }
        };

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
        .name("Money")
        .description("Money scalar")
        .coercing(coercing)
        .build();


    // prevent instantiation
    private MoneyScalar() {}

}
