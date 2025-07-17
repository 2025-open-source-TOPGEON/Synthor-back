package com.synthor.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataGenerationRequest {
    private int count; // How many rows of data to generate
    private List<FieldRequest> fields; // A list of field definitions
}
