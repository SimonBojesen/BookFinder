package com.example.bookfinder.model.prices;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceListingRepository extends JpaRepository<PriceListing, Long> {
}
