package com.artezo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "banner_pages")
public class BannerPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String pageName;

    @OneToMany(mappedBy = "bannerPage", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("dotPosition ASC")
    private List<BannerSlide> slides = new ArrayList<>();

    @Lob
    @Column(name = "banner_file_two", columnDefinition = "LONGBLOB")
    private byte[] bannerFileTwo;

    @Column(name = "banner_file_two_url")
    private String bannerFileTwoUrl;

    @Lob
    @Column(name = "banner_file_three", columnDefinition = "LONGBLOB")
    private byte[] bannerFileThree;

    @Column(name = "banner_file_three_url")
    private String bannerFileThreeUrl;

    @Lob
    @Column(name = "banner_file_four", columnDefinition = "LONGBLOB")
    private byte[] bannerFileFour;

    @Column(name = "banner_file_four_url")
    private String bannerFileFourUrl;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public BannerPage() {}

    public BannerPage(String pageName) {
        this.pageName = pageName;
        this.status = "draft";
    }

    // Helper methods
    public void addSlide(BannerSlide slide) {
        slides.add(slide);
        slide.setBannerPage(this);
    }

    public void removeSlide(BannerSlide slide) {
        slides.remove(slide);
        slide.setBannerPage(null);
    }

    // ✅ FIX: Add this method to properly set slides collection
    public void setSlides(List<BannerSlide> slides) {
        if (slides == null) {
            this.slides = new ArrayList<>();
            return;
        }
        // Clear existing slides
        this.slides.clear();
        // Add new slides
        for (BannerSlide slide : slides) {
            this.slides.add(slide);
            slide.setBannerPage(this);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public List<BannerSlide> getSlides() { return slides; }
    // Remove this getter if you want to force using the custom setter
    // public void setSlides(List<BannerSlide> slides) { this.slides = slides; }

    public byte[] getBannerFileTwo() { return bannerFileTwo; }
    public void setBannerFileTwo(byte[] bannerFileTwo) { this.bannerFileTwo = bannerFileTwo; }

    public String getBannerFileTwoUrl() { return bannerFileTwoUrl; }
    public void setBannerFileTwoUrl(String bannerFileTwoUrl) { this.bannerFileTwoUrl = bannerFileTwoUrl; }

    public byte[] getBannerFileThree() { return bannerFileThree; }
    public void setBannerFileThree(byte[] bannerFileThree) { this.bannerFileThree = bannerFileThree; }

    public String getBannerFileThreeUrl() { return bannerFileThreeUrl; }
    public void setBannerFileThreeUrl(String bannerFileThreeUrl) { this.bannerFileThreeUrl = bannerFileThreeUrl; }

    public byte[] getBannerFileFour() { return bannerFileFour; }
    public void setBannerFileFour(byte[] bannerFileFour) { this.bannerFileFour = bannerFileFour; }

    public String getBannerFileFourUrl() { return bannerFileFourUrl; }
    public void setBannerFileFourUrl(String bannerFileFourUrl) { this.bannerFileFourUrl = bannerFileFourUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

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