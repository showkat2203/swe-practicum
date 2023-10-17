package com.baylor.practicum_new.services.impl;

import com.baylor.practicum_new.dto.ProductDTO;
import com.baylor.practicum_new.dto.UserProductsDTO;
import com.baylor.practicum_new.entities.Product;
import com.baylor.practicum_new.entities.User;
import com.baylor.practicum_new.repositories.ProductRepository;
import com.baylor.practicum_new.repositories.UserRepository;
import com.baylor.practicum_new.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

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

}
