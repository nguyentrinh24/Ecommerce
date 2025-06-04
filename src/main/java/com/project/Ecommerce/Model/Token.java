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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "token")
    private String token;

    @Column(name = "refresh_token",columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "ex_pritation_date")
    private LocalDateTime expirationDate;


    private boolean revoked;
    private boolean expired;

}
