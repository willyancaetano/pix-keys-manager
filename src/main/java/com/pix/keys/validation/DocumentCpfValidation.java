package com.pix.keys.validation;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import com.pix.keys.dto.KeyType;
import com.pix.keys.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class DocumentCpfValidation implements KeyValidation {

    @Override
    public void validateKeyValue(String value) {
        CPFValidator validator = new CPFValidator();
        try {
            validator.assertValid(value);
        } catch (InvalidStateException e) {
            throw new ValidationException("CPF inválido");
        }
    }

    @Override
    public KeyType type() {
        return KeyType.DOCUMENT_CPF;
    }
}
