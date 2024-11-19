package com.billit.investment.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class InvestStatusTypeConverter implements AttributeConverter<InvestStatusType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(InvestStatusType status) {
        return status != null ? status.getCode() : null;
    }

    @Override
    public InvestStatusType convertToEntityAttribute(Integer code) {
        return code != null ? InvestStatusType.fromCode(code) : null;
    }
}

