package com.baylor.practicum_new.services;

import com.baylor.practicum_new.entities.Category;
import com.baylor.practicum_new.entities.Product;
import com.baylor.practicum_new.repositories.CategoryRepository;
import com.baylor.practicum_new.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;


    public Category create(Category category) {
        Optional<Category> checkCategory = categoryRepository.findByName(category.getName());
        if (checkCategory.isPresent()) {
            throw new RuntimeException("Category already exists");
        }
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Long categoryId, Category newCategoryData) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found for ID: " + categoryId));

        if (!category.getName().equalsIgnoreCase(newCategoryData.getName()) && categoryRepository.existsByName(newCategoryData.getName())) {
            throw new RuntimeException("Another category with the same name already exists");
        }

        category.setName(newCategoryData.getName());
        category.setDescription(newCategoryData.getDescription());

        return categoryRepository.save(category);
    }

//    public void deleteCategory(Long categoryId) {
//        Category category = categoryRepository.findById(categoryId)
//                .orElseThrow(() -> new EntityNotFoundException("Category not found for ID: " + categoryId));
//        categoryRepository.delete(category);
//    }



    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found for ID: " + categoryId));
        List<Product> products = productRepository.findProductsByCategoryId(categoryId);
        productRepository.deleteAll(products);
        categoryRepository.delete(category);
    }

}

