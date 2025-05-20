package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.CategoryDTOs;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoriesController {

    // DI
    private final CategoryService  categoryService;



//Create Category
    @PostMapping()
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTOs categoryDTO,
            BindingResult bindingResult
    ) {
        if  (bindingResult.hasErrors()) {
            List<String> messages = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> err.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(messages);
        }

        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok().body("this is create category successfully" + categoryDTO.toString()) ;
    }
    @GetMapping("")
    public ResponseEntity<?> getAllCategories( //http://localhost:8088/api/v1/categories?page=1&limit=10
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        List<Category> categoryList = categoryService.findAllCategories();
        return ResponseEntity.ok().body(categoryList.stream()) ;
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody CategoryDTOs categoryDTO) {
        categoryService.updateCategory(id, categoryDTO);
        return  ResponseEntity.ok().body("this is update category" + id);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().body("this is delete category" + id +"successfully");
    }
}
