package com.project.Ecommerce.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDTOs {

    @JsonProperty("user_id")
    @Size(min = 1,message = "user id > 0")
    private int user_id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;


    @Size(min=5,message = "phon")
    @JsonProperty("phone number must at be later 5 characters")
    private String phoneNumber;


    private String note;
    private String status;

    @Size(min=0,message = "total money must be >=0")
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
