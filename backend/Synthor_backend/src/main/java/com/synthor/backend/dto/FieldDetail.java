package com.synthor.backend.dto;

import java.util.Map;

// AI API 응답의 fields 배열 안에 있는 각 필드 객체를 나타냅니다.
public record FieldDetail(
    String name,
    String type,
    Map<String, Object> constraints,
    int nullablePercent
) {}
