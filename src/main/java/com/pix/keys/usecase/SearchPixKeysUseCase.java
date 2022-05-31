package com.pix.keys.usecase;

import com.pix.keys.dto.SearchPixKeyRequestDto;
import com.pix.keys.dto.SearchPixKeyResponseDto;
import com.pix.keys.model.PixKey;
import com.pix.keys.strategy.FindByParametersStrategy;
import com.pix.keys.strategy.FindOnlyByIdStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SearchPixKeysUseCase {

    private final FindOnlyByIdStrategy findOnlyByIdStrategy;

    private final FindByParametersStrategy findByParametersStrategy;

    public SearchPixKeysUseCase(FindOnlyByIdStrategy findOnlyByIdStrategy, FindByParametersStrategy findByParametersStrategy) {
        this.findOnlyByIdStrategy = findOnlyByIdStrategy;
        this.findByParametersStrategy = findByParametersStrategy;
    }

    public List<SearchPixKeyResponseDto> searchPixKeys(SearchPixKeyRequestDto requestDto) {

        executeValidations(requestDto);

        if(requestDto.containsId()) {

            List<PixKey> pixKeys = findOnlyByIdStrategy.findPixKeys(requestDto);

            return convertDataToDto(pixKeys);
        } else {
            List<PixKey> pixKeys = findByParametersStrategy.findPixKeys(requestDto);

            return convertDataToDto(pixKeys);
        }
    }

    private void executeValidations(SearchPixKeyRequestDto requestDto) {
        if(requestDto.containIdAndOthersFields()){
            throw new IllegalArgumentException("Quando a consulta possui ID, não é aceito nenhum outro parâmetro");
        }

        if(requestDto.containsCreationDate() && requestDto.containsInactivationDate()) {
            throw new IllegalArgumentException("Filtros data de inclusão da chave e data da inativação da chave não" +
                    "são permitidos juntos");
        }
    }

    private List<SearchPixKeyResponseDto> convertDataToDto(List<PixKey> pixKeys) {

        List<SearchPixKeyResponseDto> responseDto = new ArrayList<>();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        pixKeys.stream().forEach(
                pixKey -> {
                    responseDto.add(new SearchPixKeyResponseDto(pixKey.getId(),
                            pixKey.getType(),
                            pixKey.getValue(),
                            pixKey.getAccount().getAccountType(),
                            pixKey.getAccount().getBranchNumber(),
                            pixKey.getAccount().getAccountNumber(),
                            pixKey.getAccount().getAccountHolderName(),
                            pixKey.getAccount().getAccountHolderSurname(),
                            dateTimeFormatter.format(LocalDateTime.of(pixKey.getCreationDate(), pixKey.getCreationTime())),
                            pixKey.getInactivationDate() == null && pixKey.getInactivationTime() == null ? "" :
                                    dateTimeFormatter.format(LocalDateTime.of(pixKey.getInactivationDate(), pixKey.getInactivationTime()))));
                }
        );

        return responseDto;
    }
}
