package com.artezo.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class BannerRequestDto {

    private String pageName;

    @JsonIgnore
    private List<SlideDto> slides;

    @JsonIgnore
    private byte[] bannerFileTwo;

    private String bannerFileTwoUrl;

    @JsonIgnore
    private byte[] bannerFileThree;

    private String bannerFileThreeUrl;

    @JsonIgnore
    private byte[] bannerFileFour;

    private String bannerFileFourUrl;

    private String status;

    // ✅ REMOVE THIS - it's causing confusion
    // private List<SlideMetadata> slidesMetadata;

    // Inner class for slide metadata (sent as JSON)
    public static class SlideMetadata {
        private Integer dotPosition;
        private String leftMainTitle;
        private String leftMainRedirectUrl;
        private String rightTopRedirectUrl;
        private String rightCardTitle;
        private String rightCardDescription;

        // Getters and Setters
        public Integer getDotPosition() { return dotPosition; }
        public void setDotPosition(Integer dotPosition) { this.dotPosition = dotPosition; }

        public String getLeftMainTitle() { return leftMainTitle; }
        public void setLeftMainTitle(String leftMainTitle) { this.leftMainTitle = leftMainTitle; }

        public String getLeftMainRedirectUrl() { return leftMainRedirectUrl; }
        public void setLeftMainRedirectUrl(String leftMainRedirectUrl) { this.leftMainRedirectUrl = leftMainRedirectUrl; }

        public String getRightTopRedirectUrl() { return rightTopRedirectUrl; }
        public void setRightTopRedirectUrl(String rightTopRedirectUrl) { this.rightTopRedirectUrl = rightTopRedirectUrl; }

        public String getRightCardTitle() { return rightCardTitle; }
        public void setRightCardTitle(String rightCardTitle) { this.rightCardTitle = rightCardTitle; }

        public String getRightCardDescription() { return rightCardDescription; }
        public void setRightCardDescription(String rightCardDescription) { this.rightCardDescription = rightCardDescription; }
    }

    // Getters and Setters
    public String getPageName() { return pageName; }
    public void setPageName(String pageName) { this.pageName = pageName; }

    public List<SlideDto> getSlides() { return slides; }
    public void setSlides(List<SlideDto> slides) { this.slides = slides; }

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
}