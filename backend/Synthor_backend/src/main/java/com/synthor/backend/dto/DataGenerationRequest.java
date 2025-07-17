package com.synthor.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataGenerationRequest {

    @Schema(description = "생성할 데이터 레코드(row)의 개수", example = "10")
    private int count;

    @Schema(description = "생성할 필드들의 목록")
    private List<FieldRequest> fields;
}
