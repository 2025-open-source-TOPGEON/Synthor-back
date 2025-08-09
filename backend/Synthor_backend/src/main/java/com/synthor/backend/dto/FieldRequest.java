package com.synthor.backend.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class FieldRequest {

    @Schema(description = "생성될 데이터의 필드 이름 (JSON의 key로 사용됨)", example = "userName")
    private String name;

    @Schema(description = "생성할 데이터의 종류 (DataGenerationService 참조)", example = "full_name")
    private String type;

    @Schema(description = "고정된 값을 사용할 경우에만 설정 (type이 'fixed'일 때)", example = "서울")
    private Object value;

    @Schema(description = "사용자가 입력한 자연어 프롬프트", example = "성이 김씨인 이름")
    private String prompt;

    @Schema(description = "AI가 프롬프트를 분석한 결과")
    private Map<String, Object> parsedConstraints = new HashMap<>();

    @Schema(description = "데이터 타입에 따른 제약조건 (예: 비밀번호 길이, 이미지 크기 등)")
    private Map<String, Object> constraints = new HashMap<>();

    @Schema(description = "데이터의 Nullable 비율 (0-100 사이 값)", example = "10")
    @JsonProperty("nullablePercent")
    private Integer nullablePercent = 0;

    @JsonAnySetter
    public void addConstraint(String key, Object value) {
        this.constraints.put(key, value);
    }
}
