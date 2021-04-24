package com.github.willyancaetano.pix.keys.manager.controller;

import com.github.willyancaetano.pix.keys.manager.dto.KeyDTO;
import com.github.willyancaetano.pix.keys.manager.service.AccountKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/account/keys")
public class AccountKeyController {

    @Autowired
    private AccountKeyService service;

    @PostMapping
    public ResponseEntity<?> createNewKeyForAccount(@RequestBody KeyDTO keyDTO) {
        service.createNewKeyForAccount(keyDTO);
        return ResponseEntity.created(null).build();
    }
}
