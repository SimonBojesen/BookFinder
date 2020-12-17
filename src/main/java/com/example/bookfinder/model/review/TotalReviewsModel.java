package com.example.bookfinder.model.review;

public class TotalReviewsModel {

    int totalAmount;
    double averageRating;

    public TotalReviewsModel(int totalAmount, double averageRating) {
        this.totalAmount = totalAmount;
        this.averageRating = averageRating;
    }

    public TotalReviewsModel(){

    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
