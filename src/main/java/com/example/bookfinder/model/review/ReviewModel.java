package com.example.bookfinder.model.review;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewModel {

    public String isbn13;
    public Rating rating;

    public enum Rating {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE;
    }

    public ReviewModel() {
    }

    public ReviewModel(String isbn13, Rating rating) {
        this.isbn13 = isbn13;
        this.rating = rating;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
}


