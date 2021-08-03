package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {

    //public Optional<Species> findByPlacement();
}
