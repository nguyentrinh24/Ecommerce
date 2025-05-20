package com.project.Ecommerce.Model;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name ="categories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "name",nullable = false)
    private String name;
}
