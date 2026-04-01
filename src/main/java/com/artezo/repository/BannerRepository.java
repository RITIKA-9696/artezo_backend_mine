package com.artezo.repository;

import com.artezo.entity.BannerPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BannerRepository extends JpaRepository<BannerPage, Long> {

    // Find by exact page name
    Optional<BannerPage> findByPageName(String pageName);

    // Find by status
    List<BannerPage> findByStatus(String status);

    // Check if page name exists
    boolean existsByPageName(String pageName);

    // Search by page name containing (case insensitive)
    List<BannerPage> findByPageNameContainingIgnoreCase(String pageName);
}