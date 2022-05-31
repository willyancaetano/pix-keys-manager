package com.pix.keys.validation;

import com.pix.keys.dto.KeyType;

public interface KeyValidation {

    void validateKeyValue(String value);

    KeyType type();
}
