package com.project.Ecommerce.Controller;

import com.project.Ecommerce.Component.LocalizationUtil;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Respones.Product.ProductListResponses;
import com.project.Ecommerce.Respones.Product.ProductResponses;
import com.project.Ecommerce.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductService productService;
    private final LocalizationUtil localizationUtil;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProductWithImages(
            @RequestPart("product") @Valid ProductsDTOs productDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            productService.createProduct(productDTO, images);
            return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_CREATED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductsDTOs dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            productService.updateProduct(id, dto);
            return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_UPDATED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_DELETED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/product_images/{id}")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long id) {
        try {
            productService.deleteProductImage(id); // gọi vào service để xóa ảnh
            return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete image: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponses> getAllProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            PageRequest pageRequest = PageRequest.of(page, limit, Sort.by("id")); //bỏ Math.max(0, page - 1)
            Page<ProductResponses> products = productService.getAllProducts(keyword, categoryId, pageRequest);
            return ResponseEntity.ok(ProductListResponses.builder()
                    .productResponses(products.getContent())
                    .total(products.getTotalPages())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductDetail(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadImages(@PathVariable Long id, @RequestParam("files") List<MultipartFile> files) {
        try {
            return ResponseEntity.ok(productService.uploadProductImages(id, files));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Resource image = productService.viewProductImage(imageName);
            String contentType = Files.probeContentType(Paths.get("uploads", imageName));
            return ResponseEntity.ok()
                    .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                    .body(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading image: " + e.getMessage());
        }
    }


    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        try {
            return ResponseEntity.ok(productService.getProductsByIdsString(ids));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể parse danh sách id: " + e.getMessage());
        }
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
//    @PostMapping("/generateProductFake")
//    public ResponseEntity<?> generateProductFake() {
//        try {
//            productService.generateFakeProducts();
//            return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_FAKE_GENERATE_SUCCESS));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Error creating fake product: " + e.getMessage());
//        }
//    }
}