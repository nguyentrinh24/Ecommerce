package com.project.Ecommerce.Controller;


import com.project.Ecommerce.DTOs.CategoryDTOs;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
public class ProductsController {

    @GetMapping("")
    public ResponseEntity<?> getAllProducts()
    {
        return ResponseEntity.ok().body("lấy sản phẩm thành công ");
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addProduct(
            @Valid @ModelAttribute ProductsDTOs productsDTOs,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> messages = bindingResult.getFieldErrors()
                        .stream()
                        .map((error) -> error.getDefaultMessage())
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(messages);
            }

            List<MultipartFile> files = productsDTOs.getFile();
            // Check null hoặc rỗng
            if (files == null || files.isEmpty() ) {
                throw new IOException("File không hợp lệ hoặc bị trống");
            }

            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    if (file.getSize() > 10 * 1024 * 1024) {
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body("Kích thước ảnh lớn hơn 10MB");
                    }

                    // Kiểm tra định dạng ảnh
                    String originalFilename = file.getOriginalFilename();
                    if (originalFilename == null || !originalFilename.toLowerCase().matches(".*\\.(png|jpg|jpeg)$")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body("Chỉ chấp nhận ảnh định dạng PNG, JPG, JPEG");
                    }

                    // Lưu file
                    storeFile(file);
                }
            }

            return ResponseEntity.ok().body("Thêm sản phẩm thành công!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
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
    public ResponseEntity<?> addProducts(@PathVariable Long id)
    {
        return ResponseEntity.ok().body(String.format("remove id=%d products",id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducts(@PathVariable Long id ){
        return ResponseEntity.ok().body(String.format("update id=%d products",id));
    }


}
