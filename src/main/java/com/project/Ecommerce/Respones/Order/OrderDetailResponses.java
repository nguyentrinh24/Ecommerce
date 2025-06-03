package com.project.Ecommerce.Respones.Order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.Model.OrderDetail;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponses {
    private Long id;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    private Double price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @JsonProperty("total_money")
    private Double totalMoney;

    private String color;

    public static OrderDetailResponses fromOrderDetail(OrderDetail detail) {
        return OrderDetailResponses.builder()
                .id(detail.getId())
                .orderId(detail.getOrder().getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .price(detail.getPrice())
                .numberOfProducts(detail.getNumberOfProduct())
                .totalMoney(detail.getTotalMoney())
                .color(detail.getColor())
                .build();
    }

}
