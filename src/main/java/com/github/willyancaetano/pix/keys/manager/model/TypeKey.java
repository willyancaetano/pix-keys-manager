package com.github.willyancaetano.pix.keys.manager.model;

public enum TypeKey {

    CELLPHONE("Telefone celular", false), ALEATORY("Chave aleatória", true), DOCUMENT("CPF", false);

    private final String description;

    private final boolean valueGeneratedByApplication;

    TypeKey(final String description, final boolean valueGeneratedByApplication) {
        this.description = description;
        this.valueGeneratedByApplication = valueGeneratedByApplication;
    }

    public String getDescription() {
        return description;
    }

    public boolean isValueGeneratedByApplication() {
        return valueGeneratedByApplication;
    }
}
