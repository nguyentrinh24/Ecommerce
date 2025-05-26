package com.project.Ecommerce.Service.Iml;


import com.project.Ecommerce.DTOs.ProductImageDTOs;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Respones.Product.ProductResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductServiceIml {
    Product createProduct(ProductsDTOs productDTO) throws Exception;

    Product getProductById(long id) throws Exception;



    Product updateProduct(long id, ProductsDTOs productDTO) throws Exception;

    void deleteProduct(long id);





    ProductImage createProductImage(
            Long productId,
            ProductImageDTOs productImageDTO) throws Exception;

    Page<ProductResponses> getAllProducts(String keyword,
                                         Long categoryId, PageRequest pageRequest);
}
