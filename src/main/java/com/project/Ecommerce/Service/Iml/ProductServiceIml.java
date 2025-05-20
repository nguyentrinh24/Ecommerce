package com.project.Ecommerce.Service.Iml;


import com.project.Ecommerce.DTOs.ProductImageDTOs;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Respones.ProductResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductServiceIml {
    Product createProduct(ProductsDTOs productDTO) throws Exception;

    Product getProductById(long id) throws Exception;

    Page<ProductResponses> getAllProducts(PageRequest pageRequest) throws Exception;

    Product updateProduct(long id, ProductsDTOs productDTO) throws Exception;

    void deleteProduct(long id);

    boolean existsByName(String name);

    List<Product> findProductsByIds(List<Long> productIds);

    ProductImage createProductImage(
            Long productId,
            ProductImageDTOs productImageDTO) throws Exception;

    List<ProductImage> findByProductId(Long productId);
}
