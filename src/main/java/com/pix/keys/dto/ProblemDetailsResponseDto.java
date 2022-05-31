package com.pix.keys.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetailsResponseDto{
    private String type;
    private String title;
    private Integer status;
    private String detail;

    public ProblemDetailsResponseDto(String type, String title, Integer status, String detail) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
    }

    @JsonProperty("invalid-params")
    private List<InvalidParamsDto> invalidParams;

    public void addInvalidParam(InvalidParamsDto invalidParam){
        if(this.invalidParams == null) {
            this.invalidParams = new ArrayList<>();
        }
        this.invalidParams.add(invalidParam);
    }
}
