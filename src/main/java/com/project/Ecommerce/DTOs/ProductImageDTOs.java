package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.Model.Product;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductImageDTOs {

    @JsonProperty( "product_id")
    private Long  productId;

    @JsonProperty("image_url")
    @Size(min = 1, max = 300,message = "lenght image url maximum 300")
    private String imageUrl;
}
