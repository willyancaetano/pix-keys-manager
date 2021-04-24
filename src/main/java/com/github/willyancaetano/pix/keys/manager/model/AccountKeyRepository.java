package com.github.willyancaetano.pix.keys.manager.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountKeyRepository extends MongoRepository<AccountKey, String> {

}
