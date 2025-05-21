package com.project.Ecommerce.Model;

import com.project.Ecommerce.Service.OrderDetailService;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "order_detail")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "price")
    private Double price;

    @Column(name = "number_of_product")
    private int numberOfProduct;

    @Column(name = "total_money")
    private Double totalMoney;

    @Column(name = "color")
    private String color;


}
