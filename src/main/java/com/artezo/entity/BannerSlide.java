package com.artezo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner_pages")
public class BannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pageName;

    @Column(columnDefinition = "TEXT")  // Store JSON as TEXT
    private String slides;               // JSON array of slides

    private String bannerFileTwo;
    private String bannerFileThree;
    private String bannerFileFour;

    private String status;                // draft, published

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public BannerEntity() {}

    public BannerEntity(String pageName, String slides, String bannerFileTwo,
                      String bannerFileThree, String bannerFileFour, String status) {
        this.pageName = pageName;
        this.slides = slides;
        this.bannerFileTwo = bannerFileTwo;
        this.bannerFileThree = bannerFileThree;
        this.bannerFileFour = bannerFileFour;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters (like your Product entity)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public String getSlides() { return slides; }
    public void setSlides(String slides) { this.slides = slides; }

    public String getBannerFileTwo() { return bannerFileTwo; }
    public void setBannerFileTwo(String bannerFileTwo) { this.bannerFileTwo = bannerFileTwo; }

    public String getBannerFileThree() { return bannerFileThree; }
    public void setBannerFileThree(String bannerFileThree) { this.bannerFileThree = bannerFileThree; }

    public String getBannerFileFour() { return bannerFileFour; }
    public void setBannerFileFour(String bannerFileFour) { this.bannerFileFour = bannerFileFour; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Auto-update timestamps
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}