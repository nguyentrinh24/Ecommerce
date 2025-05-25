package com.project.Ecommerce.Controller;

import com.github.javafaker.Faker;
import com.project.Ecommerce.Component.LocalizationUtil;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Repository.ProductImageRepository;
import com.project.Ecommerce.Repository.ProductRepository;
import com.project.Ecommerce.Respones.Product.ProductListResponses;
import com.project.Ecommerce.Respones.Product.ProductResponses;
import com.project.Ecommerce.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.Ecommerce.Util.MessagesKey.*;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtil localizationUtil;

    @PostMapping("")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductsDTOs productsDTOs, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(messages);
            }
            Product newProduct = productService.createProduct(productsDTOs);
            return ResponseEntity.ok().body(localizationUtil.getMessage(PRODUCT_CREATED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") long productId, @RequestParam("files") List<MultipartFile> files) throws IOException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, localizationUtil.getMessage(PRODUCT_NOT_FOUND)));

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body(localizationUtil.getMessage(PRODUCT_IMAGE_EMPTY));
        }

        List<ProductImage> existingImages = productImageRepository.findByProductId(existingProduct);
        existingImages = existingImages == null ? new ArrayList<>() : existingImages;

        if (existingImages.size() + files.size() > ProductImage.Maximum_image) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(localizationUtil.getMessage(PRODUCT_IMAGE_LIMIT));
        }

        for (MultipartFile file : files) {
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(localizationUtil.getMessage(PRODUCT_IMAGE_TOO_LARGE));
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().matches(".*\\.(png|jpg|jpeg)$")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body(localizationUtil.getMessage(PRODUCT_IMAGE_INVALID_FORMAT));
            }

            String filename = storeFile(file);
            ProductImage productImage = ProductImage.builder()
                    .imageUrl(filename)
                    .productId(existingProduct)
                    .build();
            productImageRepository.save(productImage);
        }

        return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_IMAGE_UPLOAD_SUCCESS));
    }

    private String storeFile(MultipartFile file) throws IOException {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        java.nio.file.Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Optional<Product> exists = productRepository.findById(id);
            if (exists.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(localizationUtil.getMessage(PRODUCT_NOT_FOUND));
            }
            productRepository.deleteById(id);
            return ResponseEntity.ok().body(localizationUtil.getMessage(PRODUCT_DELETED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducts(@PathVariable Long id,
                                            @Valid @RequestBody ProductsDTOs productsDTOs,
                                            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                        .stream()
                        .map(error -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }
            productService.updateProduct(id, productsDTOs);
            return ResponseEntity.ok().body(localizationUtil.getMessage(PRODUCT_UPDATED_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtil.getMessage(PRODUCT_OPERATION_FAILED, e.getMessage()));
        }
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponses> getAllProducts(@RequestParam("limit") int limit,
                                                               @RequestParam("page") int page) {
        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<ProductResponses> responsesPage = productService.getAllProducts(pageRequest);
            return ResponseEntity.ok(ProductListResponses.builder()
                    .productResponses(responsesPage.getContent())
                    .total(responsesPage.getTotalPages())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/generateProductFake")
    public ResponseEntity<?> generateProductFake() {
        Faker faker = new Faker();
        int max = 1000;
        for (int i = 0; i < max; i++) {
            String productName = faker.commerce().productName();
            if (productService.existsByName(productName)) continue;

            ProductsDTOs productsDTOsfake = ProductsDTOs.builder()
                    .name(productName)
                    .price((double) faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryID((long) faker.number().numberBetween(2, 5))
                    .build();

            try {
                productService.createProduct(productsDTOsfake);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_FAKE_GENERATE_SUCCESS));
    }
}

