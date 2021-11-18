package com.hepiplant.backend.controller;

import com.hepiplant.backend.dto.SalesOfferDto;
import com.hepiplant.backend.service.SalesOfferService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(path = "/salesoffers")
public class SalesOfferController {

    private final SalesOfferService salesOfferService;

    public SalesOfferController(SalesOfferService salesOfferService) {
        this.salesOfferService = salesOfferService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SalesOfferDto> addSalesOffer(@RequestBody SalesOfferDto salesOfferDto){
        return ResponseEntity.ok().body(salesOfferService.create(salesOfferDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<SalesOfferDto>> getSalesOffers(@RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date startDate,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") Date endDate,
                                                              @RequestParam(required = false) String tag,
                                                              @RequestParam(required = false) Long categoryId){
        return ResponseEntity.ok().body(salesOfferService.getAllByFilters(startDate, endDate, tag, categoryId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SalesOfferDto>> getSalesOffersByCategory(@PathVariable Long categoryId){
        return ResponseEntity.ok().body(salesOfferService.getAllByCategory(categoryId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SalesOfferDto>> getSalesOffersByUser(@PathVariable Long userId){
        return ResponseEntity.ok().body(salesOfferService.getAllByUser(userId));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<SalesOfferDto>> getSalesOffersByTag(@PathVariable String tag){
        return ResponseEntity.ok().body(salesOfferService.getAllByTag(tag));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<SalesOfferDto> getSalesOfferById(@PathVariable Long id){
        return  ResponseEntity.ok().body(salesOfferService.getById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<SalesOfferDto> update(@PathVariable Long id, @RequestBody SalesOfferDto salesOfferDto){
        return  ResponseEntity.ok().body(salesOfferService.update(id, salesOfferDto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return  ResponseEntity.ok().body(salesOfferService.delete(id));
    }
}
