package com.pix.keys.usecase;

import com.pix.keys.dto.PixKeyCreationRequestDto;
import com.pix.keys.dto.PixKeyGeneratedResponseDto;
import com.pix.keys.exception.NumberOfKeysExceededException;
import com.pix.keys.exception.PixKeyAlreadyExistsException;
import com.pix.keys.model.Account;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.AccountRepository;
import com.pix.keys.repository.PixKeyRepository;
import com.pix.keys.validation.KeyValidation;
import com.pix.keys.validation.KeyValidationFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeneratePixKeyUseCase {

    private final AccountRepository accountRepository;
    private final PixKeyRepository pixKeyRepository;

    public GeneratePixKeyUseCase(AccountRepository repository, PixKeyRepository pixKeyRepository) {
        this.accountRepository = repository;
        this.pixKeyRepository = pixKeyRepository;
    }

    public PixKeyGeneratedResponseDto processRequest(PixKeyCreationRequestDto requestDto) {

        validateKeyValue(requestDto);

        validateIfPixKeyAlreadyExists(requestDto.valueKey());

        Account account = findByExistingAccountOrCreateNew(requestDto);

        validateNumberOfPixKeysByAccount(account);

        PixKey pixKey = new PixKey(requestDto.type(), requestDto.valueKey(), account);

        return new PixKeyGeneratedResponseDto(pixKeyRepository.save(pixKey).getId());
    }

    private void validateKeyValue(PixKeyCreationRequestDto requestDto) {
        KeyValidation validation = KeyValidationFactory.getValidation(requestDto.type());

        validation.validateKeyValue(requestDto.valueKey());
    }

    private void validateIfPixKeyAlreadyExists(String value) {
        Optional<PixKey> optionalPixKey = pixKeyRepository.findByValue(value);

        if(optionalPixKey.isPresent()){
            throw new PixKeyAlreadyExistsException("Chave pix já existe");
        }
    }

    private Account findByExistingAccountOrCreateNew(PixKeyCreationRequestDto requestDto) {

        Optional<Account> optionalAccount =
                accountRepository.findByAccountTypeAndBranchNumberAndAccountNumber(requestDto.accountType(),
                        requestDto.branchNumber(), requestDto.accountNumber());

        if(optionalAccount.isPresent()) {
            return optionalAccount.get();
        } else {
            Account account = new Account(requestDto.accountType(),
                    requestDto.branchNumber(),
                    requestDto.accountNumber(),
                    requestDto.accountHolderName(),
                    requestDto.accountHolderSurname(),
                    requestDto.personType());

            accountRepository.save(account);
            return account;
        }
    }

    private void validateNumberOfPixKeysByAccount(Account account) {
        List<PixKey> keys = account.getKeys().stream().filter(PixKey::isActive).toList();

        if(account.isNaturalPerson() && keys.size() == 5) {
            throw new NumberOfKeysExceededException("Foi excedido a quantidade máxima de chaves para pessoa física");
        }else if(account.isJuridicalPerson() && keys.size() == 20) {
            throw new NumberOfKeysExceededException("Foi excedido a quantidade máxima de chaves para pessoa jurídica");
        }
    }
}
