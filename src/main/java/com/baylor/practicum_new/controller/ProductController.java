package com.baylor.practicum_new.controller;

import com.baylor.practicum_new.dto.*;
import com.baylor.practicum_new.dto.CategoryDTO;
import com.baylor.practicum_new.services.ProductService;
import com.baylor.practicum_new.entities.Product;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> productDetails) {
        try {
            Long userId = Long.parseLong(productDetails.get("userId").toString());
            String productName = productDetails.get("productName").toString();
            String description = productDetails.get("description").toString();

            ProductDTO product = productService.createProduct(userId, productName, description);
            return new ResponseEntity<>(Collections.singletonMap("productId", product.getProductId()), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{productId}/edit", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody Map<String, Object> productDetails) {
        try {
            String productName = productDetails.get("productName").toString();
            String description = productDetails.get("description").toString();

            ProductDTO product = productService.updateProduct(productId, productName, description);

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("productId", product.getProductId());
            responseBody.put("message", "Product updated successfully");

            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }


//    @RequestMapping(value = "/create", method = RequestMethod.POST)
//    public ResponseEntity<?> createOrUpdateProduct(@RequestBody Map<String, Object> productDetails) {
//        try {
//            Long userId = Long.parseLong(productDetails.get("userId").toString());
//            String productName = productDetails.get("productName").toString();
//            String description = productDetails.get("description").toString();
//            Long productId = productDetails.containsKey("productId") ? Long.parseLong(productDetails.get("productId").toString()) : null;
//
//            ProductDTO product;
//            String message;
//            if (productId == null) {
//                product = productService.createProduct(userId, productName, description);
//                message = "Product created successfully";
//            } else {
//                product = productService.updateProduct(productId, productName, description);
//                message = "Product updated successfully";
//            }
//
//            Map<String, Object> responseBody = new HashMap<>();
//            responseBody.put("productId", product.getProductId());
//            responseBody.put("message", message);
//
//            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
//        } catch (IllegalArgumentException | EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
//        }
//    }

    @RequestMapping(value = "/link-categories", method = RequestMethod.POST)
    public ResponseEntity<?> linkCategoriesToProduct(@RequestBody ProductCategoryDTO productCategoryDTO) {
        ProductCategoryDTO updatedProduct = productService.createProductWithCategory(productCategoryDTO);

        Set<Map<String, Long>> formattedCategoryIds = updatedProduct.getCategoryIds().stream()
                .map(id -> Collections.singletonMap("categoryId", id))
                .collect(Collectors.toSet());

        Map<String, Object> response = new HashMap<>();
        response.put("productId", updatedProduct.getProductId());
        response.put("productName", updatedProduct.getProductName());
        response.put("description", updatedProduct.getDescription());
        response.put("categoryIds", formattedCategoryIds);

        return ResponseEntity.ok(response);
    }



    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/user/:{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<ProductDTO>> getProductsByUserId(@PathVariable Long userId) {
        List<ProductDTO> products = productService.getProductsByUserId(userId);
        return ResponseEntity.ok(products);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserProductsDTO>> getProductsGroupedByUsers() {
        return new ResponseEntity<>(productService.getAllUsersWithProducts(), HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{categoryId}", method = RequestMethod.GET)
    public ResponseEntity<List<Map<String, Object>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductCategoryDTO> products = productService.getProductsByCategory(categoryId);

        List<Map<String, Object>> formattedProducts = products.stream().map(dto -> {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("productId", dto.getProductId());
            productMap.put("productName", dto.getProductName());
            productMap.put("description", dto.getDescription());

            List<Map<String, Long>> formattedCategoryIds = dto.getCategoryIds().stream()
                    .map(id -> Collections.singletonMap("categoryId", id))
                    .collect(Collectors.toList());
            productMap.put("categoryIds", formattedCategoryIds);

            return productMap;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(formattedProducts);
    }


    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getProductsGroupedByCategories() {
        List<CategoryDTO> categories = productService.getProductsGroupedByCategories();
        return ResponseEntity.ok(categories);
    }


    @PostMapping("/bulk-create")
    public ResponseEntity<?> bulkCreateProducts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            List<BulkUploadDTO.UserProductInput> userProductInputs = parseJsonFile(file);
            productService.bulkCreateUsersAndProducts(userProductInputs);
            return ResponseEntity.ok("Bulk data processed successfully");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error processing file: " + e.getMessage());
        }
    }

    private List<BulkUploadDTO.UserProductInput> parseJsonFile(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, BulkUploadDTO.UserProductInput.class);
        return objectMapper.readValue(file.getInputStream(), type);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

//    @PutMapping("/{productId}")
//    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO) {
//        ProductDTO updatedProduct = productService.updateProduct(productId, productDTO);
//        return ResponseEntity.ok(updatedProduct);
//    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{productId}/categories", method = RequestMethod.GET)
    public ResponseEntity<List<CategoryDTO>> getCategoriesByProduct(@PathVariable Long productId) {
        try {
            List<CategoryDTO> categories = productService.getCategoriesByProduct(productId);
            return ResponseEntity.ok(categories);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }


}
