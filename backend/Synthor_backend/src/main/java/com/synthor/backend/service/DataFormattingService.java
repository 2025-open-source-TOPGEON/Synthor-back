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
            case "html" -> toHtml(data);
            case "sql" -> toSql(data);
            case "xml" -> toXml(data);
            case "ldif" -> toLdif(data);
            case "json" -> toJson(data);
            default -> toJson(data); // Default to JSON
        };
    }
    }

    private String toHtml(List<Map<String, Object>> data) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("<title>Generated Data</title>\n");
        htmlBuilder.append("<style>\n");
        htmlBuilder.append("  body { font-family: sans-serif; }\n");
        htmlBuilder.append("  table { border-collapse: collapse; width: 100%; }\n");
        htmlBuilder.append("  th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }\n");
        htmlBuilder.append("  thead tr { background-color: #f2f2f2; }\n");
        htmlBuilder.append("</style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("<h2>Generated Data</h2>\n");
        htmlBuilder.append("<table>\n");

        // Header
        htmlBuilder.append("<thead>\n<tr>\n");
        for (String key : data.get(0).keySet()) {
            htmlBuilder.append("<th>").append(escapeHtmlValue(key)).append("</th>\n");
        }
        htmlBuilder.append("</tr>\n</thead>\n");

        // Body
        htmlBuilder.append("<tbody>\n");
        for (Map<String, Object> row : data) {
            htmlBuilder.append("<tr>\n");
            for (Object value : row.values()) {
                htmlBuilder.append("<td>").append(escapeHtmlValue(value)).append("</td>\n");
            }
            htmlBuilder.append("</tr>\n");
        }
        htmlBuilder.append("</tbody>\n");

        htmlBuilder.append("</table>\n");
        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>");

        return htmlBuilder.toString();
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
            stringValue = stringValue.replace("\"", "\"\"");
            return "\"" + stringValue + "\"";
        }
        return stringValue;
    }

    private String escapeHtmlValue(Object value) {
        if (value == null) {
            return "";
        }
        String stringValue = value.toString();
        // Basic HTML escaping
        return stringValue.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String toSql(List<Map<String, Object>> data) {
        StringBuilder sqlBuilder = new StringBuilder();
        String tableName = "my_table"; // A placeholder table name

        if (data.isEmpty()) {
            return "";
        }

        // Get column names from the first data row, escape them for safety
        String columns = data.get(0).keySet().stream()
                .map(col -> "`" + col + "`") // Basic quoting for column names
                .collect(Collectors.joining(", "));

        // Create an INSERT statement for each row
        for (Map<String, Object> row : data) {
            String values = row.values().stream()
                    .map(this::escapeSqlValue)
                    .collect(Collectors.joining(", "));
            sqlBuilder.append(String.format("INSERT INTO %s (%s) VALUES (%s);\n", tableName, columns, values));
        }

        return sqlBuilder.toString();
    }

    private String escapeSqlValue(Object value) {
        if (value == null) {
            return "NULL";
        }
        // Check if the value is a number type
        if (value instanceof Number) {
            return value.toString();
        }
        // For all other types, treat as a string: escape single quotes and wrap in single quotes.
        String stringValue = value.toString().replace("'", "''");
        return "'" + stringValue + "'";
    }

    private String toXml(List<Map<String, Object>> data) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuilder.append("<rows>\n");

        for (Map<String, Object> row : data) {
            xmlBuilder.append("  <row>\n");
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                xmlBuilder.append("    <").append(key).append(">");
                xmlBuilder.append(escapeHtmlValue(value)); // Using HTML escape for XML content is generally safe
                xmlBuilder.append("</").append(key).append(">\n");
            }
            xmlBuilder.append("  </row>\n");
        }

        xmlBuilder.append("</rows>");        return xmlBuilder.toString();    }    private String toLdif(List<Map<String, Object>> data) {        StringBuilder ldifBuilder = new StringBuilder();        String baseDn = "ou=users,dc=example,dc=com"; // A placeholder base DN        for (int i = 0; i < data.size(); i++) {            Map<String, Object> row = data.get(i);            // Try to find a unique identifier for the DN, fallback to index            String uid = row.getOrDefault("username", "user" + (i + 1)).toString();            String dn = String.format("uid=%s,%s", uid, baseDn);            ldifBuilder.append("dn: ").append(dn).append("\n");            ldifBuilder.append("objectClass: inetOrgPerson\n"); // A common object class            for (Map.Entry<String, Object> entry : row.entrySet()) {                String key = entry.getKey();                Object value = entry.getValue();                if (value != null) {                    ldifBuilder.append(key).append(": ").append(value.toString()).append("\n");                }            }            ldifBuilder.append("\n"); // Separator between entries        }        return ldifBuilder.toString();    }}
