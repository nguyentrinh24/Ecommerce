package com.project.Ecommerce.Model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name ="order")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private  User user;

    @Column(name = "fullname")
    private  String fullName;

    @Column(name = "email")
    private  String email;

    @Column(name = "phonenumber")
    private  String phoneNumber;

    @Column(name = "address")
    private  String address;

    @Column(name = "note")
    private  String note;

    @Column(name="order_date")
    private LocalDate orderDate;

    @Column(name = "status")
    private String status;

    @Column(name="total_money")
    private Double totalMoney;

    @Column(name = "shipping_method")
    private String shippingMethod;
    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "active")
    private boolean active;

}

