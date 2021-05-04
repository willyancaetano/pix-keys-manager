package com.github.willyancaetano.pix.keys.manager.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountKeyRepository extends MongoRepository<AccountKey, String> {

    Optional<AccountKey> findByAccountId(String accountId);
}
