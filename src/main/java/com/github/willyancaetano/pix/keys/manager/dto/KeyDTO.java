package com.github.willyancaetano.pix.keys.manager.dto;

import com.github.willyancaetano.pix.keys.manager.model.TypeKey;

public class KeyDTO {

    private String accountId;

    private TypeKey type;

    private String value;

    public String getAccountId() {
        return accountId;
    }

    public TypeKey getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

}
