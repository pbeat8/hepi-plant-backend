package com.hepiplant.backend.service;


import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.dto.SalesOfferDto;

import java.util.List;

public interface SalesOfferService {
    SalesOfferDto create(SalesOfferDto salesOfferDto);
    List<SalesOfferDto> getAll();
    List<SalesOfferDto> getAllByCategory(Long categoryId);
    List<SalesOfferDto> getAllByUser(Long userId);
    List<SalesOfferDto> getAllByTag(String tag);
    SalesOfferDto getById(Long id);
    SalesOfferDto update(Long id, SalesOfferDto salesOfferDto);
    String delete(Long id);
}
