package com.project.Ecommerce.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name ="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "fullname")
    private  String fullName;

    @Column(name = "password")
    private String password;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "is_active")
    private boolean is_active;

    @Column(name = "date_brith")
    private Date date_brith;

    @Column(name = "fb_account_id")
    private int fb_account_id;

    @Column(name = "gg_account_id")
    private  int gg_account_id;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role roleId;

}
