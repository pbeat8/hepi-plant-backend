package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.SalesOfferComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalesOfferCommentRepository extends JpaRepository<SalesOfferComment, Long> {

}
