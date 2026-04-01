package com.artezo.service;

import com.artezo.dto.request.BannerRequestDto;
import com.artezo.dto.request.BannerSummaryDto;
import com.artezo.dto.request.SlideDto;
import com.artezo.dto.response.BannerResponseDto;

import java.util.List;

public interface BannerService {

    // ============== PAGE OPERATIONS ==============
    List<BannerSummaryDto> getAllPages();
    BannerResponseDto getPageById(Long id);
    BannerResponseDto getPageByName(String pageName);
    BannerResponseDto createPage(BannerRequestDto request);
    BannerResponseDto updatePage(Long id, BannerRequestDto request);
    BannerResponseDto patchPage(Long id, BannerRequestDto request);
    void deletePage(Long id);

    // ============== SLIDE OPERATIONS ==============
    BannerResponseDto addSlide(Long pageId, SlideDto slideDto);
    BannerResponseDto updateSlide(Long pageId, Integer dotPosition, SlideDto slideDto);
    BannerResponseDto deleteSlide(Long pageId, Integer dotPosition);
    BannerResponseDto reorderSlides(Long pageId, List<Integer> newOrder);

    // ============== IMAGE OPERATIONS ==============
    byte[] getImageById(Long imageId);
    byte[] getLeftMainImage(Long pageId, Long slideId);
    byte[] getRightTopImage(Long pageId, Long slideId);
    byte[] getBannerFile(Long pageId, String fileType);  // ADD THIS LINE

    // ============== SEARCH/FILTER ==============
    List<BannerSummaryDto> searchPages(String searchTerm);
    List<BannerSummaryDto> getPagesByStatus(String status);
    List<BannerSummaryDto> getActivePages();
}