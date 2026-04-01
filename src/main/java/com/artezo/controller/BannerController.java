package com.artezo.controller;

import com.artezo.dto.request.BannerRequestDto;
import com.artezo.dto.request.BannerSummaryDto;
import com.artezo.dto.request.SlideDto;
import com.artezo.dto.response.ApiResponse;
import com.artezo.dto.response.BannerResponseDto;
import com.artezo.service.BannerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerController {

    private static final Logger log = LoggerFactory.getLogger(BannerController.class);
    private final BannerService bannerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BannerController(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    @PostMapping(value = "/create-banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BannerResponseDto>> createBanner(
            @RequestParam("pageName") String pageName,
            @RequestParam(value = "slidesMetadata", required = false) String slidesMetadataJson,
            @RequestParam(value = "leftMainImages", required = false) List<MultipartFile> leftMainImages,
            @RequestParam(value = "rightTopImages", required = false) List<MultipartFile> rightTopImages,
            @RequestParam(value = "bannerFileTwo", required = false) MultipartFile bannerFileTwo,
            @RequestParam(value = "bannerFileThree", required = false) MultipartFile bannerFileThree,
            @RequestParam(value = "bannerFileFour", required = false) MultipartFile bannerFileFour,
            @RequestParam(value = "status", required = false) String status) {

        log.info("POST /api/banners/create-banner - Creating banner for page: {}", pageName);
        log.info("slidesMetadata: {}", slidesMetadataJson);
        log.info("leftMainImages count: {}", leftMainImages != null ? leftMainImages.size() : 0);
        log.info("rightTopImages count: {}", rightTopImages != null ? rightTopImages.size() : 0);

        try {
            BannerRequestDto requestDto = new BannerRequestDto();
            requestDto.setPageName(pageName);
            requestDto.setStatus(status != null ? status : "draft");

            List<SlideDto> slides = new ArrayList<>();

            // Parse slides metadata
            if (slidesMetadataJson != null && !slidesMetadataJson.isEmpty()) {
                BannerRequestDto.SlideMetadata[] metadataArray =
                        objectMapper.readValue(slidesMetadataJson, BannerRequestDto.SlideMetadata[].class);

                log.info("Processing {} slides from metadata", metadataArray.length);

                // ✅ DEBUG: Log metadata array length
                System.out.println("=== METADATA ARRAY LENGTH: " + metadataArray.length);
                for (int m = 0; m < metadataArray.length; m++) {
                    System.out.println("Metadata " + m + ": " + metadataArray[m]);
                }

                for (int i = 0; i < metadataArray.length; i++) {
                    BannerRequestDto.SlideMetadata metadata = metadataArray[i];
                    SlideDto slideDto = new SlideDto();
                    slideDto.setDotPosition(metadata.getDotPosition() != null ? metadata.getDotPosition() : i + 1);

                    // Left Main
                    SlideDto.LeftMain leftMain = new SlideDto.LeftMain();
                    leftMain.setTitle(metadata.getLeftMainTitle() != null ? metadata.getLeftMainTitle() : "");
                    leftMain.setRedirectUrl(metadata.getLeftMainRedirectUrl() != null ? metadata.getLeftMainRedirectUrl() : "#");

                    // Set left main image if available
                    if (leftMainImages != null && i < leftMainImages.size() && leftMainImages.get(i) != null && !leftMainImages.get(i).isEmpty()) {
                        byte[] imageBytes = leftMainImages.get(i).getBytes();
                        leftMain.setImage(imageBytes);
                        log.info("Slide {} leftMain image size: {} bytes", i, imageBytes.length);
                    } else {
                        log.info("Slide {} has no leftMain image", i);
                    }
                    slideDto.setLeftMain(leftMain);

                    // Right Top
                    SlideDto.RightTop rightTop = new SlideDto.RightTop();
                    rightTop.setRedirectUrl(metadata.getRightTopRedirectUrl() != null ? metadata.getRightTopRedirectUrl() : "#");

                    // Set right top image if available
                    if (rightTopImages != null && i < rightTopImages.size() && rightTopImages.get(i) != null && !rightTopImages.get(i).isEmpty()) {
                        byte[] imageBytes = rightTopImages.get(i).getBytes();
                        rightTop.setImage(imageBytes);
                        log.info("Slide {} rightTop image size: {} bytes", i, imageBytes.length);
                    } else {
                        log.info("Slide {} has no rightTop image", i);
                    }
                    slideDto.setRightTop(rightTop);

                    // Right Card
                    SlideDto.RightCard rightCard = new SlideDto.RightCard();
                    rightCard.setTitle(metadata.getRightCardTitle() != null ? metadata.getRightCardTitle() : "");
                    rightCard.setDescription(metadata.getRightCardDescription() != null ? metadata.getRightCardDescription() : "");
                    slideDto.setRightCard(rightCard);

                    slides.add(slideDto);
                }
            }

            requestDto.setSlides(slides);
            log.info("Total slides to save: {}", slides.size());

            // ✅ DEBUG: Log final slides count
            System.out.println("=== FINAL SLIDES COUNT BEFORE SERVICE: " + slides.size());

            // Set banner files if provided
            if (bannerFileTwo != null && !bannerFileTwo.isEmpty()) {
                requestDto.setBannerFileTwo(bannerFileTwo.getBytes());
                log.info("Banner file two size: {} bytes", bannerFileTwo.getSize());
            }
            if (bannerFileThree != null && !bannerFileThree.isEmpty()) {
                requestDto.setBannerFileThree(bannerFileThree.getBytes());
                log.info("Banner file three size: {} bytes", bannerFileThree.getSize());
            }
            if (bannerFileFour != null && !bannerFileFour.isEmpty()) {
                requestDto.setBannerFileFour(bannerFileFour.getBytes());
                log.info("Banner file four size: {} bytes", bannerFileFour.getSize());
            }

            BannerResponseDto created = bannerService.createPage(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Banner created successfully", created));

        } catch (Exception e) {
            log.error("Error creating banner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create banner: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/update-banner/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BannerResponseDto>> updateBanner(
            @PathVariable Long id,
            @RequestParam(value = "pageName", required = false) String pageName,
            @RequestParam(value = "slidesMetadata", required = false) String slidesMetadataJson,
            @RequestParam(value = "leftMainImages", required = false) List<MultipartFile> leftMainImages,
            @RequestParam(value = "rightTopImages", required = false) List<MultipartFile> rightTopImages,
            @RequestParam(value = "bannerFileTwo", required = false) MultipartFile bannerFileTwo,
            @RequestParam(value = "bannerFileThree", required = false) MultipartFile bannerFileThree,
            @RequestParam(value = "bannerFileFour", required = false) MultipartFile bannerFileFour,
            @RequestParam(value = "status", required = false) String status) {

        log.info("PUT /api/banners/update-banner/{} - Updating banner", id);

        try {
            // Log file details
            logFileDetails("leftMainImages", leftMainImages);
            logFileDetails("rightTopImages", rightTopImages);
            logSingleFileDetails("bannerFileTwo", bannerFileTwo);
            logSingleFileDetails("bannerFileThree", bannerFileThree);
            logSingleFileDetails("bannerFileFour", bannerFileFour);

            BannerRequestDto requestDto = new BannerRequestDto();

            if (pageName != null) {
                requestDto.setPageName(pageName);
            }
            if (status != null) {
                requestDto.setStatus(status);
            }

            List<SlideDto> slides = new ArrayList<>();

            // Parse slides metadata
            if (slidesMetadataJson != null && !slidesMetadataJson.isEmpty()) {
                log.info("Parsing slides metadata: {}", slidesMetadataJson);
                BannerRequestDto.SlideMetadata[] metadataArray =
                        objectMapper.readValue(slidesMetadataJson, BannerRequestDto.SlideMetadata[].class);

                log.info("Found {} slides in metadata", metadataArray.length);

                for (int i = 0; i < metadataArray.length; i++) {
                    BannerRequestDto.SlideMetadata metadata = metadataArray[i];
                    SlideDto slideDto = new SlideDto();
                    slideDto.setDotPosition(metadata.getDotPosition());

                    SlideDto.LeftMain leftMain = new SlideDto.LeftMain();
                    leftMain.setTitle(metadata.getLeftMainTitle());
                    leftMain.setRedirectUrl(metadata.getLeftMainRedirectUrl());

                    if (leftMainImages != null && i < leftMainImages.size()) {
                        MultipartFile file = leftMainImages.get(i);
                        if (file != null && !file.isEmpty()) {
                            leftMain.setImage(file.getBytes());
                            log.info("Slide {} leftMain image size: {} bytes", i, file.getSize());
                        }
                    }
                    slideDto.setLeftMain(leftMain);

                    SlideDto.RightTop rightTop = new SlideDto.RightTop();
                    rightTop.setRedirectUrl(metadata.getRightTopRedirectUrl());

                    if (rightTopImages != null && i < rightTopImages.size()) {
                        MultipartFile file = rightTopImages.get(i);
                        if (file != null && !file.isEmpty()) {
                            rightTop.setImage(file.getBytes());
                            log.info("Slide {} rightTop image size: {} bytes", i, file.getSize());
                        }
                    }
                    slideDto.setRightTop(rightTop);

                    SlideDto.RightCard rightCard = new SlideDto.RightCard();
                    rightCard.setTitle(metadata.getRightCardTitle());
                    rightCard.setDescription(metadata.getRightCardDescription());
                    slideDto.setRightCard(rightCard);

                    slides.add(slideDto);
                }
            }

            if (!slides.isEmpty()) {
                requestDto.setSlides(slides);
                log.info("Total slides to update: {}", slides.size());
            }

            // Set banner files if provided
            if (bannerFileTwo != null && !bannerFileTwo.isEmpty()) {
                requestDto.setBannerFileTwo(bannerFileTwo.getBytes());
                log.info("Banner file two size: {} bytes", bannerFileTwo.getSize());
            }
            if (bannerFileThree != null && !bannerFileThree.isEmpty()) {
                requestDto.setBannerFileThree(bannerFileThree.getBytes());
                log.info("Banner file three size: {} bytes", bannerFileThree.getSize());
            }
            if (bannerFileFour != null && !bannerFileFour.isEmpty()) {
                requestDto.setBannerFileFour(bannerFileFour.getBytes());
                log.info("Banner file four size: {} bytes", bannerFileFour.getSize());
            }

            BannerResponseDto updated = bannerService.updatePage(id, requestDto);
            return ResponseEntity.ok(ApiResponse.success("Banner updated successfully", updated));

        } catch (Exception e) {
            log.error("ERROR updating banner: ", e);  // This will print full stack trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update banner: " + e.getMessage()));
        }
    }

    @GetMapping("/get-all-banners")
    public ResponseEntity<ApiResponse<List<BannerSummaryDto>>> getAllBanners() {
        log.info("GET /api/banners/get-all-banners - Fetching all banners");
        List<BannerSummaryDto> pages = bannerService.getAllPages();
        return ResponseEntity.ok(ApiResponse.success(pages));
    }

    @GetMapping("/get-banner-by-id/{id}")
    public ResponseEntity<ApiResponse<BannerResponseDto>> getBannerById(@PathVariable Long id) {
        log.info("GET /api/banners/get-banner-by-id/{} - Fetching banner by id", id);
        BannerResponseDto page = bannerService.getPageById(id);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/get-banner-by-name/{pageName}")
    public ResponseEntity<ApiResponse<BannerResponseDto>> getBannerByName(@PathVariable String pageName) {
        log.info("GET /api/banners/get-banner-by-name/{} - Fetching banner by name", pageName);
        BannerResponseDto page = bannerService.getPageByName(pageName);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping(value = "/get-banner-file/{pageId}/{fileType}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getBannerFile(
            @PathVariable Long pageId,
            @PathVariable String fileType) {
        log.info("GET /api/banners/get-banner-file/{}/{}", pageId, fileType);
        byte[] imageData = bannerService.getBannerFile(pageId, fileType);
        if (imageData == null || imageData.length == 0) {
            log.warn("No image data found for pageId={}, fileType={}", pageId, fileType);
            return ResponseEntity.notFound().build();
        }
        log.debug("Returning {} bytes for {}", imageData.length, fileType);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
    }

    @GetMapping(value = "/get-left-main-image/{pageId}/{slideId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getLeftMainImage(
            @PathVariable Long pageId,
            @PathVariable Long slideId) {
        log.info("GET /api/banners/get-left-main-image/{}/{}", pageId, slideId);
        byte[] imageData = bannerService.getLeftMainImage(pageId, slideId);
        if (imageData == null || imageData.length == 0) {
            log.warn("No left main image found for pageId={}, slideId={}", pageId, slideId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Returning {} bytes for left main image", imageData.length);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
    }

    @GetMapping(value = "/get-right-top-image/{pageId}/{slideId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getRightTopImage(
            @PathVariable Long pageId,
            @PathVariable Long slideId) {
        log.info("GET /api/banners/get-right-top-image/{}/{}", pageId, slideId);
        byte[] imageData = bannerService.getRightTopImage(pageId, slideId);
        if (imageData == null || imageData.length == 0) {
            log.warn("No right top image found for pageId={}, slideId={}", pageId, slideId);
            return ResponseEntity.notFound().build();
        }
        log.debug("Returning {} bytes for right top image", imageData.length);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageData);
    }

    @DeleteMapping("/delete-banner/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        log.info("DELETE /api/banners/delete-banner/{} - Deleting banner", id);
        bannerService.deletePage(id);
        return ResponseEntity.ok(ApiResponse.success("Banner deleted successfully", null));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Banner Management System is running", null));
    }

    // ============== HELPER METHODS FOR LOGGING ==============

    /**
     * Log details about a list of multipart files
     */
    private void logFileDetails(String paramName, List<MultipartFile> files) {
        if (files == null) {
            log.debug("{}: null (no files provided)", paramName);
            return;
        }

        if (files.isEmpty()) {
            log.debug("{}: empty list", paramName);
            return;
        }

        log.info("{}: {} file(s) received", paramName, files.size());
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            log.info("  [{}] name='{}', size={} bytes, empty={}, contentType='{}'",
                    i, file.getOriginalFilename(), file.getSize(), file.isEmpty(), file.getContentType());
        }
    }

    /**
     * Log details about a single multipart file
     */
    private void logSingleFileDetails(String paramName, MultipartFile file) {
        if (file == null) {
            log.debug("{}: null (no file provided)", paramName);
            return;
        }

        log.info("{}: name='{}', size={} bytes, empty={}, contentType='{}'",
                paramName, file.getOriginalFilename(), file.getSize(), file.isEmpty(), file.getContentType());
    }
}