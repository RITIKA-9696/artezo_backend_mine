package com.artezo.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SlideDto {

    private Integer dotPosition;

    @JsonProperty("leftMain")
    private LeftMain leftMain;

    @JsonProperty("rightTop")
    private RightTop rightTop;

    @JsonProperty("rightCard")
    private RightCard rightCard;


    // Default constructor
    public SlideDto() {}

    // Inner class for LeftMain
    public static class LeftMain {
        private String title;

        @JsonIgnore  // Don't serialize byte[] to JSON
        private byte[] image;  // For upload

        private String imageUrl;  // For response (String URL)
        private String redirectUrl;

        // Default constructor
        public LeftMain() {}

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public byte[] getImage() { return image; }
        public void setImage(byte[] image) { this.image = image; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getRedirectUrl() { return redirectUrl; }
        public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    }

    // Inner class for RightTop
    public static class RightTop {
        @JsonIgnore
        private byte[] image;  // For upload

        private String imageUrl;  // For response (String URL)
        private String redirectUrl;

        // Default constructor
        public RightTop() {}

        // Getters and Setters
        public byte[] getImage() { return image; }
        public void setImage(byte[] image) { this.image = image; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public String getRedirectUrl() { return redirectUrl; }
        public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
    }

    // Inner class for RightCard
    public static class RightCard {
        private String title;
        private String description;

        // Default constructor
        public RightCard() {}

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    // Getters and Setters for SlideDto
    public Integer getDotPosition() { return dotPosition; }
    public void setDotPosition(Integer dotPosition) { this.dotPosition = dotPosition; }

    public LeftMain getLeftMain() { return leftMain; }
    public void setLeftMain(LeftMain leftMain) { this.leftMain = leftMain; }

    public RightTop getRightTop() { return rightTop; }
    public void setRightTop(RightTop rightTop) { this.rightTop = rightTop; }

    public RightCard getRightCard() { return rightCard; }
    public void setRightCard(RightCard rightCard) { this.rightCard = rightCard; }

}