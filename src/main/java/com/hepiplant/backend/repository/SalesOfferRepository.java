package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.SalesOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOfferRepository extends JpaRepository<SalesOffer, Long> {

    //public Optional<Species> findByPlacement();
}
