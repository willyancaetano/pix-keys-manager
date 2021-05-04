package com.github.willyancaetano.pix.keys.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemResponseDto {

    private String type;

    private String title;

    private Integer status;

    private String detail;

    private Object instance;

    public ProblemResponseDto(final String type, final String title, final Integer status, final String detail, final Object instance) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public Object getInstance() {
        return instance;
    }

}

