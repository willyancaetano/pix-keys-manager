package com.github.willyancaetano.pix.keys.manager.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Document(collection = "accountKeys")
public class AccountKey {

    @Id
    private String id;

    private String accountId;

    private Map<TypeKey, String> keys;

    public AccountKey(final String accountId) {
        this.accountId = accountId;
        this.keys = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
    }

    public Map<TypeKey, String> getKeys() {
        return Collections.unmodifiableMap(keys);
    }

    public void addNewKey(TypeKey type, String value){
        this.keys.put(type, value);
    }

}
