package com.ims.user.domain.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {

    private String timezone;
    private String locale;
    private UUID defaultWarehouseId;

    @Converter(autoApply = false)
    public static class JsonConverter implements AttributeConverter<UserPreferences, String> {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(UserPreferences attribute) {
            if (attribute == null) return null;
            try {
                return MAPPER.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to serialize UserPreferences", e);
            }
        }

        @Override
        public UserPreferences convertToEntityAttribute(String dbData) {
            if (dbData == null || dbData.isBlank()) return null;
            try {
                return MAPPER.readValue(dbData, UserPreferences.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize UserPreferences", e);
            }
        }
    }
}
