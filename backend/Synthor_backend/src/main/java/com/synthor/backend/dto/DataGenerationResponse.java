package com.synthor.backend.dto;

import java.util.List;
import java.util.Map;

public class DataGenerationResponse {

    private List<FieldRequest> fields;
    private List<Map<String, Object>> data;

    public DataGenerationResponse() {
    }

    public DataGenerationResponse(List<FieldRequest> fields, List<Map<String, Object>> data) {
        this.fields = fields;
        this.data = data;
    }

    public List<FieldRequest> getFields() {
        return fields;
    }

    public void setFields(List<FieldRequest> fields) {
        this.fields = fields;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
