package org.kirkiano.finance.bank.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/*
Regarding Hibernate's old way of converting custom types, see:
* https://stackoverflow.com/a/76442375
* https://www.baeldung.com/hibernate-custom-types
*/


@Converter
class CharIdLongConverter implements AttributeConverter<CharId, Long> {

    @Override
    public Long convertToDatabaseColumn(CharId charId) {
        return charId.value();
    }

    @Override
    public CharId convertToEntityAttribute(Long aLong) {
        return new CharId(aLong);
    }
}