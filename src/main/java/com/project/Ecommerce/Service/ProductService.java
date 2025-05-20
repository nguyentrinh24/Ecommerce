package com.project.Ecommerce.Service;

import com.project.Ecommerce.DTOs.ProductImageDTOs;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Exceptions.DataNotFoundException;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Repository.CategoriRepository;
import com.project.Ecommerce.Repository.ProductImageRepository;
import com.project.Ecommerce.Repository.ProductRepository;
import com.project.Ecommerce.Respones.ProductResponses;
import com.project.Ecommerce.Service.Iml.ProductServiceIml;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.util.List;


@Service
@RequiredArgsConstructor

public class ProductService implements ProductServiceIml {
    private final ProductRepository productRepository;
    private final CategoriRepository categoriRepository;
    private final ProductImageRepository productImageRepository;

    @Override
    public Product createProduct(ProductsDTOs productDTO) throws Exception {
        //check id
        Category exitsCategory = categoriRepository.findById(productDTO
                 .getCategoryID())
                .orElseThrow(
                        ()->new DataNotFoundException("can not category with id"));

        Product product = ProductResponses.toProductEntity(exitsCategory,productDTO);

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(long id) throws Exception {


        return productRepository.findById(id).orElseThrow(() -> new Exception("Product not found id"));
    }

    @Override
    public Page<ProductResponses> getAllProducts(PageRequest pageRequest) throws Exception {
        return productRepository.findAll(pageRequest).map(ProductResponses::toProductResponse);


    }

    @Override
    public Product updateProduct(long id, ProductsDTOs productDTO) throws Exception {
        // check id product
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new Exception("Product not found id"));

        // Check category tồn tại
        Category category = categoriRepository.findById(productDTO.getCategoryID())
                .orElseThrow(() -> new Exception("Category not found"));

        // Update
        if (productDTO.getName() != null && !productDTO.getName().isEmpty()) {
            existingProduct.setName(productDTO.getName());
        }

        if (productDTO.getDescription() != null && !productDTO.getDescription().isEmpty()) {
            existingProduct.setDescription(productDTO.getDescription());
        }

        if (productDTO.getThumbnail() != null && !productDTO.getThumbnail().isEmpty()) {
            existingProduct.setThumbnail(productDTO.getThumbnail());
        }

        if (productDTO.getPrice() != null && productDTO.getPrice() >= 0) {
            existingProduct.setPrice(productDTO.getPrice());
        }

        // Update category
        existingProduct.setCategoryId(category);

        return productRepository.save(existingProduct);
    }


    @Override
    public void deleteProduct(long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new DateTimeException("Product not found with id: " + id));

        productRepository.delete(product);
    }

    @Override
    public boolean existsByName(String name) {
        return false;
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return List.of();
    }

    @Override
    public ProductImage createProductImage(Long productId, ProductImageDTOs productImageDTO) throws Exception {
        // check id product
        Product exitsProduct = productRepository.findById(productId).orElseThrow(() -> new Exception("Product not found with id: " + productId));

        // gioi han update anh <6
        List<ProductImage> images = productImageRepository.findByProductId(exitsProduct);
        if (images.size() >= ProductImage.Maximum_image) {
            throw new Exception("A product can only have up to 6 images");
        }

        //Convert
        ProductImage productImage =  ProductImage.builder()
                .productId(exitsProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // luu
        return productImageRepository.save(productImage);
    }

    @Override
    public List<ProductImage> findByProductId(Long productId) {
        return List.of();
    }
}
