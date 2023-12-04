package com.baylor.practicum_new.repositories;

import com.baylor.practicum_new.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser_UserId(Long userId);

    @Query("SELECT p FROM Product p WHERE p.categories IS EMPTY")
    List<Product> findProductsWithNoCategories();

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.categoryId = :categoryId")
    List<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);

    boolean existsByProductName(String productName);

    List<Product> findAllByCategoriesIsEmpty();

    Optional<Product> findByProductNameAndProductIdNot(String productName, Long productId);



}
