package com.baylor.practicum_new.services;

import com.baylor.practicum_new.dto.*;
import com.baylor.practicum_new.dto.UserProductsDTO;
import com.baylor.practicum_new.entities.Product;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(Long userId, String productName, String description);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByUserId(Long userId);
    List<UserProductsDTO> getAllUsersWithProducts();
    @Transactional
    public ProductCategoryDTO createProductWithCategory(ProductCategoryDTO productCategoryDTO);

    List<ProductCategoryDTO> getProductsByCategory(Long categoryId);
    public List<CategoryDTO> getProductsGroupedByCategories();


}

