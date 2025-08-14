package com.synthor.backend.service;

import com.synthor.backend.dto.AiApiResponse;
import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.dto.FieldRequest;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataGenerationService {

    private final Faker koreanFaker;
    private final Faker defaultFaker;
    private final AiApiService aiApiService;

    // Predefined data arrays are assumed to be here...

    public DataGenerationService(AiApiService aiApiService) {
        this.koreanFaker = new Faker(new Locale("ko"));
        this.defaultFaker = new Faker(Locale.ENGLISH);
        this.aiApiService = aiApiService;
    }

    public List<Map<String, Object>> generateData(DataGenerationRequest request) {
        List<Map<String, Object>> generatedData = new ArrayList<>();
        int count = request.getCount();
        List<FieldRequest> fields = request.getFields();

        for (FieldRequest field : fields) {
            String prompt = field.getPrompt();
            if (prompt != null && !prompt.isBlank()) {
                AiApiResponse aiResponse = aiApiService.suggestFieldType(prompt);
                if (aiResponse != null && aiResponse.getType() != null) {
                    String aiType = aiResponse.getType().trim();
                    field.setType(aiType);
                    Map<String, Object> aiConstraints = aiResponse.getConstraints();
                    if (aiConstraints != null && !aiConstraints.isEmpty()) {
                        field.setConstraints(aiConstraints);
                    }
                    if (aiResponse.getNullablePercent() != null) {
                        field.setNullablePercent(aiResponse.getNullablePercent());
                    }
                }
            }
        }

        Map<String, Set<Integer>> nullableIndexes = new HashMap<>();
        for (FieldRequest field : fields) {
            Integer nullablePercent = field.getNullablePercent();
            if (nullablePercent != null && nullablePercent > 0) {
                int nullableCount = (int) Math.round(count * (nullablePercent / 100.0));
                Set<Integer> indexes = new HashSet<>();
                while (indexes.size() < nullableCount) {
                    indexes.add(defaultFaker.random().nextInt(count));
                }
                nullableIndexes.put(field.getName(), indexes);
            }
        }

        for (int i = 0; i < count; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (FieldRequest field : fields) {
                String fieldName = field.getName();
                Object fieldValue;
                if (nullableIndexes.containsKey(fieldName) && nullableIndexes.get(fieldName).contains(i)) {
                    fieldValue = null;
                } else {
                    if ("fixed".equalsIgnoreCase(field.getType())) {
                        fieldValue = field.getValue();
                    } else {
                        fieldValue = generateValueByType(field);
                    }
                }
                row.put(fieldName, fieldValue);
            }
            generatedData.add(row);
        }
        return generatedData;
    }

    private Object generateValueByType(FieldRequest field) {
        String type = field.getType() != null ? field.getType().replaceAll("[^a-zA-Z0-9_]", "") : "";
        Map<String, Object> constraints = field.getConstraints();
        Map<String, Object> parsedConstraints = field.getParsedConstraints();

        // --- [KOREAN] Person & Personal Info ---
        if ("korean_full_name".equals(type)) {
            String lastName = (String) parsedConstraints.getOrDefault("lastName", constraints.get("lastName"));
            if (lastName != null) return lastName + koreanFaker.name().firstName();
            else return koreanFaker.name().lastName() + koreanFaker.name().firstName();
        } else if ("korean_first_name".equals(type)) {
            return koreanFaker.name().firstName();
        } else if ("korean_last_name".equals(type)) {
            return koreanFaker.name().lastName();
        
        // --- [ENGLISH] Person & Personal Info ---
        } else if ("full_name".equals(type)) {
            return defaultFaker.name().fullName();
        } else if ("first_name".equals(type)) {
            return defaultFaker.name().firstName();
        } else if ("last_name".equals(type)) {
            return defaultFaker.name().lastName();
        
        // --- Internet & Tech ---
        } else if ("username".equals(type)) {
            return defaultFaker.name().username();
        } else if ("password".equals(type)) {
            Integer minLength = (Integer) parsedConstraints.getOrDefault("minimum_length", constraints.getOrDefault("minimum_length", 8));
            int min = (minLength != null) ? minLength : 8;
            int max = min + 5;
            return defaultFaker.internet().password(min, max, true, true, true);
        } else if ("email_address".equals(type)) {
            return defaultFaker.internet().emailAddress();
        
        // --- [KOREAN] Address ---
        } else if ("korean_address".equals(type)) {
            return koreanFaker.address().fullAddress();
        
        // --- [ENGLISH] Address ---
        } else if ("address".equals(type)) {
            return defaultFaker.address().fullAddress();
        } else if ("country".equals(type)) {
            Object options = constraints.get("options");
            if (options instanceof List && !((List<?>) options).isEmpty()) {
                List<?> optionsList = (List<?>) options;
                return optionsList.get(defaultFaker.random().nextInt(optionsList.size())).toString();
            } else {
                return defaultFaker.address().country();
            }
        } else if ("number".equals(type)) {
            int min = (Integer) constraints.getOrDefault("min", 0);
            int max = (Integer) constraints.getOrDefault("max", 100);
            Integer decimals = (Integer) constraints.get("decimals");

            if (decimals != null && decimals > 0) {
                return defaultFaker.number().randomDouble(decimals, min, max);
            } else {
                return defaultFaker.number().numberBetween(min, max);
            }
        }
        // (Imagine all other numerous cases are converted here in full)
        
        // Fallback for any type not handled above
        else {
            return "Unsupported Type: " + type;
        }
    }

    // ... other private helper methods
}