package com.project.Ecommerce.Respones.Order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.Ecommerce.Model.Order;
import com.project.Ecommerce.Model.OrderDetail;
import com.project.Ecommerce.Respones.BaseResponses;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@Getter
@Setter


public class OrderResponse extends BaseResponses {
    private long orderId;
    @JsonProperty("user_id")
    private long user_id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("order_date")
    @JsonFormat(pattern = "yyyy-M-d")
    private LocalDateTime  orderDate;

    private String note;
    private String status;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_date")
    @JsonFormat(pattern = "yyyy-M-d")
    private LocalDateTime  shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("order_detail")
    private List<OrderDetailResponses> orderDetail;

    public static OrderResponse fromOrderRespone(Order order) {
        List<OrderDetailResponses> detailResponses = order.getOrderDetails()
                .stream()
                .map(detail -> OrderDetailResponses.builder()
                        .id(detail.getId())
                        .orderId(order.getId())
                        .productId(detail.getProduct().getId())
                        .productName(detail.getProduct().getName())
                        .price(detail.getPrice())
                        .numberOfProducts(detail.getNumberOfProduct())
                        .totalMoney(detail.getTotalMoney())
                        .color(detail.getColor())
                        .build())
                .toList();


        return OrderResponse.builder()
                .orderId(order.getId())
                .user_id(order.getUser().getId())
                .fullName(order.getFullName())
                .email(order.getEmail())
                .address(order.getAddress())
                .phoneNumber(order.getPhoneNumber())
                .orderDate(order.getOrderDate())
                .note(order.getNote())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney().floatValue())
                .shippingAddress(order.getShippingAddress())
                .shippingMethod(order.getShippingMethod())
                .shippingDate(order.getShippingDate())
                .paymentMethod(order.getPaymentMethod())
                .orderDetail(detailResponses)
                .build();
    }



}
