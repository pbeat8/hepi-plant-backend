package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.Category;
import com.hepiplant.backend.entity.SalesOffer;
import com.hepiplant.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesOfferRepository extends JpaRepository<SalesOffer, Long> {

    List<SalesOffer> findAllByCategory(Category category);
    List<SalesOffer> findAllByUser(User user);
    List<SalesOffer> findAllByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}
