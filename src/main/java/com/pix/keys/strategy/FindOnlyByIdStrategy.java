package com.pix.keys.strategy;

import com.pix.keys.dto.SearchPixKeyRequestDto;
import com.pix.keys.exception.PixKeyNotExistsException;
import com.pix.keys.model.PixKey;
import com.pix.keys.repository.PixKeyRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FindOnlyByIdStrategy implements FinderByPixKeys {

    private final PixKeyRepository pixKeyRepository;

    public FindOnlyByIdStrategy(PixKeyRepository pixKeyRepository) {
        this.pixKeyRepository = pixKeyRepository;
    }

    @Override
    public List<PixKey> findPixKeys(SearchPixKeyRequestDto requestDto) {

        PixKey pixKey = validateIfPixKeyExists(requestDto.getId());

        return List.of(pixKey);
    }

    private PixKey validateIfPixKeyExists(String id) {
        Optional<PixKey> optionalPixKey = pixKeyRepository.findById(id);

        if(!optionalPixKey.isPresent()) {
            throw new PixKeyNotExistsException("Id da chave não existe");
        }

        return optionalPixKey.get();
    }
}
