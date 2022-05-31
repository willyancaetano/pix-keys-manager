package com.pix.keys.validation;

import com.pix.keys.dto.KeyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeyValidationFactory {

    @Autowired
    private List<KeyValidation> validations;

    private static final Map<KeyType, KeyValidation> myValidationsCache = new HashMap<>();

    @PostConstruct
    public void initCache() {
        for(KeyValidation validation : validations) {
            myValidationsCache.put(validation.type(), validation);
        }
    }

    public static KeyValidation getValidation(KeyType type) {
        return myValidationsCache.get(type);
    }
}