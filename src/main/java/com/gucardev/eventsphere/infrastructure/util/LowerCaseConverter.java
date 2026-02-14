package com.gucardev.eventsphere.infrastructure.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;
import java.util.Optional;

//  @Convert(converter = LowerCaseConverter.class)

@Converter
public class LowerCaseConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String s) {
        return Optional.ofNullable(s).map(x -> x.toLowerCase(Locale.ROOT)).orElse(null);
    }

    @Override
    public String convertToEntityAttribute(String s) {
        return Optional.ofNullable(s).map(x -> x.toLowerCase(Locale.ROOT)).orElse(null);
    }
}
