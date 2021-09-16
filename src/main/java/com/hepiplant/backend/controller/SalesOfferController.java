package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.SalesOfferDto;
import com.hepiplant.backend.service.SalesOfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/salesoffers")
public class SalesOfferController {

    private final SalesOfferService salesOfferService;

    public SalesOfferController(SalesOfferService salesOfferService) {
        this.salesOfferService = salesOfferService;
    }

    @PostMapping
    public ResponseEntity<SalesOfferDto> addSalesOffer(@RequestBody SalesOfferDto salesOfferDto){
        return ResponseEntity.ok().body(salesOfferService.create(salesOfferDto));
    }

    @GetMapping
    public ResponseEntity<List<SalesOfferDto>> getSalesOffers(){
        return ResponseEntity.ok().body(salesOfferService.getAll());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SalesOfferDto>> getSalesOffersByCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok().body(salesOfferService.getAllByCategory(categoryId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SalesOfferDto>> getSalesOffersByUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(salesOfferService.getAllByUser(userId));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<SalesOfferDto>> getSalesOffersByTag(@PathVariable String tag){
        return ResponseEntity.ok().body(salesOfferService.getAllByTag(tag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOfferDto> getSalesOfferById(@PathVariable Long id){
        return  ResponseEntity.ok().body(salesOfferService.getById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SalesOfferDto> update(@PathVariable Long id, @RequestBody SalesOfferDto salesOfferDto){
        return  ResponseEntity.ok().body(salesOfferService.update(id, salesOfferDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(salesOfferService.delete(id));
    }

}
