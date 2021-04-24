package com.github.willyancaetano.pix.keys.manager.model;

public enum TypeKey {

    CELLPHONE("Telefone celular"), ALEATORY("Chave aleatória"), DOCUMENT("CPF");

    private final String description;

    TypeKey(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
