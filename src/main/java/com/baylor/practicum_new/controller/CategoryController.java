package com.baylor.practicum_new.controller;


import com.baylor.practicum_new.dto.CategoryDTO;
import com.baylor.practicum_new.entities.Category;
import com.baylor.practicum_new.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Category category) {
        String categoryName = category.getName();

        try {
            Category new_category = categoryService.create(category);
            return new ResponseEntity<>(Collections.singletonMap("categoryId", new_category.getCategoryId()), HttpStatus.CREATED);
        } catch (RuntimeException e) {

            if ("Category already exists".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Category already exists");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Category");
            }
        }
    }

    @GetMapping("")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(category -> new CategoryDTO(category.getCategoryId(), category.getName(), category.getDescription(), null))
                .collect(Collectors.toList());

        return new ResponseEntity<>(categoryDTOs, HttpStatus.OK);
    }



}
