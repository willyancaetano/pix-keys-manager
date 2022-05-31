package com.pix.keys.validation;

import com.pix.keys.dto.KeyType;
import com.pix.keys.exception.ValidationException;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

@Component
public class EmailValidation implements KeyValidation {

    @Override
    public void validateKeyValue(String value) {
        boolean isValid = EmailValidator.getInstance().isValid(value);

        if(!isValid) {
            throw new ValidationException("Email inválido");
        }
    }

    @Override
    public KeyType type() {
        return KeyType.EMAIL;
    }
}
