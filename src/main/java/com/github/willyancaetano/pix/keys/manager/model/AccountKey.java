package com.github.willyancaetano.pix.keys.manager.model;

import com.github.willyancaetano.pix.keys.manager.enumeration.Resources;
import com.github.willyancaetano.pix.keys.manager.exception.BadRequestException;
import com.github.willyancaetano.pix.keys.manager.exception.ConflictException;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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

    public void addNewKey(TypeKey type, Optional<String> valueOptional){

        if(keys.containsKey(type)){
            throw new ConflictException("Chave já existe", Optional.empty(), Resources.KEYS);
        }

        if(type.isValueGeneratedByApplication()){
            this.keys.put(type, UUID.randomUUID().toString());
        }else{
            validateValueSentByKey(valueOptional);
            this.keys.put(type, valueOptional.get());
        }
    }

    private void validateValueSentByKey(Optional<String> valueOptional){
        if(!valueOptional.isPresent()) {
            throw new BadRequestException("Valor precisa ser enviado", Optional.empty(), Resources.KEYS);
        }
    }

}
