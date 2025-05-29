package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder

public class OrderDTOs {

    @JsonProperty("user_id")

    private long user_id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;


    @Size(min=5,message = "phone")
    @JsonProperty("phone number must at be later 5 characters")
    private String phoneNumber;

    @JsonProperty("order_date")
    @JsonFormat(pattern = "yyyy-M-d")
    private LocalDateTime  orderDate;

    private String note;
    private String status;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_date")
    @JsonFormat(pattern = "yyyy-M-d")
    private LocalDateTime  shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("cart_item")
    private CartItemDTOS cartItem;

}
