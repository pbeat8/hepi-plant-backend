package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);
    @Query(value =
            "SELECT count(*) FROM ( " +
                    "       SELECT * FROM forum.post_tag " +
                    "       WHERE tag_id = ?1 " +
                    "       UNION " +
                    "       SELECT * FROM forum.sales_offer_tag " +
                    "       WHERE tag_id = ?1) q ",
            nativeQuery = true)
    long countPostsAndSalesOffersByTagId(Long id);
}
