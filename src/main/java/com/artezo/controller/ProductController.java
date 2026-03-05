package com.artezo.controller;

import com.artezo.dto.request.CreateProductRequestDto;
import com.artezo.dto.response.ProductResponseDto;
import com.artezo.exceptions.ProductCreateResult;
import com.artezo.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);


    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // ────────────────────────────────────────────────
    //                  CRUD ENDPOINTS (unchanged mostly)
    // ────────────────────────────────────────────────

    @PostMapping(value = "/create-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProduct(
            @RequestPart("productJsonData") CreateProductRequestDto request,

            // Main product images - optional
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "mockupImages", required = false) List<MultipartFile> mockupImages,

            // Variant images - optional (indexed)
            @RequestPart(value = "variantsMainImages", required = false) List<MultipartFile> variantMainImages,

            // Hero banners images - optional (indexed)
            @RequestPart(value = "heroBannersImages", required = false) List<MultipartFile> heroBannerImages,

            // ─── NEW: Main product video (single file, optional) ───
            @RequestPart(value = "productVideo", required = false) MultipartFile productVideo,

            // Installation steps images & videos - optional (indexed)
            @RequestPart(value = "installationStepsImages", required = false) List<MultipartFile> stepImages,
            @RequestPart(value = "installationStepsVideos", required = false) List<MultipartFile> stepVideos) {

        log.info("Creating product: name = {}, hasVariants = {}",
                request.getProductName(), request.isHasVariants());

        // ── Assign main image ──
        if (mainImage != null && !mainImage.isEmpty()) {
            try {
                request.setMainImage(mainImage.getBytes());
            } catch (IOException e) {
                log.warn("Failed to read mainImage bytes", e);
            }
        }

        // ── Assign mockup images ──
        if (mockupImages != null && !mockupImages.isEmpty()) {
            List<byte[]> mockupBytes = new ArrayList<>();
            for (MultipartFile file : mockupImages) {
                if (file != null && !file.isEmpty()) {
                    try {
                        mockupBytes.add(file.getBytes());
                    } catch (IOException e) {
                        log.warn("Failed to read mockup image", e);
                    }
                }
            }
            request.setMockupImages(mockupBytes);
        }

        // ── Assign variant main images (by index) ──
        if (request.getVariants() != null && variantMainImages != null) {
            for (int i = 0; i < Math.min(request.getVariants().size(), variantMainImages.size()); i++) {
                MultipartFile file = variantMainImages.get(i);
                if (file != null && !file.isEmpty()) {
                    try {
                        request.getVariants().get(i).setMainImage(file.getBytes());
                    } catch (IOException e) {
                        log.warn("Failed to read variant main image at index {}", i, e);
                    }
                }
            }
        }

        // ── Assign hero banners images (by index) ──
        if (request.getHeroBanners() != null && heroBannerImages != null) {
            for (int i = 0; i < Math.min(request.getHeroBanners().size(), heroBannerImages.size()); i++) {
                MultipartFile file = heroBannerImages.get(i);
                if (file != null && !file.isEmpty()) {
                    try {
                        request.getHeroBanners().get(i).setBannerImg(file.getBytes());
                    } catch (IOException e) {
                        log.warn("Failed to read hero banner image at index {}", i, e);
                    }
                }
            }
        }

        // ─── NEW: Assign main product video ───
        if (productVideo != null && !productVideo.isEmpty()) {
            try {
                request.setProductVideo(productVideo.getBytes());
            } catch (IOException e) {
                log.warn("Failed to read product video bytes", e);
            }
        }

        // ── Assign installation steps images & videos ──
        if (request.getInstallationSteps() != null) {
            // Step images
            if (stepImages != null) {
                for (int i = 0; i < Math.min(request.getInstallationSteps().size(), stepImages.size()); i++) {
                    MultipartFile file = stepImages.get(i);
                    if (file != null && !file.isEmpty()) {
                        try {
                            request.getInstallationSteps().get(i).setStepImage(file.getBytes());
                        } catch (IOException e) {
                            log.warn("Failed to read installation step image at index {}", i, e);
                        }
                    }
                }
            }

            // Step videos
            if (stepVideos != null) {
                for (int i = 0; i < Math.min(request.getInstallationSteps().size(), stepVideos.size()); i++) {
                    MultipartFile file = stepVideos.get(i);
                    if (file != null && !file.isEmpty()) {
                        try {
                            request.getInstallationSteps().get(i).setVideoFile(file.getBytes());
                        } catch (IOException e) {
                            log.warn("Failed to read installation step video at index {}", i, e);
                        }
                    }
                }
            }
        }

        // ── Call service ──
        ProductCreateResult result = productService.createProduct(request);

        if (result.isSuccess()) {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(result.getDto());
        } else {
            return ResponseEntity
                    .status(result.getStatus())
                    .body(result.getErrorMessage());
        }
    }

    @GetMapping("/get-by-productPrimeId/{productPrimeId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long productPrimeId) {
        log.info("Fetching product by productPrimeId: {}", productPrimeId);
        return ResponseEntity.ok(productService.getProductById(productPrimeId));
    }

    @GetMapping("/get-product-by-productStrId/{productStrId}")
    public ResponseEntity<ProductResponseDto> getProductByStrId(@PathVariable String productStrId) {
        log.info("Fetching product by strId: {}", productStrId);
        return ResponseEntity.ok(productService.getProductByStrId(productStrId));
    }

    @PutMapping("/put-product/{productPrimeId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long productPrimeId, @RequestBody CreateProductRequestDto request) {
        log.info("Full update product productPrimeId: {}", productPrimeId);
        return ResponseEntity.ok(productService.updateProduct(productPrimeId, request));
    }

    @PatchMapping("/patch-product/{productPrimeId}")
    public ResponseEntity<ProductResponseDto> patchProduct(@PathVariable Long productPrimeId, @RequestBody CreateProductRequestDto request) {
        log.info("Patch product productPrimeId: {}", productPrimeId);
        return ResponseEntity.ok(productService.patchProduct(productPrimeId, request));
    }

    @DeleteMapping("/delete-by-productPrimeId/{productPrimeId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productPrimeId) {
        log.info("Soft delete product productPrimeId: {}", productPrimeId);
        productService.deleteProduct(productPrimeId);
        return ResponseEntity.noContent().build();
    }

    // ────────────────────────────────────────────────
    //                  IMAGE SERVING (byte[] → fast for small files)
    // ────────────────────────────────────────────────
    @GetMapping(value = "/{productPrimeId}/main", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE})
    public ResponseEntity<byte[]> getMainImage(@PathVariable Long productPrimeId) {
        byte[] data = productService.getProductMainImageData(productPrimeId);
        if (data == null || data.length == 0) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // improve detection later
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping(value = "/{productPrimeId}/mockup/{index}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getMockupImage(@PathVariable Long productPrimeId, @PathVariable int index) {
        List<byte[]> images = productService.getProductMockupImagesData(productPrimeId);
        if (images == null || index < 0 || index >= images.size()) return ResponseEntity.notFound().build();

        byte[] data = images.get(index);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(data.length)
                .body(data);
    }

    @GetMapping(value = "/{productId}/variant/{variantId}/main", produces = MediaType.ALL_VALUE)
    public ResponseEntity<byte[]> getVariantMainImage(@PathVariable Long productPrimeId, @PathVariable String variantId) {
        byte[] data = productService.getVariantMainImageData(productPrimeId, variantId);
        if (data == null || data.length == 0) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentLength(data.length)
                .body(data);
    }

    // ────────────────────────────────────────────────
    //          VIDEO STREAMING (Range support + URL in DTO)
    // ────────────────────────────────────────────────


    @GetMapping(value = "/{productPrimeId}/installation-video/{stepIndex}", produces = "video/mp4")
    public ResponseEntity<InputStreamResource> streamInstallationVideo(
            @PathVariable Long productPrimeId,
            @PathVariable int stepIndex,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletRequest request) throws IOException {

        byte[] fullVideoBytes = productService.getInstallationVideoData(productPrimeId, stepIndex);
        if (fullVideoBytes == null || fullVideoBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        long fileSize = fullVideoBytes.length;
        String etag = "\"" + Integer.toHexString(Arrays.hashCode(fullVideoBytes)) + "\"";

        // ✅ Handle 304 - client already has it cached
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        long start = 0, end = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            try {
                if (ranges[0].length() > 0) start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && ranges[1].length() > 0) end = Long.parseLong(ranges[1]);
                if (end >= fileSize) end = fileSize - 1;
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
            }
        }

        long contentLength = end - start + 1;
        ByteArrayInputStream bis = new ByteArrayInputStream(fullVideoBytes, (int) start, (int) contentLength);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        headers.setContentLength(contentLength);
        headers.set("Accept-Ranges", "bytes");
        headers.set("ETag", etag);
        headers.set("Cache-Control", "public, max-age=86400"); // ✅ browser caches
        headers.set("Last-Modified", "Wed, 01 Jan 2025 00:00:00 GMT"); // or actual timestamp

        if (rangeHeader != null) {
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
            return new ResponseEntity<>(new InputStreamResource(bis), headers, HttpStatus.PARTIAL_CONTENT);
        }

        return new ResponseEntity<>(new InputStreamResource(bis), headers, HttpStatus.OK);
    }


    /**
     * Get the main product video
     * Returns 200 with video bytes or 404 if not found
     */
    @GetMapping(value = "/{productPrimeId}/product-video", produces = {"video/mp4", "application/octet-stream"})
    public ResponseEntity<InputStreamResource> getProductVideo(
            @PathVariable Long productPrimeId,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            HttpServletRequest request) throws IOException {

        log.info("Streaming product video for productId {}", productPrimeId);

        byte[] fullVideoBytes = productService.getProductVideoData(productPrimeId);

        if (fullVideoBytes == null || fullVideoBytes.length == 0) {
            return ResponseEntity.notFound().build();
        }

        long fileSize = fullVideoBytes.length;
        String etag = "\"" + Integer.toHexString(Arrays.hashCode(fullVideoBytes)) + "\"";

        // ✅ 304 - client already has it cached
        String ifNoneMatch = request.getHeader("If-None-Match");
        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        long start = 0, end = fileSize - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            try {
                if (ranges[0].length() > 0) start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && ranges[1].length() > 0) end = Long.parseLong(ranges[1]);
                if (end >= fileSize) end = fileSize - 1;
            } catch (NumberFormatException e) {
                log.warn("Invalid Range header: {}", rangeHeader);
                return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE).build();
            }
        }

        long contentLength = end - start + 1;
        ByteArrayInputStream bis = new ByteArrayInputStream(fullVideoBytes, (int) start, (int) contentLength);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        headers.setContentLength(contentLength);
        headers.set("Accept-Ranges", "bytes");
        headers.set("ETag", etag);
        headers.set("Cache-Control", "public, max-age=86400");
        headers.set("Content-Disposition", "inline; filename=\"product-video-" + productPrimeId + ".mp4\"");

        if (rangeHeader != null) {
            headers.set(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
            return new ResponseEntity<>(new InputStreamResource(bis), headers, HttpStatus.PARTIAL_CONTENT);
        }

        return new ResponseEntity<>(new InputStreamResource(bis), headers, HttpStatus.OK);
    }
}