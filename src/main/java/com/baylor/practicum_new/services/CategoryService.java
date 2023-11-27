package com.baylor.practicum_new.services;

import com.baylor.practicum_new.entities.Category;
import com.baylor.practicum_new.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

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

}

