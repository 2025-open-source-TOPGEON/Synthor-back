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
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String format(List<Map<String, Object>> data, String format) throws JsonProcessingException {
        if (data == null || data.isEmpty()) {
            return "[]";
        }

        return switch (format.toLowerCase()) {
            case "csv" -> toCsv(data);
            case "html" -> toHtml(data);
            case "sql" -> toSql(data);
            case "xml" -> toXml(data);
            case "ldif" -> toLdif(data);
            case "json" -> toJson(data);
            default -> toJson(data);
        };
    }

    private String toJson(List<Map<String, Object>> data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }

    private String toCsv(List<Map<String, Object>> data) {
        StringBuilder csvBuilder = new StringBuilder();
        String header = data.get(0).keySet().stream()
                .map(this::escapeCsvValue)
                .collect(Collectors.joining(","));
        csvBuilder.append(header).append("\n");

        for (Map<String, Object> row : data) {
            String rowString = row.values().stream()
                    .map(this::escapeCsvValue)
                    .collect(Collectors.joining(","));
            csvBuilder.append(rowString).append("\n");
        }
        return csvBuilder.toString();
    }

    private String toHtml(List<Map<String, Object>> data) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n<html>\n<head>\n");
        htmlBuilder.append("<title>Generated Data</title>\n");
        htmlBuilder.append("<style>body{font-family:sans-serif;}table{border-collapse:collapse;width:100%;}th,td{border:1px solid #ddd;text-align:left;padding:8px;}thead tr{background-color:#f2f2f2;}</style>\n");
        htmlBuilder.append("</head>\n<body>\n<h2>Generated Data</h2>\n<table>\n");
        htmlBuilder.append("<thead>\n<tr>\n");
        for (String key : data.get(0).keySet()) {
            htmlBuilder.append("<th>").append(escapeHtmlValue(key)).append("</th>\n");
        }
        htmlBuilder.append("</tr>\n</thead>\n<tbody>\n");
        for (Map<String, Object> row : data) {
            htmlBuilder.append("<tr>\n");
            for (Object value : row.values()) {
                htmlBuilder.append("<td>").append(escapeHtmlValue(value)).append("</td>\n");
            }            htmlBuilder.append("</tr>\n");
        }
        htmlBuilder.append("</tbody>\n</table>\n</body>\n</html>");
        return htmlBuilder.toString();
    }

    private String toSql(List<Map<String, Object>> data) {
        StringBuilder sqlBuilder = new StringBuilder();
        String tableName = "my_table";
        String columns = data.get(0).keySet().stream()
                .map(col -> "`" + col + "`")
                .collect(Collectors.joining(", "));

        for (Map<String, Object> row : data) {
            String values = row.values().stream()
                    .map(this::escapeSqlValue)
                    .collect(Collectors.joining(", "));
            sqlBuilder.append(String.format("INSERT INTO %s (%s) VALUES (%s);\n", tableName, columns, values));
        }
        return sqlBuilder.toString();
    }

    private String toXml(List<Map<String, Object>> data) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rows>\n");
        for (Map<String, Object> row : data) {
            xmlBuilder.append("  <row>\n");
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                xmlBuilder.append("    <").append(entry.getKey()).append(">");
                xmlBuilder.append(escapeHtmlValue(entry.getValue()));
                xmlBuilder.append("</").append(entry.getKey()).append(">\n");
            }
            xmlBuilder.append("  </row>\n");
        }
        xmlBuilder.append("</rows>");
        return xmlBuilder.toString();
    }

    private String toLdif(List<Map<String, Object>> data) {
        StringBuilder ldifBuilder = new StringBuilder();
        String baseDn = "ou=users,dc=example,dc=com";
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> row = data.get(i);
            String uid = row.getOrDefault("username", "user" + (i + 1)).toString();
            String dn = String.format("uid=%s,%s", uid, baseDn);

            ldifBuilder.append("dn: ").append(dn).append("\n");
            ldifBuilder.append("objectClass: inetOrgPerson\n");
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getValue() != null) {
                    ldifBuilder.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n");
                }
            }
            ldifBuilder.append("\n");
        }
        return ldifBuilder.toString();
    }

    // --- Helper methods for escaping --- //

    private String escapeCsvValue(Object value) {
        if (value == null) return "";
        String stringValue = value.toString();
        if (stringValue.contains(",") || stringValue.contains("\n") || stringValue.contains("\"")) {
            stringValue = stringValue.replace("\"", "\"\"");
            return "\"" + stringValue + "\"";
        }
        return stringValue;
    }

    private String escapeHtmlValue(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String escapeSqlValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Number) return value.toString();
        return "'" + value.toString().replace("'", "''") + "'";
    }
}