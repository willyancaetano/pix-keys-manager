package com.github.willyancaetano.pix.keys.manager.service;

import com.github.willyancaetano.pix.keys.manager.dto.KeyDTO;
import com.github.willyancaetano.pix.keys.manager.model.AccountKey;
import com.github.willyancaetano.pix.keys.manager.model.AccountKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountKeyService {

    @Autowired
    private AccountKeyRepository repository;

    public void createNewKeyForAccount(final KeyDTO keyDTO) {

        AccountKey accountKey = new AccountKey(keyDTO.getAccountId());

        accountKey.addNewKey(keyDTO.getType(), keyDTO.getValue());

        repository.save(accountKey);
    }

}
