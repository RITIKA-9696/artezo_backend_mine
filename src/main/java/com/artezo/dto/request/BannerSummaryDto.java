package com.artezo.dto.request;

import java.time.LocalDateTime;

public class BannerSummaryDto {

    private Long id;
    private String pageName;
    private Integer slidesCount;
    private String bannerFileTwo;
    private String bannerFileThree;
    private String bannerFileFour;
    private String status;

    private LocalDateTime createdAt;

    // Default constructor
    public BannerSummaryDto() {}

    // Parameterized constructor
    public BannerSummaryDto(Long id, String pageName, Integer slidesCount,
                            String bannerFileTwo, String bannerFileThree,
                            String bannerFileFour, String status, LocalDateTime createdAt) {
        this.id = id;
        this.pageName = pageName;
        this.slidesCount = slidesCount;
        this.bannerFileTwo = bannerFileTwo;
        this.bannerFileThree = bannerFileThree;
        this.bannerFileFour = bannerFileFour;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public Integer getSlidesCount() { return slidesCount; }
    public void setSlidesCount(Integer slidesCount) { this.slidesCount = slidesCount; }

    public String getBannerFileTwo() { return bannerFileTwo; }
    public void setBannerFileTwo(String bannerFileTwo) { this.bannerFileTwo = bannerFileTwo; }

    public String getBannerFileThree() { return bannerFileThree; }
    public void setBannerFileThree(String bannerFileThree) { this.bannerFileThree = bannerFileThree; }

    public String getBannerFileFour() { return bannerFileFour; }
    public void setBannerFileFour(String bannerFileFour) { this.bannerFileFour = bannerFileFour; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }  // ✅ Add getter
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}