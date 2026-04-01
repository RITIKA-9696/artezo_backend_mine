package com.artezo.dto.response;

import com.artezo.dto.request.SlideDto;
import java.time.LocalDateTime;
import java.util.List;

public class BannerResponseDto {

    private Long id;
    private String pageName;
    private List<SlideDto> slides;
    private String bannerFileTwoUrl;      // Changed from bannerFileTwo (byte[])
    private String bannerFileThreeUrl;    // Changed from bannerFileThree (byte[])
    private String bannerFileFourUrl;     // Changed from bannerFileFour (byte[])
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public BannerResponseDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public List<SlideDto> getSlides() { return slides; }
    public void setSlides(List<SlideDto> slides) { this.slides = slides; }

    public String getBannerFileTwoUrl() { return bannerFileTwoUrl; }
    public void setBannerFileTwoUrl(String bannerFileTwoUrl) { this.bannerFileTwoUrl = bannerFileTwoUrl; }

    public String getBannerFileThreeUrl() { return bannerFileThreeUrl; }
    public void setBannerFileThreeUrl(String bannerFileThreeUrl) { this.bannerFileThreeUrl = bannerFileThreeUrl; }

    public String getBannerFileFourUrl() { return bannerFileFourUrl; }
    public void setBannerFileFourUrl(String bannerFileFourUrl) { this.bannerFileFourUrl = bannerFileFourUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}