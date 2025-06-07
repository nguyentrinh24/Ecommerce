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

    @Column(columnDefinition = "BIT(1)", nullable = false) // false (0): chưa bị thu hồi, true (1): đã bị thu hồi
    private boolean revoked;

    @Column(columnDefinition = "BIT(1)", nullable = false) // false (0): còn hạn, true (1): đã hết hạn
    private boolean expired;


}
