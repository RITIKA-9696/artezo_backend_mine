package com.artezo.service.serviceImpl;

import com.artezo.dto.request.BannerRequestDto;
import com.artezo.dto.request.BannerSummaryDto;
import com.artezo.dto.request.SlideDto;
import com.artezo.dto.response.BannerResponseDto;
import com.artezo.entity.BannerPage;
import com.artezo.entity.BannerSlide;
import com.artezo.repository.BannerRepository;
import com.artezo.repository.SlideRepository;
import com.artezo.service.BannerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BannerServiceImpl implements BannerService {

    private static final Logger log = LoggerFactory.getLogger(BannerServiceImpl.class);

    private final BannerRepository bannerRepository;
    private final SlideRepository slideRepository;

    public BannerServiceImpl(BannerRepository bannerRepository, SlideRepository slideRepository) {
        this.bannerRepository = bannerRepository;
        this.slideRepository = slideRepository;
    }

    private BannerResponseDto convertToResponse(BannerPage page) {
        BannerResponseDto dto = new BannerResponseDto();
        dto.setId(page.getId());
        dto.setPageName(page.getPageName());

        List<SlideDto> slideDtos = page.getSlides().stream()
                .map(this::convertSlideToDto)
                .collect(Collectors.toList());
        dto.setSlides(slideDtos);

        dto.setBannerFileTwoUrl(page.getBannerFileTwoUrl());
        dto.setBannerFileThreeUrl(page.getBannerFileThreeUrl());
        dto.setBannerFileFourUrl(page.getBannerFileFourUrl());
        dto.setStatus(page.getStatus());
        dto.setCreatedAt(page.getCreatedAt());
        dto.setUpdatedAt(page.getUpdatedAt());

        return dto;
    }

    private SlideDto convertSlideToDto(BannerSlide slide) {
        SlideDto dto = new SlideDto();
        dto.setDotPosition(slide.getDotPosition());

        SlideDto.LeftMain leftMain = new SlideDto.LeftMain();
        leftMain.setTitle(slide.getLeftMainTitle());
        if (slide.getLeftMainImageUrl() != null && !slide.getLeftMainImageUrl().isEmpty()) {
            leftMain.setImageUrl(slide.getLeftMainImageUrl());
        }
        leftMain.setRedirectUrl(slide.getLeftMainRedirectUrl());
        dto.setLeftMain(leftMain);

        SlideDto.RightTop rightTop = new SlideDto.RightTop();
        if (slide.getRightTopImageUrl() != null && !slide.getRightTopImageUrl().isEmpty()) {
            rightTop.setImageUrl(slide.getRightTopImageUrl());
        }
        rightTop.setRedirectUrl(slide.getRightTopRedirectUrl());
        dto.setRightTop(rightTop);

        SlideDto.RightCard rightCard = new SlideDto.RightCard();
        rightCard.setTitle(slide.getRightCardTitle());
        rightCard.setDescription(slide.getRightCardDescription());
        dto.setRightCard(rightCard);

        return dto;
    }

    private BannerSummaryDto convertToSummary(BannerPage page) {
        BannerSummaryDto dto = new BannerSummaryDto();
        dto.setId(page.getId());
        dto.setPageName(page.getPageName());
        dto.setSlidesCount(page.getSlides().size());
        dto.setBannerFileTwo(page.getBannerFileTwoUrl());
        dto.setBannerFileThree(page.getBannerFileThreeUrl());
        dto.setBannerFileFour(page.getBannerFileFourUrl());
        dto.setStatus(page.getStatus());
        dto.setCreatedAt(page.getCreatedAt());
//        dto.setUpdatedAt(page.getUpdatedAt());
        return dto;
    }

    @Override
    public List<BannerSummaryDto> getAllPages() {
        log.info("Fetching all banner pages");
        return bannerRepository.findAll().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }

    @Override
    public BannerResponseDto getPageById(Long id) {
        log.info("Fetching banner page with id: {}", id);
        BannerPage page = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + id));
        return convertToResponse(page);
    }

    @Override
    public BannerResponseDto getPageByName(String pageName) {
        log.info("Fetching banner page with name: {}", pageName);
        BannerPage page = bannerRepository.findByPageName(pageName)
                .orElseThrow(() -> new RuntimeException("Page not found with name: " + pageName));
        return convertToResponse(page);
    }

    @Override
    @Transactional
    public BannerResponseDto createPage(BannerRequestDto request) {
        log.info("Creating new banner page: {}", request.getPageName());

        if (request.getPageName() == null || request.getPageName().trim().isEmpty()) {
            throw new RuntimeException("Page name is required");
        }

        if (bannerRepository.existsByPageName(request.getPageName())) {
            throw new RuntimeException("Page with name '" + request.getPageName() + "' already exists");
        }

        // Create and save page
        BannerPage page = new BannerPage();
        page.setPageName(request.getPageName().trim());
        page.setStatus(request.getStatus() != null ? request.getStatus() : "draft");

        // Set banner files
        if (request.getBannerFileTwo() != null && request.getBannerFileTwo().length > 0) {
            page.setBannerFileTwo(request.getBannerFileTwo());
        }
        if (request.getBannerFileThree() != null && request.getBannerFileThree().length > 0) {
            page.setBannerFileThree(request.getBannerFileThree());
        }
        if (request.getBannerFileFour() != null && request.getBannerFileFour().length > 0) {
            page.setBannerFileFour(request.getBannerFileFour());
        }

        // Save page to get ID
        BannerPage savedPage = bannerRepository.save(page);
        log.info("Saved page with ID: {}", savedPage.getId());

        // Set banner URLs
        if (savedPage.getBannerFileTwo() != null && savedPage.getBannerFileTwo().length > 0) {
            savedPage.setBannerFileTwoUrl("/api/banners/get-banner-file/" + savedPage.getId() + "/file-two");
        }
        if (savedPage.getBannerFileThree() != null && savedPage.getBannerFileThree().length > 0) {
            savedPage.setBannerFileThreeUrl("/api/banners/get-banner-file/" + savedPage.getId() + "/file-three");
        }
        if (savedPage.getBannerFileFour() != null && savedPage.getBannerFileFour().length > 0) {
            savedPage.setBannerFileFourUrl("/api/banners/get-banner-file/" + savedPage.getId() + "/file-four");
        }

        savedPage = bannerRepository.save(savedPage);

        // Process slides
        if (request.getSlides() != null && !request.getSlides().isEmpty()) {
            log.info("Processing {} slides", request.getSlides().size());

            List<BannerSlide> slidesToSave = new ArrayList<>();

            for (int i = 0; i < request.getSlides().size(); i++) {
                SlideDto slideDto = request.getSlides().get(i);

                BannerSlide slide = new BannerSlide();
                slide.setDotPosition(slideDto.getDotPosition() != null ? slideDto.getDotPosition() : i + 1);
                slide.setBannerPage(savedPage);

                if (slideDto.getLeftMain() != null) {
                    slide.setLeftMainTitle(slideDto.getLeftMain().getTitle());
                    if (slideDto.getLeftMain().getImage() != null && slideDto.getLeftMain().getImage().length > 0) {
                        slide.setLeftMainImage(slideDto.getLeftMain().getImage());
                        log.info("Slide {} leftMain image size: {} bytes", i, slideDto.getLeftMain().getImage().length);
                    }
                    slide.setLeftMainRedirectUrl(slideDto.getLeftMain().getRedirectUrl() != null ?
                            slideDto.getLeftMain().getRedirectUrl() : "#");
                }

                if (slideDto.getRightTop() != null) {
                    if (slideDto.getRightTop().getImage() != null && slideDto.getRightTop().getImage().length > 0) {
                        slide.setRightTopImage(slideDto.getRightTop().getImage());
                        log.info("Slide {} rightTop image size: {} bytes", i, slideDto.getRightTop().getImage().length);
                    }
                    slide.setRightTopRedirectUrl(slideDto.getRightTop().getRedirectUrl() != null ?
                            slideDto.getRightTop().getRedirectUrl() : "#");
                }

                if (slideDto.getRightCard() != null) {
                    slide.setRightCardTitle(slideDto.getRightCard().getTitle() != null ?
                            slideDto.getRightCard().getTitle() : "");
                    slide.setRightCardDescription(slideDto.getRightCard().getDescription() != null ?
                            slideDto.getRightCard().getDescription() : "");
                }

                slidesToSave.add(slide);
            }

            // Save all slides
            List<BannerSlide> savedSlides = slideRepository.saveAll(slidesToSave);
            log.info("Saved {} slides", savedSlides.size());

            // Set image URLs
            for (BannerSlide slide : savedSlides) {
                if (slide.getLeftMainImage() != null && slide.getLeftMainImage().length > 0) {
                    slide.setLeftMainImageUrl("/api/banners/get-left-main-image/" + savedPage.getId() + "/" + slide.getId());
                }
                if (slide.getRightTopImage() != null && slide.getRightTopImage().length > 0) {
                    slide.setRightTopImageUrl("/api/banners/get-right-top-image/" + savedPage.getId() + "/" + slide.getId());
                }
            }

            // Update slides with URLs
            slideRepository.saveAll(savedSlides);

            // Set slides in page
            savedPage.setSlides(savedSlides);
        }

        BannerPage finalPage = bannerRepository.save(savedPage);
        log.info("✓ Banner page '{}' created successfully with {} slides",
                finalPage.getPageName(), finalPage.getSlides().size());

        return convertToResponse(finalPage);
    }

    @Override
    @Transactional
    public BannerResponseDto updatePage(Long id, BannerRequestDto request) {
        log.info("Updating banner page with id: {}", id);

        BannerPage page = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + id));

        // Update basic info
        if (request.getPageName() != null && !request.getPageName().trim().isEmpty()) {
            if (!page.getPageName().equals(request.getPageName()) &&
                    bannerRepository.existsByPageName(request.getPageName())) {
                throw new RuntimeException("Page with name '" + request.getPageName() + "' already exists");
            }
            page.setPageName(request.getPageName().trim());
        }

        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            page.setStatus(request.getStatus());
        }

        // Update banner files
        if (request.getBannerFileTwo() != null && request.getBannerFileTwo().length > 0) {
            page.setBannerFileTwo(request.getBannerFileTwo());
            page.setBannerFileTwoUrl("/api/banners/get-banner-file/" + page.getId() + "/file-two");
            log.info("Banner file two updated");
        }
        if (request.getBannerFileThree() != null && request.getBannerFileThree().length > 0) {
            page.setBannerFileThree(request.getBannerFileThree());
            page.setBannerFileThreeUrl("/api/banners/get-banner-file/" + page.getId() + "/file-three");
            log.info("Banner file three updated");
        }
        if (request.getBannerFileFour() != null && request.getBannerFileFour().length > 0) {
            page.setBannerFileFour(request.getBannerFileFour());
            page.setBannerFileFourUrl("/api/banners/get-banner-file/" + page.getId() + "/file-four");
            log.info("Banner file four updated");
        }

        // Update slides
        if (request.getSlides() != null && !request.getSlides().isEmpty()) {
            log.info("Updating {} slides", request.getSlides().size());

            // ✅ FIX: Don't clear the collection directly
            // Instead, delete all existing slides using repository
            List<BannerSlide> existingSlides = slideRepository.findByBannerPageId(id);

            if (!existingSlides.isEmpty()) {
                // Delete all existing slides
                slideRepository.deleteAll(existingSlides);
                log.info("Deleted {} existing slides", existingSlides.size());

                // Clear the reference in the page entity
                page.getSlides().clear();

                // Flush to ensure deletion is executed
                slideRepository.flush();
            }

            // Create new slides
            List<BannerSlide> newSlides = new ArrayList<>();

            for (int i = 0; i < request.getSlides().size(); i++) {
                SlideDto slideDto = request.getSlides().get(i);
                Integer dotPosition = slideDto.getDotPosition() != null ? slideDto.getDotPosition() : i + 1;

                BannerSlide slide = new BannerSlide();
                slide.setDotPosition(dotPosition);
                slide.setBannerPage(page);

                if (slideDto.getLeftMain() != null) {
                    slide.setLeftMainTitle(slideDto.getLeftMain().getTitle());
                    if (slideDto.getLeftMain().getImage() != null && slideDto.getLeftMain().getImage().length > 0) {
                        slide.setLeftMainImage(slideDto.getLeftMain().getImage());
                        log.info("Slide {} leftMain image updated: {} bytes", i, slideDto.getLeftMain().getImage().length);
                    }
                    slide.setLeftMainRedirectUrl(slideDto.getLeftMain().getRedirectUrl() != null ?
                            slideDto.getLeftMain().getRedirectUrl() : "#");
                }

                if (slideDto.getRightTop() != null) {
                    if (slideDto.getRightTop().getImage() != null && slideDto.getRightTop().getImage().length > 0) {
                        slide.setRightTopImage(slideDto.getRightTop().getImage());
                        log.info("Slide {} rightTop image updated: {} bytes", i, slideDto.getRightTop().getImage().length);
                    }
                    slide.setRightTopRedirectUrl(slideDto.getRightTop().getRedirectUrl() != null ?
                            slideDto.getRightTop().getRedirectUrl() : "#");
                }

                if (slideDto.getRightCard() != null) {
                    slide.setRightCardTitle(slideDto.getRightCard().getTitle() != null ?
                            slideDto.getRightCard().getTitle() : "");
                    slide.setRightCardDescription(slideDto.getRightCard().getDescription() != null ?
                            slideDto.getRightCard().getDescription() : "");
                }

                newSlides.add(slide);
            }

            // Save new slides
            List<BannerSlide> savedSlides = slideRepository.saveAll(newSlides);
            log.info("Saved {} new slides", savedSlides.size());

            // Set image URLs for new slides
            for (BannerSlide slide : savedSlides) {
                if (slide.getLeftMainImage() != null && slide.getLeftMainImage().length > 0) {
                    slide.setLeftMainImageUrl("/api/banners/get-left-main-image/" + page.getId() + "/" + slide.getId());
                    log.info("Slide {} left main URL set", slide.getId());
                }
                if (slide.getRightTopImage() != null && slide.getRightTopImage().length > 0) {
                    slide.setRightTopImageUrl("/api/banners/get-right-top-image/" + page.getId() + "/" + slide.getId());
                    log.info("Slide {} right top URL set", slide.getId());
                }
            }

            // Update slides with URLs
            savedSlides = slideRepository.saveAll(savedSlides);

            // ✅ FIX: Set the new slides collection properly
            page.setSlides(savedSlides);

            log.info("Slides updated successfully - {} slides saved", savedSlides.size());
        }

        BannerPage updatedPage = bannerRepository.save(page);
        log.info("✓ Banner page '{}' updated successfully", updatedPage.getPageName());
        return convertToResponse(updatedPage);
    }

    @Override
    @Transactional
    public BannerResponseDto patchPage(Long id, BannerRequestDto request) {
        log.info("Patching banner page with id: {}", id);
        BannerPage page = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + id));
        if (request.getPageName() != null && !request.getPageName().isEmpty()) {
            page.setPageName(request.getPageName());
        }
        if (request.getStatus() != null && !request.getStatus().isEmpty()) {
            page.setStatus(request.getStatus());
        }
        BannerPage patchedPage = bannerRepository.save(page);
        log.info("✓ Banner page patched successfully");
        return convertToResponse(patchedPage);
    }

    @Override
    @Transactional
    public void deletePage(Long id) {
        log.info("Deleting banner page with id: {}", id);

        try {
            // First check if page exists
            BannerPage page = bannerRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Page not found with id: " + id));

            // Delete slides first
            List<BannerSlide> slides = page.getSlides();
            if (!slides.isEmpty()) {
                slideRepository.deleteAll(slides);
                log.info("Deleted {} slides for page {}", slides.size(), id);
            }

            // Then delete page
            bannerRepository.delete(page);
            log.info("✓ Banner page {} deleted successfully", id);

        } catch (Exception e) {
            log.error("Error deleting page {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete page: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public BannerResponseDto addSlide(Long pageId, SlideDto slideDto) {
        log.info("Adding slide to page {}", pageId);
        BannerPage page = bannerRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + pageId));

        BannerSlide slide = new BannerSlide();
        slide.setDotPosition(slideDto.getDotPosition() != null ? slideDto.getDotPosition() : page.getSlides().size() + 1);
        slide.setBannerPage(page);

        if (slideDto.getLeftMain() != null) {
            slide.setLeftMainTitle(slideDto.getLeftMain().getTitle());
            if (slideDto.getLeftMain().getImage() != null) {
                slide.setLeftMainImage(slideDto.getLeftMain().getImage());
            }
            slide.setLeftMainRedirectUrl(slideDto.getLeftMain().getRedirectUrl());
        }

        if (slideDto.getRightTop() != null) {
            if (slideDto.getRightTop().getImage() != null) {
                slide.setRightTopImage(slideDto.getRightTop().getImage());
            }
            slide.setRightTopRedirectUrl(slideDto.getRightTop().getRedirectUrl());
        }

        if (slideDto.getRightCard() != null) {
            slide.setRightCardTitle(slideDto.getRightCard().getTitle());
            slide.setRightCardDescription(slideDto.getRightCard().getDescription());
        }

        BannerSlide savedSlide = slideRepository.save(slide);

        if (savedSlide.getLeftMainImage() != null && savedSlide.getLeftMainImage().length > 0) {
            savedSlide.setLeftMainImageUrl("/api/banners/get-left-main-image/" + pageId + "/" + savedSlide.getId());
        }
        if (savedSlide.getRightTopImage() != null && savedSlide.getRightTopImage().length > 0) {
            savedSlide.setRightTopImageUrl("/api/banners/get-right-top-image/" + pageId + "/" + savedSlide.getId());
        }

        slideRepository.save(savedSlide);
        page.addSlide(savedSlide);
        BannerPage updatedPage = bannerRepository.save(page);

        return convertToResponse(updatedPage);
    }

    // Other methods (updateSlide, deleteSlide, reorderSlides, getImage methods) remain same...
    @Override
    @Transactional
    public BannerResponseDto updateSlide(Long pageId, Integer dotPosition, SlideDto slideDto) {
        // Keep existing implementation
        log.info("Updating slide at position {} for page {}", dotPosition, pageId);
        BannerPage page = bannerRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + pageId));
        BannerSlide slideToUpdate = page.getSlides().stream()
                .filter(s -> s.getDotPosition().equals(dotPosition))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Slide with dotPosition " + dotPosition + " not found"));
        if (slideDto.getLeftMain() != null) {
            slideToUpdate.setLeftMainTitle(slideDto.getLeftMain().getTitle());
            if (slideDto.getLeftMain().getImage() != null) {
                slideToUpdate.setLeftMainImage(slideDto.getLeftMain().getImage());
            }
            slideToUpdate.setLeftMainRedirectUrl(slideDto.getLeftMain().getRedirectUrl());
        }
        if (slideDto.getRightTop() != null) {
            if (slideDto.getRightTop().getImage() != null) {
                slideToUpdate.setRightTopImage(slideDto.getRightTop().getImage());
            }
            slideToUpdate.setRightTopRedirectUrl(slideDto.getRightTop().getRedirectUrl());
        }
        if (slideDto.getRightCard() != null) {
            slideToUpdate.setRightCardTitle(slideDto.getRightCard().getTitle());
            slideToUpdate.setRightCardDescription(slideDto.getRightCard().getDescription());
        }
        slideRepository.save(slideToUpdate);
        BannerPage updatedPage = bannerRepository.save(page);
        return convertToResponse(updatedPage);
    }

    @Override
    @Transactional
    public BannerResponseDto deleteSlide(Long pageId, Integer dotPosition) {
        // Keep existing implementation
        log.info("Deleting slide at position {} from page {}", dotPosition, pageId);
        BannerPage page = bannerRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + pageId));
        BannerSlide slideToRemove = page.getSlides().stream()
                .filter(s -> s.getDotPosition().equals(dotPosition))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Slide with dotPosition " + dotPosition + " not found"));
        Long slideId = slideToRemove.getId();
        page.getSlides().remove(slideToRemove);
        slideRepository.deleteById(slideId);
        List<BannerSlide> sortedSlides = page.getSlides().stream()
                .sorted(Comparator.comparing(BannerSlide::getDotPosition))
                .collect(Collectors.toList());
        for (int i = 0; i < sortedSlides.size(); i++) {
            sortedSlides.get(i).setDotPosition(i + 1);
        }
        slideRepository.saveAll(sortedSlides);
        BannerPage updatedPage = bannerRepository.save(page);
        return convertToResponse(updatedPage);
    }

    @Override
    @Transactional
    public BannerResponseDto reorderSlides(Long pageId, List<Integer> newOrder) {
        // Keep existing implementation
        log.info("Reordering slides for page {}", pageId);
        BannerPage page = bannerRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + pageId));
        List<BannerSlide> slides = new ArrayList<>(page.getSlides());
        if (slides.size() != newOrder.size()) {
            throw new RuntimeException("New order size doesn't match number of slides");
        }
        List<BannerSlide> reordered = new ArrayList<>();
        for (int i = 0; i < newOrder.size(); i++) {
            Integer oldPosition = newOrder.get(i);
            for (BannerSlide slide : slides) {
                if (slide.getDotPosition().equals(oldPosition)) {
                    slide.setDotPosition(i + 1);
                    reordered.add(slide);
                    break;
                }
            }
        }
        page.getSlides().clear();
        for (BannerSlide slide : reordered) {
            page.addSlide(slide);
        }
        slideRepository.saveAll(reordered);
        BannerPage updatedPage = bannerRepository.save(page);
        return convertToResponse(updatedPage);
    }

    @Override
    public byte[] getImageById(Long imageId) {
        return null;
    }

    @Override
    public byte[] getLeftMainImage(Long pageId, Long slideId) {
        log.info("Fetching left main image for page {}, slide {}", pageId, slideId);
        BannerSlide slide = slideRepository.findById(slideId)
                .orElseThrow(() -> new RuntimeException("Slide not found with id: " + slideId));
        if (!slide.getBannerPage().getId().equals(pageId)) {
            throw new RuntimeException("Slide does not belong to page " + pageId);
        }
        return slide.getLeftMainImage();
    }

    @Override
    public byte[] getRightTopImage(Long pageId, Long slideId) {
        log.info("Fetching right top image for page {}, slide {}", pageId, slideId);
        BannerSlide slide = slideRepository.findById(slideId)
                .orElseThrow(() -> new RuntimeException("Slide not found with id: " + slideId));
        if (!slide.getBannerPage().getId().equals(pageId)) {
            throw new RuntimeException("Slide does not belong to page " + pageId);
        }
        return slide.getRightTopImage();
    }

    @Override
    public byte[] getBannerFile(Long pageId, String fileType) {
        log.info("Fetching banner file '{}' for page {}", fileType, pageId);
        BannerPage page = bannerRepository.findById(pageId)
                .orElseThrow(() -> new RuntimeException("Page not found with id: " + pageId));
        switch (fileType.toLowerCase()) {
            case "file-two": return page.getBannerFileTwo();
            case "file-three": return page.getBannerFileThree();
            case "file-four": return page.getBannerFileFour();
            default: throw new RuntimeException("Invalid file type: " + fileType);
        }
    }

    @Override
    public List<BannerSummaryDto> searchPages(String searchTerm) {
        log.info("Searching pages with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPages();
        }
        List<BannerPage> pages = bannerRepository.findByPageNameContainingIgnoreCase(searchTerm);
        return pages.stream().map(this::convertToSummary).collect(Collectors.toList());
    }

    @Override
    public List<BannerSummaryDto> getPagesByStatus(String status) {
        log.info("Fetching pages with status: {}", status);
        return bannerRepository.findByStatus(status).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }

    @Override
    public List<BannerSummaryDto> getActivePages() {
        log.info("Fetching active pages");
        return bannerRepository.findByStatus("published").stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
}