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

    @JsonProperty("price")
    private Double price;

    @JsonProperty("number_of_products")
    private int numberOfProducts;

    @JsonProperty("total_money")
    private Double totalMoney;

    private String color;

    public static OrderDetailResponses fromOrderDetail(OrderDetail orderDetail) {
        return OrderDetailResponses
                .builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .productId(orderDetail.getProduct().getId())
                .price(orderDetail.getPrice())
                .numberOfProducts(orderDetail.getNumberOfProduct())
                .totalMoney(orderDetail.getTotalMoney())
                .color(orderDetail.getColor())
                .build();
    }
}
