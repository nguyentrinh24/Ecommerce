package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.CategoryDTOs;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Repository.CategoriRepository;
import com.project.Ecommerce.Service.Iml.CategoryServiceIml;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceIml {
    private final CategoriRepository categoryRepository;

    @Override
    @Transactional
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    public Category createCategory(@Valid CategoryDTOs categoryDTO) {
        if (!StringUtils.hasText(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        Category newCategory = Category.builder()
                .name(categoryDTO.getName())
                .build();
        return categoryRepository.save(newCategory);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, @Valid CategoryDTOs categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        if (!StringUtils.hasText(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }
        existingCategory.setName(categoryDTO.getName());
        return categoryRepository.save(existingCategory);
    }

    @Override
    @Transactional
    public Category deleteCategory(Long id) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        categoryRepository.deleteById(id);
        return existingCategory;
    }

    @Override
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
}