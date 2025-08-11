package com.synthor.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class AiApiResponse {
    private String type;
    private Map<String, Object> constraints;
    private Integer nullablePercent;
}
