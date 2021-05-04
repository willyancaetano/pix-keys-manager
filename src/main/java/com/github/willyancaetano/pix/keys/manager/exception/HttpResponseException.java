package com.github.willyancaetano.pix.keys.manager.exception;

import com.github.willyancaetano.pix.keys.manager.dto.ProblemResponseDto;
import com.github.willyancaetano.pix.keys.manager.enumeration.Resources;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public abstract class HttpResponseException extends RuntimeException {

    private String type;

    private String title;

    private int status;

    private String detail;

    private Object instance;

    public HttpResponseException(String detail, Optional<Object> instance, Resources type) {
        super(detail);
        this.detail = detail;

        if(instance.isPresent()){
            this.instance = instance.get();
        }

        this.type = type.getPathErrorResolveResources();
        this.title = httpStatus().getReasonPhrase();
        this.status = httpStatus().value();
    }

    public ProblemResponseDto getProblemResponseDto() {
        return new ProblemResponseDto(type, title, status, detail, instance);
    }

    public abstract HttpStatus httpStatus();
}
