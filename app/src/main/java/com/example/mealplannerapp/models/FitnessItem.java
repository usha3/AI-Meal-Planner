package com.example.mealplannerapp.models;

public class FitnessItem {

    String title;
    String image;
    String duration;
    String type;
    String videoId;
    String benefits;
    String description;

    public FitnessItem(String title, String image,
                       String duration, String type,
                       String videoId,
                       String benefits,
                       String description) {

        this.title = title;
        this.image = image;
        this.duration = duration;
        this.type = type;
        this.videoId = videoId;
        this.benefits = benefits;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDuration() {
        return duration;
    }

    public String getType() {
        return type;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getBenefits() {
        return benefits;
    }

    public String getDescription() {
        return description;
    }
}