package com.pix.keys.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.pix.keys.dto.KeyType;
import com.pix.keys.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CellphoneValidation implements KeyValidation {

    @Override
    public void validateKeyValue(String value) {
        validateIfStartsWithSignalPlus(value);
        validateFormatPhoneNumber(value);
    }

    private void validateIfStartsWithSignalPlus(String value) {
        if(!value.startsWith("+")) {
            throw new ValidationException("Número de celular inválido");
        }
    }

    private void validateFormatPhoneNumber(String value) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

        Phonenumber.PhoneNumber phoneNumber = null;

        try{
            phoneNumber = phoneNumberUtil.parse(value, "BR");
            if(!phoneNumberUtil.isValidNumber(phoneNumber)) {
                throw new ValidationException("Número de celular inválido");
            }
        } catch (NumberParseException e) {
            throw new ValidationException("Número de celular inválido", e);
        }
    }

    @Override
    public KeyType type() {
        return KeyType.CELLPHONE;
    }
}
