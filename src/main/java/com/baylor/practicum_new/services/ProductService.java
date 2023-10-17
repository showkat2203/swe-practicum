package com.baylor.practicum_new.services;

import com.baylor.practicum_new.dto.ProductDTO;
import com.baylor.practicum_new.dto.UserProductsDTO;

import java.util.List;

public interface ProductService {
    ProductDTO createProduct(Long userId, String productName, String description);
    List<ProductDTO> getAllProducts();
    List<ProductDTO> getProductsByUserId(Long userId);
    List<UserProductsDTO> getAllUsersWithProducts();
}

