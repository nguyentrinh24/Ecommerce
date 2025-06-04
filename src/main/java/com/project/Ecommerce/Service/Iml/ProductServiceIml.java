package com.project.Ecommerce.Service.Iml;

import com.project.Ecommerce.DTOs.ProductImageDTOs;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Respones.Product.ProductResponses;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductServiceIml {
    Product createProduct(ProductsDTOs productDTO) throws Exception;

    Product getProductById(long id) throws Exception;

    Product updateProduct(long id, ProductsDTOs productDTO) throws Exception;

    void deleteProduct(long id);

    List<Product> findProductsByIds(List<Long> productIds);

    ProductImage createProductImage(Long productId, ProductImageDTOs productImageDTO) throws Exception;

    Page<ProductResponses> getAllProducts(String keyword, Long categoryId, PageRequest pageRequest);

    String uploadProductImages(Long productId, List<MultipartFile> files) throws Exception;

    Resource viewProductImage(String imageName) throws Exception;

    List<Product> getProductsByIdsString(String ids) throws Exception;

    String generateFakeProducts();
    ProductResponses getProductDetail(Long id) throws Exception;

}
