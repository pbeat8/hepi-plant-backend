package com.hepiplant.backend.service;


import com.hepiplant.backend.dto.SalesOfferDto;

import java.util.Date;
import java.util.List;

public interface SalesOfferService {
    SalesOfferDto create(SalesOfferDto salesOfferDto);
    List<SalesOfferDto> getAll(Date startDate, Date endDate);
    List<SalesOfferDto> getAllByCategory(Long categoryId);
    List<SalesOfferDto> getAllByUser(Long userId);
    List<SalesOfferDto> getAllByTag(String tag);
    List<SalesOfferDto> getAllByFilters(Date startDate, Date endDate, String tag, Long categoryId);
    SalesOfferDto getById(Long id);
    SalesOfferDto update(Long id, SalesOfferDto salesOfferDto);
    String delete(Long id);
}

