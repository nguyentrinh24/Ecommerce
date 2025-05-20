package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
    List<ProductImage> findByProductId(Product product);
}
