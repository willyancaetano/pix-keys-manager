package com.pix.keys.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

public record PixKeyUpdateValueRequestDto(

        @NotNull
        UUID id,

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
        String accountHolderSurname) {
}
