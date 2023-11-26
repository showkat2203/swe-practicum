package com.baylor.practicum_new.services.impl;

import com.baylor.practicum_new.dto.ProductDTO;
import com.baylor.practicum_new.dto.*;
import com.baylor.practicum_new.entities.Category;
import com.baylor.practicum_new.entities.Product;
import com.baylor.practicum_new.entities.User;
import com.baylor.practicum_new.repositories.CategoryRepository;
import com.baylor.practicum_new.repositories.ProductRepository;
import com.baylor.practicum_new.repositories.UserRepository;
import com.baylor.practicum_new.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    public void ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ProductDTO createProduct(Long userId, String productName, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        Product product = new Product();
        product.setUser(user);
        product.setProductName(productName);
        product.setDescription(description);

        Product savedProduct = productRepository.save(product);
        return new ProductDTO(savedProduct.getProductId(), savedProduct.getProductName(), savedProduct.getDescription());
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> new ProductDTO(product.getProductId(),
                        product.getProductName(), product.getDescription()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByUserId(Long userId) {
        List<Product> products = productRepository.findByUser_UserId(userId);

        return products.stream().map(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getProductId());
            productDTO.setProductName(product.getProductName());
            productDTO.setDescription(product.getDescription());
            return productDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UserProductsDTO> getAllUsersWithProducts() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            List<Product> userProducts = productRepository.findByUser_UserId(user.getUserId());
            List<ProductDTO> productDTOs = userProducts.stream()
                    .map(product -> new ProductDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getDescription()))
                    .collect(Collectors.toList());
            return new UserProductsDTO(user.getUserId(), user.getName(), productDTOs);
        }).collect(Collectors.toList());
    }

    @Transactional
    public ProductCategoryDTO createProductWithCategory(ProductCategoryDTO productCategoryDTO) {
        Product product = productRepository.findById(productCategoryDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found for ID: " + productCategoryDTO.getProductId()));

        Set<Category> categories = productCategoryDTO.getCategoryIds().stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found for ID: " + id)))
                .collect(Collectors.toSet());
        product.setCategories(categories);

        Product updatedProduct = productRepository.save(product);

        Set<Long> categoryIds = updatedProduct.getCategories().stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toSet());

        return new ProductCategoryDTO(updatedProduct.getProductId(), updatedProduct.getProductName(),
                updatedProduct.getDescription(), categoryIds);
    }

    @Override
    public List<ProductCategoryDTO> getProductsByCategory(Long categoryId) {
        List<Product> products;
        if (categoryId == 0) {
            products = productRepository.findProductsWithNoCategories();
        } else {
            products = productRepository.findProductsByCategoryId(categoryId);
        }

        return products.stream()
                .map(product -> new ProductCategoryDTO(product.getProductId(),
                        product.getProductName(), product.getDescription(),
                        product.getCategories().stream()
                                .map(Category::getCategoryId)
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getProductsGroupedByCategories() {
        List<Category> categories = categoryRepository.findAllWithProducts();

        return categories.stream().map(category -> {
            List<ProductDTO> productDTOs = category.getProducts() != null ? category.getProducts().stream()
                    .map(product -> new ProductDTO(product.getProductId(), product.getProductName(), product.getDescription()))
                    .collect(Collectors.toList()) : new ArrayList<>();

            return new CategoryDTO(category.getCategoryId(), category.getName(), category.getDescription(), productDTOs);
        }).collect(Collectors.toList());
    }


}
