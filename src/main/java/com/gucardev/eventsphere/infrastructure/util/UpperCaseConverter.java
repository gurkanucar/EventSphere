package com.gucardev.eventsphere.infrastructure.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Locale;
import java.util.Optional;

//  @Convert(converter = UpperCaseConverter.class)

@Converter
public class UpperCaseConverter implements AttributeConverter<String, String> {
  @Override
  public String convertToDatabaseColumn(String attribute) {
    return Optional.ofNullable(attribute)
        .map(s -> s.toUpperCase(Locale.ROOT))
        .orElse(null);
  }
  
  @Override
  public String convertToEntityAttribute(String dbData) {
    return Optional.ofNullable(dbData)
        .map(s -> s.toUpperCase(Locale.ROOT))
        .orElse(null);
  }
}
