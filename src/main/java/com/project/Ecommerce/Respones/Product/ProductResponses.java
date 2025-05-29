package com.project.Ecommerce.Respones.Product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Model.ProductImage;
import com.project.Ecommerce.Respones.BaseResponses;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@SuperBuilder


public class ProductResponses extends BaseResponses {
    private Long productId;
    private String name;
    private Double price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryID;

    @JsonProperty("product_images")
    private List<ProductImage> productImages = new ArrayList<>();

   // Entity -> Response DTO
   public static ProductResponses toProductResponse(Product product) {
       return ProductResponses.builder()
               .productId(product.getId())
               .name(product.getName())
               .price(product.getPrice())
               .thumbnail(product.getThumbnail())
               .description(product.getDescription())
               .categoryID(product.getCategoryId().getId())
               .productImages(product.getProductImages() != null ? product.getProductImages() : new ArrayList<>())
               .createdAt(product.getCreatedAt())
               .updatedAt(product.getUpdatedAt())
               .build();
   }


    // DTO -> Entity
    public static Product toProductEntity( Category category,ProductsDTOs dto) {
        return Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .description(dto.getDescription())
                .thumbnail(dto.getThumbnail())
                .categoryId(category)
                .build();
    }
}
