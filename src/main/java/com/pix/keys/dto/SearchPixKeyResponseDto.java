package com.pix.keys.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public record SearchPixKeyResponseDto(
        @NotNull
        String id,

        @NotNull
        KeyType type,

        @NotBlank
        @Size(min = 1, max = 77)
        String valueKey,

        @NotNull
        AccountType accountType,

        @NotNull
        @Min(value = 1)
        @Max(value = 9999)
        Integer branchNumber,

        @NotNull
        @Min(value = 1)
        @Max(value = 99999999)
        Integer accountNumber,

        @NotNull
        @Size(min = 1, max = 30)
        String accountHolderName,

        @Size(min = 1, max = 45)
        String accountHolderSurname,

        @NotNull
        String creationDate,

        String inactivationDate) {
}
