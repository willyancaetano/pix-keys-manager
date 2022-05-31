package com.pix.keys.controller;

import com.pix.keys.dto.KeyType;
import com.pix.keys.dto.PixKeyCreationRequestDto;
import com.pix.keys.dto.PixKeyGeneratedResponseDto;
import com.pix.keys.dto.PixKeyUpdateValueRequestDto;
import com.pix.keys.dto.PixKeyUpdatedValueResponseDto;
import com.pix.keys.dto.SearchPixKeyRequestDto;
import com.pix.keys.dto.SearchPixKeyResponseDto;
import com.pix.keys.usecase.GeneratePixKeyUseCase;
import com.pix.keys.usecase.SearchPixKeysUseCase;
import com.pix.keys.usecase.UpdatePixKeyUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/pix-keys")
public class PixKeysController {

    private final GeneratePixKeyUseCase generatePixKeyUseCase;
    private final UpdatePixKeyUseCase updatePixKeyUseCase;
    private final SearchPixKeysUseCase searchPixKeysUseCase;

    public PixKeysController(GeneratePixKeyUseCase generatePixKeyUseCase, UpdatePixKeyUseCase updatePixKeyUseCase, SearchPixKeysUseCase searchPixKeysUseCase) {
        this.generatePixKeyUseCase = generatePixKeyUseCase;
        this.updatePixKeyUseCase = updatePixKeyUseCase;
        this.searchPixKeysUseCase = searchPixKeysUseCase;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public PixKeyGeneratedResponseDto createNewPixKey(@Valid @RequestBody PixKeyCreationRequestDto requestDto) {
        return generatePixKeyUseCase.processRequest(requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping
    public PixKeyUpdatedValueResponseDto updateValueKey(@Valid @RequestBody PixKeyUpdateValueRequestDto requestDto) {
        return updatePixKeyUseCase.updateValueKey(requestDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<SearchPixKeyResponseDto> searchPixKeys(@RequestHeader(value = "id", required = false) String id,
                                                       @RequestHeader(value = "type", required = false) String keyType,
                                                       @RequestHeader(value = "branch-number", required = false) Integer branchNumber,
                                                       @RequestHeader(value = "account-number", required = false) Integer accountNumber,
                                                       @RequestHeader(value = "holder-name", required = false) String accountHolderName,
                                                       @RequestHeader(value = "creation-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdAt,
                                                       @RequestHeader(value = "inactivation-date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateInactivateKey) {

        return searchPixKeysUseCase.searchPixKeys(SearchPixKeyRequestDto.builder()
                .id(id)
                .keyType(keyType == null ? null : KeyType.valueOf(keyType))
                .branchNumber(branchNumber)
                .accountNumber(accountNumber)
                .accountHolderName(accountHolderName)
                .creationDate(createdAt)
                .inactivationDate(dateInactivateKey).build());
    }

}
