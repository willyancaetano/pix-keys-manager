package com.pix.keys.usecase;

import com.pix.keys.dto.InactivationPixKeyRequestDto;
import com.pix.keys.dto.InactivationPixKeyResponseDto;
import com.pix.keys.exception.PixKeyNotActiveException;
import com.pix.keys.exception.PixKeyNotExistsException;
import com.pix.keys.model.Account;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.PixKeyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class InactivatePixKeyUseCase {

    private final PixKeyRepository pixKeyRepository;

    public InactivatePixKeyUseCase(PixKeyRepository pixKeyRepository) {
        this.pixKeyRepository = pixKeyRepository;
    }

    public InactivationPixKeyResponseDto inactivatePixKey(InactivationPixKeyRequestDto requestDto) {
        PixKey pixKey = validateIfPixKeyExists(requestDto.id());

        validateIfPixKeyIsNotActive(pixKey);

        pixKey.inactivateKey();

        PixKey pixKeyInactivated = pixKeyRepository.save(pixKey);

        Account account = pixKeyInactivated.getAccount();

        return new InactivationPixKeyResponseDto(
                pixKeyInactivated.getId(),
                pixKeyInactivated.getType(),
                pixKeyInactivated.getValue(),
                account.getAccountType(),
                account.getBranchNumber(),
                account.getAccountNumber(),
                account.getAccountHolderName(),
                account.getAccountHolderSurname(),
                LocalDateTime.of(pixKey.getCreationDate(), pixKeyInactivated.getCreationTime()),
                LocalDateTime.of(pixKey.getInactivationDate(), pixKeyInactivated.getInactivationTime())
        );
    }

    private PixKey validateIfPixKeyExists(String id) {
        Optional<PixKey> optionalPixKey = pixKeyRepository.findById(id);

        if(!optionalPixKey.isPresent()) {
            throw new PixKeyNotExistsException("Id da chave não existe");
        }

        return optionalPixKey.get();
    }

    private void validateIfPixKeyIsNotActive(PixKey pixKey) {
        if(!pixKey.isActive()) {
            throw new PixKeyNotActiveException("Chave está inativada");
        }
    }
}
