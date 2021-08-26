package com.hepiplant.backend.service;


import com.hepiplant.backend.entity.SalesOffer;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SalesOfferService {
    List<SalesOffer> getAll();
    SalesOffer getById(Long id);
}
