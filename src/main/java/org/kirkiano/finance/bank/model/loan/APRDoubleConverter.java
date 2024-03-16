package org.kirkiano.finance.bank.model.loan;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
class APRDoubleConverter implements AttributeConverter<APR, Double> {

    @Override
    public Double convertToDatabaseColumn(APR apr) {
        return apr.doubleValue();
    }

    @Override
    public APR convertToEntityAttribute(Double p) {
        return new APR(p);
    }
}