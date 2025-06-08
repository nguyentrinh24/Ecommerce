package com.project.Ecommerce.Controller;

import com.project.Ecommerce.DTOs.CategoryDTOs;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Respones.Category.UpdateCategoryResponses;
import com.project.Ecommerce.Service.CategoryService;
import com.project.Ecommerce.Component.LocalizationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoryService categoryService;
    private final LocalizationUtil localizationUtil;

    // CREATE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("")
    public ResponseEntity<?> createCategory(
            @Valid @RequestBody CategoryDTOs categoryDTO,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            String messages = bindingResult.getFieldErrors()
                    .stream()
                    .map(err -> err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
            return ResponseEntity.badRequest().body(messages);
        }

        try {
            categoryService.createCategory(categoryDTO);
            return ResponseEntity.ok().body(
                    localizationUtil.getMessage(INSERT_CATEGORY_SUCCESSFULLY)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    localizationUtil.getMessage(INSERT_CATEGORY_FAILED, e.getMessage())
            );
        }
    }

    //  GET ALL CATEGORIES
    @GetMapping("")
    public ResponseEntity<?> getAllCategories(
            @RequestParam("page") int page,
            @RequestParam("limit") int limit
    ) {
        List<Category> categoryList = categoryService.findAllCategories();
        return ResponseEntity.ok(categoryList);
    }



    //  UPDATE CATEGORY
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponses> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTOs categoryDTO
    ) {
        try {
            categoryService.updateCategory(id, categoryDTO);
            return ResponseEntity.ok(
                    UpdateCategoryResponses.builder()
                            .message(localizationUtil.getMessage(UPDATE_CATEGORY_SUCCESSFULLY))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    UpdateCategoryResponses.builder()
                            .message("Cập nhật thất bại: " + e.getMessage())
                            .build()
            );
        }
    }

    //  DELETE CATEGORY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(
                    localizationUtil.getMessage(DELETE_CATEGORY_SUCCESSFULLY, id)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    "Xóa thất bại: " + e.getMessage()
            );
        }
    }
}
