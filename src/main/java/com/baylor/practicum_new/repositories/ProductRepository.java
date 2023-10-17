package com.baylor.practicum_new.repositories;

import com.baylor.practicum_new.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser_UserId(Long userId);

}
