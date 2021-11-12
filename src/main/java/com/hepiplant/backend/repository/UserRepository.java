package com.hepiplant.backend.repository;

import com.hepiplant.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUid(String uId);
    Optional<User> findByEmail(String email);
    @Query(value =
            "SELECT count(*) FROM ( " +
            "       SELECT * FROM forum.post_comments " +
            "       WHERE user_id = ?1 " +
            "       UNION " +
            "       SELECT * FROM forum.sales_offer_comments " +
            "       WHERE user_id = ?1) q ",
    nativeQuery = true)
    long getCommentsCountByUserId(Long id);
}
