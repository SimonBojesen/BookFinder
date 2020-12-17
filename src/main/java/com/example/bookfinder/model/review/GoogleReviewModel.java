package com.example.bookfinder.model.review;

public class GoogleReviewModel {

    private Integer amount;
    private Double rating;
    private String isbn13;
    private String bookId;

    public GoogleReviewModel(Integer amount, Double rating, String isbn13, String bookId) {
        this.amount = amount;
        this.rating = rating;
        this.isbn13 = isbn13;
        this.bookId = bookId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
