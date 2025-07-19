package com.synthor.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DataFormattingService {

    private final ObjectMapper objectMapper;

    public DataFormattingService() {
        this.objectMapper = new ObjectMapper();
        // For pretty printing JSON
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Formats the data into the specified format string.
     * @param data The list of data rows to format.
     * @param format The target format (e.g., "json", "csv").
     * @return A string representation of the data in the target format.
     * @throws JsonProcessingException If JSON processing fails.
     */
    public String format(List<Map<String, Object>> data, String format) throws JsonProcessingException {
        if (data == null || data.isEmpty()) {
            return "[]"; // Return empty JSON array for empty data
        }

        return switch (format.toLowerCase()) {
            case "csv" -> toCsv(data);
            // Future formats like "html", "xml" will be added here
            case "json" -> toJson(data);
            default -> toJson(data); // Default to JSON
        };
    }

    private String toJson(List<Map<String, Object>> data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    private String toCsv(List<Map<String, Object>> data) {
        StringBuilder csvBuilder = new StringBuilder();

        // 1. Header row from the keys of the first map
        String header = data.get(0).keySet().stream()
                .map(this::escapeCsvValue)
                .collect(Collectors.joining(","));
        csvBuilder.append(header).append("\n");

        // 2. Data rows
        for (Map<String, Object> row : data) {
            String rowString = row.values().stream()
                    .map(this::escapeCsvValue)
                    .collect(Collectors.joining(","));
            csvBuilder.append(rowString).append("\n");
        }

        return csvBuilder.toString();
    }

    /**
     * Escapes special characters in a value for CSV format.
     * If a value contains a comma, newline, or double quote, it will be enclosed in double quotes.
     * Existing double quotes within the value will be escaped by doubling them.
     * @param value The object to be converted to its string representation and escaped.
     * @return The escaped string value for CSV.
     */
    private String escapeCsvValue(Object value) {
        if (value == null) {
            return "";
        }
        String stringValue = value.toString();
        if (stringValue.contains(",") || stringValue.contains("\n") || stringValue.contains("\"")) {
            // Escape existing double quotes by doubling them
            stringValue = stringValue.replace("\"", """");
            return "\"" + stringValue + "\"";
        }
        return stringValue;
    }
}
