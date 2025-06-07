package com.project.Ecommerce.Service;

import com.github.javafaker.Faker;
import com.project.Ecommerce.DTOs.ProductImageDTOs;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Repository.CategoriRepository;
import com.project.Ecommerce.Repository.ProductImageRepository;
import com.project.Ecommerce.Repository.ProductRepository;
import com.project.Ecommerce.Respones.Product.ProductResponses;
import com.project.Ecommerce.Service.Iml.ProductServiceIml;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceIml {
    private final ProductRepository productRepository;
    private final CategoriRepository categoriRepository;
    private final ProductImageRepository productImageRepository;

    private final String uploadPath = "uploads";

    @Override
    public Product createProduct(ProductsDTOs dto, List<MultipartFile> images) throws Exception {
        Category category = categoriRepository.findById(dto.getCategoryID())
                .orElseThrow(() -> new DataNotFoundException("Category not found"));

        Product product = ProductResponses.toProductEntity(category, dto);
        product = productRepository.save(product);

        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                String fileName = storeFile(file); // lưu ảnh thư mục
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(fileName);
                productImage.setProductId(product);
                productImageRepository.save(productImage);
            }
        }

        return product;
    }


    @Override
    public Product getProductById(long id) throws Exception {
        return productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));
    }

    @Override
    public Product updateProduct(long id, ProductsDTOs productDTO) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));

        Category category = categoriRepository.findById(productDTO.getCategoryID())
                .orElseThrow(() -> new Exception("Category not found"));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategoryId(category);
        if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isBlank()) {
            product.setThumbnail(productDTO.getThumbnail());
        }



        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTOs dto) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        List<ProductImage> images = productImageRepository.findByProductId(product);
        if (images.size() >= ProductImage.Maximum_image) {
            throw new Exception("Exceeded image limit");
        }

        ProductImage image = ProductImage.builder()
                .productId(product)
                .imageUrl(dto.getImageUrl())
                .build();

        return productImageRepository.save(image);
    }

    @Override
    public Page<ProductResponses> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest) {
        Page<Product> page = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return page.map(ProductResponses::toProductResponse);
    }

    @Override
    public String uploadProductImages(Long productId, List<MultipartFile> files) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        List<ProductImage> existing = productImageRepository.findByProductId(product);
        if (existing.size() + files.size() > ProductImage.Maximum_image) {
            throw new Exception("Too many images");
        }

        for (MultipartFile file : files) {
            if (file.getSize() > 10 * 1024 * 1024) throw new Exception("File too large");
            if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().matches(".*\\.(jpg|jpeg|png)$"))
                throw new Exception("Invalid file format");

            String savedName = storeFile(file);
            productImageRepository.save(ProductImage.builder()
                    .imageUrl(savedName)
                    .productId(product)
                    .build());
        }

        return "Upload thành công";
    }

    private String storeFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + Paths.get(file.getOriginalFilename()).getFileName().toString();
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

        Path target = uploadDir.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    @Override
    public Resource viewProductImage(String imageName) throws Exception {
        Path filePath = Paths.get(uploadPath, imageName);
        if (!Files.exists(filePath)) {
            filePath = Paths.get(uploadPath, "notfound.jpeg");
        }

        if (!Files.exists(filePath)) {
            throw new Exception("Image not found");
        }

        return new UrlResource(filePath.toUri());
    }

    @Override
    public List<Product> getProductsByIdsString(String ids) throws Exception {
        List<Long> parsedIds = Arrays.stream(ids.split(","))
                .filter(s -> !s.isBlank())
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return productRepository.findProductsByIds(parsedIds);
    }

//    @Override
//    public String generateFakeProducts() {
////        Faker faker = new Faker();
////        int max = 100;
////        int created = 0;
////
////        for (int i = 0; i < max; i++) {
////            String name = faker.commerce().productName();
////            if (productRepository.existsByName(name)) continue;
////
////            try {
////                Product product = createProduct(ProductsDTOs.builder()
////                        .name(name)
////                        .description(faker.lorem().sentence())
////                        .price((double) faker.number().numberBetween(1000, 100_000_000))
////                        .thumbnail("https://picsum.photos/300/300?random=" + i)
////                        .categoryID((long) faker.number().numberBetween(2, 5))
////                        .build());
////
////                for (int j = 0; j < 5; j++) {
////                    productImageRepository.save(ProductImage.builder()
////                            .productId(product)
////                            .imageUrl("https://picsum.photos/300/300?random=" + faker.random().nextInt(1000))
////                            .build());
////                }
////
////                created++;
////            } catch (Exception ignored) {}
////        }
////
////        return "Generated " + created + " fake products.";
//    }

    @Override
    public ProductResponses getProductDetail(Long id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found"));
        return ProductResponses.toProductResponse(product);
    }

    @Override
    public void deleteProductImage(Long id) throws Exception {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new Exception("Image not found"));

        productImageRepository.delete(image);

        // nếu ảnh bị xoá là ảnh thumbnail hiện tại reset thumbnail về null hoặc ảnh khác
        Product product = image.getProductId();
        if (product.getThumbnail() != null && product.getThumbnail().equals(image.getImageUrl())) {
            product.setThumbnail(null); // hoặc gán ảnh khác nếu có
            productRepository.save(product);
        }
    }

}
