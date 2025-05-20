package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.Model.Product;
import com.project.Ecommerce.Respones.ProductResponses;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class ProductsDTOs {

    @NotEmpty(message = "name cannot empty")
    private String name;

    @Min(value = 0,message = "price > 0")
    private Float price;
    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryID;



}
