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

    @OneToMany(cascade = {CascadeType.ALL})
    private List<PriceListing> priceListings;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public List<PriceListing> getPriceListings() {
        return priceListings;
    }

    public void setPriceListings(List<PriceListing> priceListings) {
        this.priceListings = priceListings;
    }



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
