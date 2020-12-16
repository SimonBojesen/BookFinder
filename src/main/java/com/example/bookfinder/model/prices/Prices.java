package com.example.bookfinder.model.prices;

import com.example.bookfinder.model.prices.PriceListing;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "prices")
public class Prices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique=true)
    private String bookId;

    private String bookTitle;

    @OneToMany(cascade = {CascadeType.ALL})
    private List<PriceListing> priceListings;

    public Prices(String bookId, String bookTitle, List<PriceListing> priceListings) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.priceListings = priceListings;
    }

    public Prices() {

    }

    public long getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public List<PriceListing> getPriceListings() {
        return priceListings;
    }

    public void setPriceListings(List<PriceListing> priceListings) {
        this.priceListings = priceListings;
    }
}
