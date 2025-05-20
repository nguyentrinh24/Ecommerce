package com.project.Ecommerce.Controller;

import com.github.javafaker.Faker;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Repository.ProductImageRepository;
import com.project.Ecommerce.Repository.ProductRepository;
import com.project.Ecommerce.Respones.ProductListResponses;
import com.project.Ecommerce.Respones.ProductResponses;
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

@RestController
@RequestMapping("${api.prefix}/products")

@RequiredArgsConstructor
public class ProductsController {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final ProductImageRepository productImageRepository;

    // @GetMapping("")
    // public ResponseEntity<?> getAllProducts()
    // {
    // List<Product> products = productRepository.findAll();
    //
    // return ResponseEntity.ok(products);
    // }

    @PostMapping(value = "")
    public ResponseEntity<?> addProduct(
            @Valid @RequestBody ProductsDTOs productsDTOs,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getFieldErrors()
                        .stream()
                        .map((error) -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(messages);
            }
            Product newProducts = productService.createProduct(productsDTOs);
            return ResponseEntity.ok().body(newProducts);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable("id") long productId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        // 1. Kiểm tra sản phẩm có tồn tại
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // 2. Kiểm tra file null hoặc rỗng
        if (files == null || files.isEmpty()) {
            throw new IOException("File không hợp lệ hoặc bị trống");
        }
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Một trong các file bị trống");
            }
        }

        // 3. Kiểm tra số ảnh hiện tại của sản phẩm
        List<ProductImage> existingImages = productImageRepository.findByProductId(existingProduct);
        if (existingImages == null) {
            existingImages = new ArrayList<>();
        }

        if (existingImages.size() + files.size() > ProductImage.Maximum_image) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Mỗi sản phẩm chỉ được tối đa 6 ảnh.");
        }

        // 4. Xử lý từng ảnh
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                if (file.getSize() > 10 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("Kích thước ảnh lớn hơn 10MB");
                }

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || !originalFilename.toLowerCase().matches(".*\\.(png|jpg|jpeg)$")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("Chỉ chấp nhận ảnh định dạng PNG, JPG, JPEG");
                }

                // 5. Lưu file
                String filename = storeFile(file);

                // 6. Lưu thông tin vào DB
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(filename)
                        .productId(existingProduct) // Gán sản phẩm vào đây
                        .build();
                productImageRepository.save(productImage);
            }
        }

        return ResponseEntity.ok("Tải ảnh thành công");
    }

    private String storeFile(MultipartFile file) throws IOException {

        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            Optional<Product> exits = productRepository.findById(id);
            if (exits.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(String.format("Không tìm thấy sản phẩm với id = %d", id));
            }

            productRepository.deleteById(id);
            return ResponseEntity.ok().body(String.format("Đã xoá sản phẩm id = %d thành công", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xoá sản phẩm: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducts(
            @PathVariable Long id,
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

            Product updatedProduct = productService.updateProduct(id, productsDTOs);
            return ResponseEntity.ok().body(updatedProduct);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponses> getAllProducts(
            @RequestParam("limit") int limit,
            @RequestParam("page") int page) {

        try {
            PageRequest pageRequest = PageRequest.of(page - 1, limit,
                    Sort.by(Sort.Direction.DESC, "createdAt"));

            // Gọi service để lấy sản phẩm đã phân trang và sort
            Page<ProductResponses> responsesPage = productService.getAllProducts(pageRequest);

            // lấy tổng số trang
            int totalPages = responsesPage.getTotalPages();
            List<ProductResponses> productList = responsesPage.getContent();
            // Trả về data đúng logic
            return ResponseEntity.ok(ProductListResponses.builder()
                    .productResponses(productList)
                    .total(totalPages)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    // fake dâta
//    @PostMapping("/generateProductFake")
//    public ResponseEntity<?> generateProductFake() {
//        Faker faker = new Faker();
//        int max =1000;
//        for (int i = 0; i <max ; i++) {
//            String productName = faker.commerce().productName();
//            if(productService.existsByName(productName)){
//                continue;
//            }
//
//            ProductsDTOs productsDTOsfake =  ProductsDTOs.builder()
//                    .name(productName)
//                    .price((float) faker.number().numberBetween(10, 90_000_000))
//                    .description(faker.lorem().sentence())
//                    .thumbnail("")
//                    .categoryID((long) faker.number().numberBetween(2, 5))
//                    .build();
//            try{
//                productService.createProduct(productsDTOsfake);
//            }catch (Exception e){
//                return ResponseEntity.badRequest().body(null);
//            }
//        }
//
//
//        return ResponseEntity.ok().body("faker data successfully generated");
//
//    }
}
