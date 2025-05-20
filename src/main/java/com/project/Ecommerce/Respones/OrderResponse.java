package com.project.Ecommerce.Respones;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
@Getter
@Setter


public class OrderResponse extends BaseResponses {

    @JsonProperty("user_id")
    private int user_id;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;


    private String phoneNumber;


    private String note;
    private String status;

    @JsonProperty("total_money")
    private Double totalMoney;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_date")
    private LocalDateTime shippingDate;

    @JsonProperty("payment_method")
    private String paymentMethod;
}
