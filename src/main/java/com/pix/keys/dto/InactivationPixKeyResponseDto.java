package com.pix.keys.dto;

import java.time.LocalDateTime;

public record InactivationPixKeyResponseDto(
        String id,
        KeyType type,
        String valueKey,
        AccountType accountType,
        Integer branchNumber,
        Integer accountNumber,
        String accountHolderName,
        String accountHolderSurname,
        LocalDateTime creationDateTime,
        LocalDateTime inactivationDateTime) {
}
