package com.synthor.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldRequest {

    @Schema(description = "생성될 데이터의 필드 이름 (JSON의 key로 사용됨)", example = "userName")
    private String name;

    @Schema(description = "생성할 데이터의 종류 (DataGenerationService 참조)", example = "full_name")
    private String type;

    @Schema(description = "고정된 값을 사용할 경우에만 설정 (type이 'fixed'일 때)", example = "서울")
    private Object value;
}
