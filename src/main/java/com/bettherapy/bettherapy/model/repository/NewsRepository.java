package com.bettherapy.bettherapy.model.repository;

import com.bettherapy.bettherapy.model.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

    // Get only visible and not-expired news
    List<News> findByVisibleTrueAndExpiresAtAfterOrderByCreatedAtDesc(java.time.LocalDateTime now);

    // Optionally: get all visible regardless of expiration
    List<News> findByVisibleTrueOrderByCreatedAtDesc();

}
