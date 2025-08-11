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

    // Predefined data arrays...
    private static final String[] KOREAN_JOB_TITLES = {"팀장", "부장", "과장"};
    // ... (other arrays are omitted for brevity)

    public DataGenerationService(AiApiService aiApiService) {
        this.koreanFaker = new Faker(new Locale("ko"));
        this.defaultFaker = new Faker(Locale.ENGLISH);
        this.aiApiService = aiApiService;
    }

    public List<Map<String, Object>> generateData(DataGenerationRequest request) {
        // ... (AI override and nullable logic remains the same)
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
        // Sanitize the type string to remove any non-alphanumeric characters (except underscore)
        String type = field.getType() != null ? field.getType().replaceAll("[^a-zA-Z0-9_]", "") : "";
        Map<String, Object> constraints = field.getConstraints();
        Map<String, Object> parsedConstraints = field.getParsedConstraints();

        // REWRITTEN FROM SWITCH TO IF-ELSE IF
        if ("korean_full_name".equals(type)) {
            String lastName = (String) parsedConstraints.getOrDefault("lastName", constraints.get("lastName"));
            if (lastName != null) {
                return lastName + koreanFaker.name().firstName();
            } else {
                return koreanFaker.name().lastName() + koreanFaker.name().firstName();
            }
        } else if ("country".equals(type)) {
            Object options = constraints.get("options");
            if (options instanceof List && !((List<?>) options).isEmpty()) {
                List<?> optionsList = (List<?>) options;
                return optionsList.get(defaultFaker.random().nextInt(optionsList.size())).toString();
            } else {
                return defaultFaker.address().country();
            }
        } else if ("password".equals(type)) {
            Integer minLength = (Integer) parsedConstraints.getOrDefault("minimum_length", constraints.getOrDefault("minimum_length", 8));
            int min = (minLength != null) ? minLength : 8;
            int max = min + 5;
            return defaultFaker.internet().password(min, max, true, true, true);
        }
        // ... Add all other cases here in else if blocks
        // Example for a simple case:
        else if ("korean_first_name".equals(type)) {
            return koreanFaker.name().firstName();
        }
        // (Imagine all other cases are converted here)
        else {
            return "Unsupported Type: " + type;
        }
    }

    // ... (other private helper methods remain the same)
}
