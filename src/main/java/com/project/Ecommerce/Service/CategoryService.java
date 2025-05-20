package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.CategoryDTOs;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Repository.CategoriRepository;
import com.project.Ecommerce.Service.Iml.CategoryServiceIml;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
//Dependency Injection
@RequiredArgsConstructor

public class CategoryService implements CategoryServiceIml {
    private final CategoriRepository categoriRepository;



    @Override
    public Category findById(Long id) {
        return categoriRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not found"));
     }

    @Override
    public Category createCategory(CategoryDTOs categoryDTO) {
       Category newcategory = Category.builder()
                                   .name(categoryDTO.getName())
                                   .build();
       return categoriRepository.save(newcategory);
    }

    @Override
    public Category updateCategory(Long id, CategoryDTOs categoryDTO) {
        Category exitsCategory = categoriRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Category not found"));
        exitsCategory.setName(categoryDTO.getName());
        categoriRepository.save(exitsCategory);
        return exitsCategory;
    }

    @Override
    public Category deleteCategory(Long id) {
        Category exitsCategory = categoriRepository.findById(id).orElseThrow(()->new  EntityNotFoundException("Category not found"));

        // xóa cứng
        categoriRepository.deleteById(id);

        return  exitsCategory;
    }

    @Override
    public List<Category> findAllCategories() {
        return categoriRepository.findAll();
    }
}
