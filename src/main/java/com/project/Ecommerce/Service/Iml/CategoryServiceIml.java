package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.CategoryDTOs;
import com.project.Ecommerce.Model.Category;

import java.util.List;

public interface CategoryServiceIml {
    Category findById(Long id);

    Category createCategory(CategoryDTOs categoryDTO);

    Category updateCategory(Long id, CategoryDTOs categoryDTO);

    Category deleteCategory(Long id);

    List<Category> findAllCategories();


}
