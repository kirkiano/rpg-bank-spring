package org.kirkiano.finance.bank.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
class MoneyLongConverter implements AttributeConverter<Money, Long> {

    @Override
    public Long convertToDatabaseColumn(Money money) {
        return money.longValue();
    }

    @Override
    public Money convertToEntityAttribute(Long aLong) {
        return Money.from(aLong);
    }
}