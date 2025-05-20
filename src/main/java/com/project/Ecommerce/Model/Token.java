package com.project.Ecommerce.Model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name ="tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "token")
    private String token;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "ex_pritation_date")
    private LocalDateTime expirationDate;


    private boolean revoked;
    private boolean expired;

}
