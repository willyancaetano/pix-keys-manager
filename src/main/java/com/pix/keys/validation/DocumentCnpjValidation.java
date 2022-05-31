package com.pix.keys.validation;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.pix.keys.dto.KeyType;
import com.pix.keys.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class DocumentCnpjValidation implements KeyValidation {

    @Override
    public void validateKeyValue(String value) {
        CNPJValidator validator = new CNPJValidator();
        try {
            validator.assertValid(value);
        } catch (InvalidStateException e) {
            throw new ValidationException("CNPJ inválido");
        }
    }

    @Override
    public KeyType type() {
        return KeyType.DOCUMENT_CNPJ;
    }
}
