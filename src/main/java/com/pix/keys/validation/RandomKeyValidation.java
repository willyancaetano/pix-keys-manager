package com.pix.keys.validation;

import com.pix.keys.dto.KeyType;
import com.pix.keys.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomKeyValidation implements KeyValidation {

    @Override
    public void validateKeyValue(String value) {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Chave aleatória inválida");
        }
    }

    @Override
    public KeyType type() {
        return KeyType.RANDOM;
    }
}
