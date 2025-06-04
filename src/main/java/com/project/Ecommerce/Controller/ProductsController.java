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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductService productService;
    private final LocalizationUtil localizationUtil;

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductsDTOs productsDTOs, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream().map(e -> e.getDefaultMessage()).collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        try {
            productService.createProduct(productsDTOs);
            return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_CREATED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

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

    @GetMapping("")
    public ResponseEntity<ProductListResponses> getAllProducts(@RequestParam(defaultValue = "") String keyword,
                                                               @RequestParam(defaultValue = "0") Long categoryId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int limit) {
        try {
            int actualPage = Math.max(0, page - 1);
            PageRequest pageRequest = PageRequest.of(actualPage, limit, Sort.by("id"));
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

    @PostMapping("/generateProductFake")
    public ResponseEntity<?> generateProductFake() {
        try {
            productService.generateFakeProducts();
            return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_FAKE_GENERATE_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating fake product: " + e.getMessage());
        }
    }
}