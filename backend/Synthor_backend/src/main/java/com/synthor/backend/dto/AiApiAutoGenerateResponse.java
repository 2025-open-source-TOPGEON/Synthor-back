package com.synthor.backend.dto;

import java.util.List;

// AI API의 전체 응답 구조를 나타냅니다.
public record AiApiAutoGenerateResponse(
    int count,
    List<FieldDetail> fields
) {}
