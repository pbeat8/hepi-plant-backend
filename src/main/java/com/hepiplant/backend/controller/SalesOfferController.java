package com.hepiplant.backend.controller;

import com.hepiplant.backend.service.CategoryService;
import com.hepiplant.backend.service.SalesOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/salesoffers")
public class SalesOfferController {
    private final SalesOfferService salesOfferService;

    public SalesOfferController(SalesOfferService salesOfferService) {
        this.salesOfferService = salesOfferService;
    }

    @GetMapping
    public ResponseEntity<?> getSalesOffers(){
        return ResponseEntity.ok().body(salesOfferService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSalesOfferById(@PathVariable Long id){
        return  ResponseEntity.ok().body(salesOfferService.getById(id));
    }
}
