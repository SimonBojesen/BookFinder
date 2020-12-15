package com.example.bookfinder.model.prices;

import com.example.bookfinder.model.login.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricesRepository extends JpaRepository<Prices, Long> {
    Prices findByBookId(String bookId);
}
