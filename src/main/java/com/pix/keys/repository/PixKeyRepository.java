package com.pix.keys.repository;

import com.pix.keys.model.PixKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PixKeyRepository extends JpaRepository<PixKey, String> {

    Optional<PixKey> findByValue(String value);
}
