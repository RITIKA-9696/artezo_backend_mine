package com.artezo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner_slides")
public class BannerSlide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_page_id", nullable = false)
    private BannerPage bannerPage;

    @Column(name = "dot_position", nullable = false)
    private Integer dotPosition;

    // Left Main fields
    @Column(name = "left_main_title")
    private String leftMainTitle;

    @Lob
    @Column(name = "left_main_image", columnDefinition = "LONGBLOB")
    private byte[] leftMainImage;  // Store as byte array

    @Column(name = "left_main_image_url")
    private String leftMainImageUrl;  // URL for accessing the image

    @Column(name = "left_main_redirect_url")
    private String leftMainRedirectUrl;

    // Right Top fields
    @Lob
    @Column(name = "right_top_image", columnDefinition = "LONGBLOB")
    private byte[] rightTopImage;  // Store as byte array

    @Column(name = "right_top_image_url")
    private String rightTopImageUrl;  // URL for accessing the image

    @Column(name = "right_top_redirect_url")
    private String rightTopRedirectUrl;

    // Right Card fields
    @Column(name = "right_card_title")
    private String rightCardTitle;

    @Column(name = "right_card_description")
    private String rightCardDescription;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public BannerSlide() {}

    public BannerSlide(Integer dotPosition) {
        this.dotPosition = dotPosition;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BannerPage getBannerPage() { return bannerPage; }
    public void setBannerPage(BannerPage bannerPage) { this.bannerPage = bannerPage; }

    public Integer getDotPosition() { return dotPosition; }
    public void setDotPosition(Integer dotPosition) { this.dotPosition = dotPosition; }

    public String getLeftMainTitle() { return leftMainTitle; }
    public void setLeftMainTitle(String leftMainTitle) { this.leftMainTitle = leftMainTitle; }

    public byte[] getLeftMainImage() { return leftMainImage; }
    public void setLeftMainImage(byte[] leftMainImage) { this.leftMainImage = leftMainImage; }

    public String getLeftMainImageUrl() { return leftMainImageUrl; }
    public void setLeftMainImageUrl(String leftMainImageUrl) { this.leftMainImageUrl = leftMainImageUrl; }

    public String getLeftMainRedirectUrl() { return leftMainRedirectUrl; }
    public void setLeftMainRedirectUrl(String leftMainRedirectUrl) { this.leftMainRedirectUrl = leftMainRedirectUrl; }

    public byte[] getRightTopImage() { return rightTopImage; }
    public void setRightTopImage(byte[] rightTopImage) { this.rightTopImage = rightTopImage; }

    public String getRightTopImageUrl() { return rightTopImageUrl; }
    public void setRightTopImageUrl(String rightTopImageUrl) { this.rightTopImageUrl = rightTopImageUrl; }

    public String getRightTopRedirectUrl() { return rightTopRedirectUrl; }
    public void setRightTopRedirectUrl(String rightTopRedirectUrl) { this.rightTopRedirectUrl = rightTopRedirectUrl; }

    public String getRightCardTitle() { return rightCardTitle; }
    public void setRightCardTitle(String rightCardTitle) { this.rightCardTitle = rightCardTitle; }

    public String getRightCardDescription() { return rightCardDescription; }
    public void setRightCardDescription(String rightCardDescription) { this.rightCardDescription = rightCardDescription; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}