package com.artezo.repository;

import com.artezo.entity.BannerPage;
import com.artezo.entity.BannerSlide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SlideRepository extends JpaRepository<BannerSlide, Long> {

    List<BannerSlide> findByBannerPageId(Long pageId);

    @Modifying
    @Transactional
    @Query("DELETE FROM BannerSlide s WHERE s.bannerPage.id = :pageId")
    int deleteByBannerPageId(@Param("pageId") Long pageId);

    // Add this method for safer delete
    @Modifying
    @Transactional
    @Query("DELETE FROM BannerSlide s WHERE s.bannerPage = :page")
    void deleteByBannerPage(@Param("page") BannerPage page);
}