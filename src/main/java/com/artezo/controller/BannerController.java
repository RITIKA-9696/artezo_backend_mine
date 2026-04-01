package com.artezo.controller;

import com.artezo.dto.request.BannerRequestDto;
import com.artezo.dto.request.BannerSummaryDto;
import com.artezo.dto.request.SlideDto;
import com.artezo.dto.response.ApiResponse;
import com.artezo.dto.response.BannerResponseDto;
import com.artezo.dto.response.BannerResponseDtoWithPreview;
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
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerControllerEnhanced {

    private static final Logger log = LoggerFactory.getLogger(BannerControllerEnhanced.class);
    private final BannerService bannerService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BannerControllerEnhanced(BannerService bannerService) {
        this.bannerService = bannerService;
    }

    /**
     * ✅ CREATE BANNER WITH FILE PREVIEW SUPPORT
     * Converts file bytes to base64 for instant preview after creation
     */
    @PostMapping(value = "/create-banner", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BannerResponseDtoWithPreview>> createBanner(
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

            // ✅ CONVERT TO PREVIEW DTO WITH BASE64 IMAGES
            BannerResponseDtoWithPreview previewDto = convertToPreviewDto(created, bannerFileTwo, bannerFileThree, bannerFileFour);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Banner created successfully", previewDto));

        } catch (Exception e) {
            log.error("Error creating banner: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create banner: " + e.getMessage()));
        }
    }

    /**
     * ✅ UPDATE BANNER WITH FILE PREVIEW SUPPORT
     * Converts file bytes to base64 for instant preview after editing
     */
    @PutMapping(value = "/update-banner/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<BannerResponseDtoWithPreview>> updateBanner(
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
            // ✅ Enhanced logging for update operation
            logFileDetails("leftMainImages", leftMainImages);
            logFileDetails("rightTopImages", rightTopImages);
            logSingleFileDetails("bannerFileTwo", bannerFileTwo);
            logSingleFileDetails("bannerFileThree", bannerFileThree);
            logSingleFileDetails("bannerFileFour", bannerFileFour);

            BannerRequestDto requestDto = new BannerRequestDto();
            if (pageName != null && !pageName.isEmpty()) {
                requestDto.setPageName(pageName);
            }
            if (status != null && !status.isEmpty()) {
                requestDto.setStatus(status);
            }

            List<SlideDto> slides = new ArrayList<>();

            // Parse slides metadata
            if (slidesMetadataJson != null && !slidesMetadataJson.isEmpty()) {
                BannerRequestDto.SlideMetadata[] metadataArray =
                        objectMapper.readValue(slidesMetadataJson, BannerRequestDto.SlideMetadata[].class);

                log.info("✓ Processing {} slides for update", metadataArray.length);

                for (int i = 0; i < metadataArray.length; i++) {
                    BannerRequestDto.SlideMetadata metadata = metadataArray[i];
                    SlideDto slideDto = new SlideDto();
                    slideDto.setDotPosition(metadata.getDotPosition() != null ? metadata.getDotPosition() : i + 1);

                    // Left Main
                    SlideDto.LeftMain leftMain = new SlideDto.LeftMain();
                    leftMain.setTitle(metadata.getLeftMainTitle() != null ? metadata.getLeftMainTitle() : "");
                    leftMain.setRedirectUrl(metadata.getLeftMainRedirectUrl() != null ? metadata.getLeftMainRedirectUrl() : "#");

                    if (leftMainImages != null && i < leftMainImages.size() && leftMainImages.get(i) != null && !leftMainImages.get(i).isEmpty()) {
                        byte[] imageBytes = leftMainImages.get(i).getBytes();
                        leftMain.setImage(imageBytes);
                        log.info("✓ Slide {} leftMain image updated: {} bytes", i, imageBytes.length);
                    }
                    slideDto.setLeftMain(leftMain);

                    // Right Top
                    SlideDto.RightTop rightTop = new SlideDto.RightTop();
                    rightTop.setRedirectUrl(metadata.getRightTopRedirectUrl() != null ? metadata.getRightTopRedirectUrl() : "#");

                    if (rightTopImages != null && i < rightTopImages.size() && rightTopImages.get(i) != null && !rightTopImages.get(i).isEmpty()) {
                        byte[] imageBytes = rightTopImages.get(i).getBytes();
                        rightTop.setImage(imageBytes);
                        log.info("✓ Slide {} rightTop image updated: {} bytes", i, imageBytes.length);
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

            // Only set slides if we processed any
            if (!slides.isEmpty()) {
                requestDto.setSlides(slides);
            }

            // Set banner files if provided
            if (bannerFileTwo != null && bannerFileTwo.getSize() > 0) {
                try {
                    requestDto.setBannerFileTwo(bannerFileTwo.getBytes());
                    log.info("✓ Banner file two updated: {} bytes", bannerFileTwo.getSize());
                } catch (IOException e) {
                    log.error("✗ Failed to update bannerFileTwo: {}", e.getMessage());
                }
            }
            if (bannerFileThree != null && bannerFileThree.getSize() > 0) {
                try {
                    requestDto.setBannerFileThree(bannerFileThree.getBytes());
                    log.info("✓ Banner file three updated: {} bytes", bannerFileThree.getSize());
                } catch (IOException e) {
                    log.error("✗ Failed to update bannerFileThree: {}", e.getMessage());
                }
            }
            if (bannerFileFour != null && bannerFileFour.getSize() > 0) {
                try {
                    requestDto.setBannerFileFour(bannerFileFour.getBytes());
                    log.info("✓ Banner file four updated: {} bytes", bannerFileFour.getSize());
                } catch (IOException e) {
                    log.error("✗ Failed to update bannerFileFour: {}", e.getMessage());
                }
            }

            BannerResponseDto updated = bannerService.updatePage(id, requestDto);

            // ✅ CONVERT TO PREVIEW DTO WITH BASE64 IMAGES
            BannerResponseDtoWithPreview previewDto = convertToPreviewDto(updated, bannerFileTwo, bannerFileThree, bannerFileFour);

            return ResponseEntity.ok(ApiResponse.success("Banner updated successfully", previewDto));

        } catch (Exception e) {
            log.error("✗ Error updating banner: {}", e.getMessage(), e);
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

    // ============== HELPER METHODS ==============

    /**
     * ✅ CONVERT RESPONSE TO PREVIEW DTO WITH BASE64 ENCODED IMAGES
     * This allows immediate preview of files without additional API calls
     */
    private BannerResponseDtoWithPreview convertToPreviewDto(
            BannerResponseDto response,
            MultipartFile bannerFileTwo,
            MultipartFile bannerFileThree,
            MultipartFile bannerFileFour) {

        BannerResponseDtoWithPreview previewDto = new BannerResponseDtoWithPreview();
        previewDto.setId(response.getId());
        previewDto.setPageName(response.getPageName());
        previewDto.setSlides(response.getSlides());
        previewDto.setStatus(response.getStatus());
        previewDto.setCreatedAt(response.getCreatedAt());
        previewDto.setUpdatedAt(response.getUpdatedAt());

        // Set file URLs
        previewDto.setBannerFileTwoUrl(response.getBannerFileTwoUrl());
        previewDto.setBannerFileThreeUrl(response.getBannerFileThreeUrl());
        previewDto.setBannerFileFourUrl(response.getBannerFileFourUrl());

        // ✅ CONVERT FILES TO BASE64 FOR PREVIEW
        try {
            if (bannerFileTwo != null && !bannerFileTwo.isEmpty()) {
                String base64 = Base64.getEncoder().encodeToString(bannerFileTwo.getBytes());
                previewDto.setBannerFileTwoPreview("data:image/jpeg;base64," + base64);
                log.debug("✓ Banner file two preview generated: {} bytes", bannerFileTwo.getSize());
            }
        } catch (IOException e) {
            log.error("✗ Error converting bannerFileTwo to base64: {}", e.getMessage());
        }

        try {
            if (bannerFileThree != null && !bannerFileThree.isEmpty()) {
                String base64 = Base64.getEncoder().encodeToString(bannerFileThree.getBytes());
                previewDto.setBannerFileThreePreview("data:image/jpeg;base64," + base64);
                log.debug("✓ Banner file three preview generated: {} bytes", bannerFileThree.getSize());
            }
        } catch (IOException e) {
            log.error("✗ Error converting bannerFileThree to base64: {}", e.getMessage());
        }

        try {
            if (bannerFileFour != null && !bannerFileFour.isEmpty()) {
                String base64 = Base64.getEncoder().encodeToString(bannerFileFour.getBytes());
                previewDto.setBannerFileThreePreview("data:image/jpeg;base64," + base64);
                log.debug("✓ Banner file four preview generated: {} bytes", bannerFileFour.getSize());
            }
        } catch (IOException e) {
            log.error("✗ Error converting bannerFileFour to base64: {}", e.getMessage());
        }

        return previewDto;
    }

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