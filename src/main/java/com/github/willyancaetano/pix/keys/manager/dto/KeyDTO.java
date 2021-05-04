package com.github.willyancaetano.pix.keys.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.willyancaetano.pix.keys.manager.model.TypeKey;

import java.util.Optional;

public class KeyDTO {

    private String accountId;

    private TypeKey type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String value;

    public KeyDTO() {
    }

    public KeyDTO(final String accountId, final TypeKey type, String value) {
        this.accountId = accountId;
        this.type = type;
        this.value = value;
    }

    public String getAccountId() {
        return accountId;
    }

    public TypeKey getType() {
        return type;
    }

    public Optional<String> getValue() {
        return Optional.ofNullable(value);
    }

}
