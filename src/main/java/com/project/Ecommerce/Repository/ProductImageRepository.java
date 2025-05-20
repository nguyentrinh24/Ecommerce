package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
}
