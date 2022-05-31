package com.pix.keys.strategy;

import com.pix.keys.dto.SearchPixKeyRequestDto;
import com.pix.keys.model.PixKey;

import java.util.List;

public interface FinderByPixKeys {

    List<PixKey> findPixKeys(SearchPixKeyRequestDto requestDto);
}
