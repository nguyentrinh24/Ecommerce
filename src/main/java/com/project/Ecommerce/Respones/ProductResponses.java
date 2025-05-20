package com.project.Ecommerce.Respones;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.DTOs.ProductsDTOs;
import com.project.Ecommerce.Model.Category;
import com.project.Ecommerce.Model.Product;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
@SuperBuilder


public class ProductResponses extends BaseResponses {

    private String name;
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryID;

   // Entity -> Response DTO
    public static ProductResponses toProductResponse(Product product) {
        return ProductResponses.builder()
                .name(product.getName())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .description(product.getDescription())
                .categoryID(product.getCategoryId().getId())
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
