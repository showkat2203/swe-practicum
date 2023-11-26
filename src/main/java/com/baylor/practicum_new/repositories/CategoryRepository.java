package com.baylor.practicum_new.repositories;
import com.baylor.practicum_new.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
     Optional<Category> findByName(String categoryName);

     @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products")
     List<Category> findAllWithProducts();

}
