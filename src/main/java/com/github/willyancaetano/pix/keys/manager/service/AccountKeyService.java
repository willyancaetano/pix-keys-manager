package com.github.willyancaetano.pix.keys.manager.service;

import com.github.willyancaetano.pix.keys.manager.dto.KeyDTO;
import com.github.willyancaetano.pix.keys.manager.model.AccountKey;
import com.github.willyancaetano.pix.keys.manager.model.AccountKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountKeyService {

    @Autowired
    private AccountKeyRepository repository;

    public KeyDTO createNewKeyForAccount(final KeyDTO keyDTO) {

        Optional<AccountKey> accountKeyOptional = repository.findByAccountId(keyDTO.getAccountId());

        AccountKey accountKey = retrieveOrCreateAccountKey(keyDTO, accountKeyOptional);

        accountKey.addNewKey(keyDTO.getType(), keyDTO.getValue());

        AccountKey accountKeySaved = repository.save(accountKey);

        return new KeyDTO(accountKeySaved.getAccountId(), keyDTO.getType(), Optional.of(accountKeySaved.getKeys().get(keyDTO.getType())));
    }

    private AccountKey retrieveOrCreateAccountKey(final KeyDTO keyDTO, final Optional<AccountKey> accountKeyOptional) {
        AccountKey accountKey = null;

        if(accountKeyOptional.isPresent()){
            accountKey = accountKeyOptional.get();
        }else{
            accountKey = new AccountKey(keyDTO.getAccountId());;
        }
        return accountKey;
    }

}
