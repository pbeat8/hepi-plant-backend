package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.entity.SalesOffer;
import com.hepiplant.backend.repository.SalesOfferRepository;
import com.hepiplant.backend.service.SalesOfferService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class SalesOfferServiceImpl implements SalesOfferService {
    public SalesOfferRepository salesOfferRepository;

    public SalesOfferServiceImpl(SalesOfferRepository salesOfferRepository) {
        this.salesOfferRepository = salesOfferRepository;
    }

    @Override
    public List<SalesOffer> getAll() {
        return salesOfferRepository.findAll();
    }

    @Override
    public SalesOffer getById(Long id) {
        return salesOfferRepository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }
}
