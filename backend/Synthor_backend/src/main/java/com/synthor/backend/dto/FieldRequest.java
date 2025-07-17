package com.synthor.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldRequest {
    private String name; // The desired name of the data field, e.g., "사용자 이름"
    private String type; // The type of data to generate, e.g., "full_name"
    private Object value; // Used for fixed values, e.g., "최"
}
