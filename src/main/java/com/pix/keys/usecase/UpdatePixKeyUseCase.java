package com.pix.keys.usecase;

import com.pix.keys.dto.KeyType;
import com.pix.keys.dto.PixKeyUpdateValueRequestDto;
import com.pix.keys.dto.PixKeyUpdatedValueResponseDto;
import com.pix.keys.exception.KeyTypeNotAllowedException;
import com.pix.keys.exception.PixKeyNotActiveException;
import com.pix.keys.exception.PixKeyNotExistsException;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.PixKeyRepository;
import com.pix.keys.validation.KeyValidation;
import com.pix.keys.validation.KeyValidationFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UpdatePixKeyUseCase {

    private final PixKeyRepository pixKeyRepository;

    public UpdatePixKeyUseCase(PixKeyRepository pixKeyRepository) {
        this.pixKeyRepository = pixKeyRepository;
    }

    public PixKeyUpdatedValueResponseDto updateValueKey(PixKeyUpdateValueRequestDto requestDto) {

        PixKey pixKey = validateIfPixKeyExists(requestDto.id().toString());

        validateIfKeyTypeIsRandom(pixKey);

        validateIfPixKeyIsNotActive(pixKey);

        validateKeyValue(pixKey.getType(), requestDto.valueKey());

        pixKey.setValue(requestDto.valueKey());

        PixKey pixKeyUpdated = pixKeyRepository.save(pixKey);

        return new PixKeyUpdatedValueResponseDto(UUID.fromString(pixKeyUpdated.getId()),
                pixKeyUpdated.getType(),
                pixKeyUpdated.getValue(),
                pixKeyUpdated.getAccount().getAccountType(),
                pixKeyUpdated.getAccount().getBranchNumber(),
                pixKeyUpdated.getAccount().getAccountNumber(),
                pixKeyUpdated.getAccount().getAccountHolderName(),
                pixKeyUpdated.getAccount().getAccountHolderSurname(),
                LocalDateTime.of(pixKeyUpdated.getCreationDate(), pixKeyUpdated.getCreationTime()));
    }

    private PixKey validateIfPixKeyExists(String id) {
        Optional<PixKey> optionalPixKey = pixKeyRepository.findById(id);

        if(!optionalPixKey.isPresent()) {
            throw new PixKeyNotExistsException("Id da chave não existe");
        }

        return optionalPixKey.get();
    }

    private void validateIfKeyTypeIsRandom(PixKey pixKey) {
        if(pixKey.getType() == KeyType.RANDOM) {
            throw new KeyTypeNotAllowedException("Tipo de chave não permitido a alteração");
        }
    }

    private void validateIfPixKeyIsNotActive(PixKey pixKey) {
        if(!pixKey.isActive()) {
            throw new PixKeyNotActiveException("Chave está inativada");
        }
    }

    private void validateKeyValue(KeyType type, String value) {
        KeyValidation validation = KeyValidationFactory.getValidation(type);

        validation.validateKeyValue(value);
    }
}
