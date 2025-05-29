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
import org.springframework.core.io.UrlResource;
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
            productService.createProduct(productsDTOs);
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
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads", imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                String contentType = Files.probeContentType(imagePath);
                MediaType mediaType = contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .body(resource);
            } else {
                java.nio.file.Path fallbackPath = Paths.get("uploads", "notfound.jpeg");
                UrlResource fallbackResource = new UrlResource(fallbackPath.toUri());

                if (fallbackResource.exists()) {
                    return ResponseEntity.ok()
                            .contentType(MediaType.IMAGE_JPEG)
                            .body(fallbackResource);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Image not found and fallback image is also missing.");
                }
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading image: " + e.getMessage());
        }
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
    public ResponseEntity<ProductListResponses> getAllProduct(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "categoryId") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            int actualPage = Math.max(page - 1, 0); // Ngăn page âm
            PageRequest pageRequest = PageRequest.of(actualPage, limit, Sort.by("id").ascending());

            Page<ProductResponses> responsesPage = productService.getAllProducts(keyword, categoryId, pageRequest);

            return ResponseEntity.ok(ProductListResponses.builder()
                    .productResponses(responsesPage.getContent())
                    .total(responsesPage.getTotalPages())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    //http://localhost:8088/api/v1/products/6
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable("id") Long productId) {
        try {
            Product existingProduct = productService.getProductById(productId);
            if (existingProduct == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
            }

            return ResponseEntity.ok(ProductResponses.toProductResponse(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        if (ids == null || ids.isBlank()) {
            return ResponseEntity.badRequest().body("Danh sách IDs không hợp lệ");
        }

        try {
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .filter(s -> !s.isBlank()) // chống empty
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Không thể parse danh sách id: " + e.getMessage());
        }
    }

//    @PostMapping("/generateProductFake")
//    public ResponseEntity<?> generateProductFake() {
//        Faker faker = new Faker();
//        int max = 100;
//
//        for (int i = 0; i < max; i++) {
//            String productName = faker.commerce().productName();
//            if (productService.existsByName(productName)) continue;
//
//            ProductsDTOs fakeProductDTO = ProductsDTOs.builder()
//                    .name(productName)
//                    .price((double) faker.number().numberBetween(10, 90_000_000))
//                    .description(faker.lorem().sentence())
//                    .thumbnail("https://picsum.photos/300/300?random=" + faker.random().nextInt(1000))
//                    .categoryID((long) faker.number().numberBetween(2, 5))
//                    .build();
//
//            try {
//                // Tạo product
//                Product savedProduct = productService.createProduct(fakeProductDTO);
//
//                // Tạo 5 ảnh giả
//                for (int j = 0; j < 5; j++) {
//                    ProductImage image = ProductImage.builder()
//                            .productId(savedProduct)
//                            .imageUrl("https://picsum.photos/300/300?random=" + faker.random().nextInt(1000))
//                            .build();
//
//                    productImageRepository.save(image); // Inject ProductImageRepository
//                }
//
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body("Error creating fake product: " + e.getMessage());
//            }
//        }
//
//        return ResponseEntity.ok(localizationUtil.getMessage(PRODUCT_FAKE_GENERATE_SUCCESS));
//    }

}

