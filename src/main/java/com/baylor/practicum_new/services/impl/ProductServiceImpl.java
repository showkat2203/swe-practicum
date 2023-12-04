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
import java.util.HashSet;
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

        if (productRepository.existsByProductName(productName)) {
            throw new IllegalArgumentException("Product name '" + productName + "' already exists.");
        }

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

        Set<Long> categoryIds = productCategoryDTO.getCategoryIds();

        if (categoryIds.size() > 3) {
            throw new IllegalArgumentException("A product cannot be linked to more than three categories.");
        }

        Set<Category> categories = categoryIds.stream()
                .map(id -> categoryRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found for ID: " + id)))
                .collect(Collectors.toSet());

        product.setCategories(categories);
        Product updatedProduct = productRepository.save(product);

        Set<Long> linkedCategoryIds = updatedProduct.getCategories().stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toSet());

        return new ProductCategoryDTO(updatedProduct.getProductId(), updatedProduct.getProductName(),
                updatedProduct.getDescription(), linkedCategoryIds);
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
    @Transactional
    public List<CategoryDTO> getProductsGroupedByCategories() {
        List<Product> noCategoryProducts = productRepository.findAllByCategoriesIsEmpty();

        // Create a 'General' category for products with no category
        CategoryDTO generalCategory = new CategoryDTO(null, "General", "Products with no specific category",
                noCategoryProducts.stream()
                        .map(product -> new ProductDTO(product.getProductId(), product.getProductName(), product.getDescription()))
                        .collect(Collectors.toList()));

        // Fetch all categories with their products
        List<Category> categories = categoryRepository.findAllWithProducts();

        // Convert categories to DTOs
        List<CategoryDTO> categoryDTOs = categories.stream().map(category -> {
            List<ProductDTO> productDTOs = category.getProducts() != null ? category.getProducts().stream()
                    .map(product -> new ProductDTO(product.getProductId(), product.getProductName(), product.getDescription()))
                    .collect(Collectors.toList()) : new ArrayList<>();

            return new CategoryDTO(category.getCategoryId(), category.getName(), category.getDescription(), productDTOs);
        }).collect(Collectors.toList());

        // Add the 'General' category at the beginning of the list
        categoryDTOs.add(0, generalCategory);

        return categoryDTOs;
    }


    @Transactional
    public void bulkCreateUsersAndProducts(List<BulkUploadDTO.UserProductInput> userInputs) {
        for (BulkUploadDTO.UserProductInput userInput : userInputs) {
            User user = userRepository.findById(userInput.getUserId())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setName(userInput.getUserName());
                        newUser.setEmail(generatePlaceholderEmail(userInput.getUserName()));
                        return userRepository.save(newUser);
                    });

            for (BulkUploadDTO.ProductInput productInput : userInput.getProducts()) {
                Product product = new Product();
                product.setProductName(productInput.getProductName());
                product.setDescription(productInput.getDescription());
                product.setUser(user);

                Set<Category> categories = new HashSet<>();
                for (Long categoryId : productInput.getCategoryIds()) {
                    Category category = categoryRepository.findById(categoryId)
                            .orElseGet(() -> {
                                Category newCategory = new Category();
                                newCategory.setCategoryId(categoryId);
                                return categoryRepository.save(newCategory);
                            });
                    categories.add(category);
                }
                product.setCategories(categories);

                productRepository.save(product);
            }
        }
    }

//    @Transactional
//    public void bulkCreateUsersAndProducts(List<BulkUploadDTO.UserProductInput> userInputs) {
//        for (BulkUploadDTO.UserProductInput userInput : userInputs) {
//            User user = userRepository.findById(userInput.getUserId())
//                    .orElseGet(() -> {
//                        User newUser = new User();
//                        newUser.setName(userInput.getUserName());
//                        newUser.setEmail(generatePlaceholderEmail(userInput.getUserName()));
//                        return userRepository.save(newUser);
//                    });
//
//            for (BulkUploadDTO.ProductInput productInput : userInput.getProducts()) {
//                // Check for duplicate product name, ignoring case
//                String productName = productInput.getProductName();
//                boolean productExists = productRepository
//                        .findAll() // or use a more efficient query method if available
//                        .stream()
//                        .anyMatch(p -> p.getProductName().equalsIgnoreCase(productName));
//
//                if (productExists) {
//                    // Handle duplicate name case, e.g., skip or log
//                    continue; // Skip this product
//                }
//
//                Product product = new Product();
//                product.setProductName(productName);
//                product.setDescription(productInput.getDescription());
//                product.setUser(user);
//
//                Set<Category> categories = new HashSet<>();
//                for (Long categoryId : productInput.getCategoryIds()) {
//                    Category category = categoryRepository.findById(categoryId)
//                            .orElseGet(() -> {
//                                Category newCategory = new Category();
//                                newCategory.setCategoryId(categoryId);
//                                return categoryRepository.save(newCategory);
//                            });
//                    categories.add(category);
//                }
//                product.setCategories(categories);
//
//                productRepository.save(product);
//            }
//        }
//    }


    private String generatePlaceholderEmail(String userName) {
        return userName.replaceAll(" ", "_").toLowerCase() + "@placeholder.com";
    }

    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return new ProductDTO(product.getProductId(), product.getProductName(), product.getDescription());
    }

    @Override
    public ProductDTO updateProduct(Long productId, String productName, String description) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found for ID: " + productId));

        boolean productNameExists = productRepository
                .findByProductNameAndProductIdNot(productName, productId)
                .isPresent();

        if (productNameExists) {
            throw new IllegalArgumentException("Product name '" + productName + "' already exists.");
        }
        existingProduct.setProductName(productName);
        existingProduct.setDescription(description);

        Product updatedProduct = productRepository.save(existingProduct);
        return new ProductDTO(updatedProduct.getProductId(), updatedProduct.getProductName(), updatedProduct.getDescription());
    }


    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found for ID: " + productId));

        product.setCategories(new HashSet<>());
        productRepository.save(product);

        productRepository.deleteById(productId);
    }

    @Override
    public List<CategoryDTO> getCategoriesByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found for ID: " + productId));

        return product.getCategories().stream()
                .map(category -> new CategoryDTO(category.getCategoryId(), category.getName(), category.getDescription(), null))
                .collect(Collectors.toList());
    }



}
