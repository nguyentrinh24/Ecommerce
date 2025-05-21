package com.project.Ecommerce.DTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class OrderDetailDTOs {

    @JsonProperty("order_id")
    @Min(value = 1,message = "order id must be > 0")
    private  long orderId;

    @JsonProperty("product_id")
    @Min(value = 1,message = "product id must be > 0")
    private  long productId;

    @JsonProperty("price")
    @Min(value = 1,message = "total must be >= 0")
    private Double price;

    @JsonProperty("number_of_product")
    @Min(value = 1,message = "number of products must be >= 1")
    private int number_of_products;

    @JsonProperty("total_money")
    @Min(value = 1,message = "total money must be > 0")
    private  Double total_money;

    @JsonProperty("color")
    private String color;

}
