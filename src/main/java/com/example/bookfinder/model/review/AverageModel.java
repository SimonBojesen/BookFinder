package com.example.bookfinder.model.review;

public class AverageModel {

    private int reviewAmount;
    private Double averageRating;
    private String isbn13;

    public AverageModel(int reviewAmount, Double averageRating, String isbn13) {
        this.reviewAmount = reviewAmount;
        this.averageRating = averageRating;
        this.isbn13 = isbn13;
    }

    public AverageModel() {
    }

    public int getReviewAmount() {
        return reviewAmount;
    }

    public void setReviewAmount(int reviewAmount) {
        this.reviewAmount = reviewAmount;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }
}
