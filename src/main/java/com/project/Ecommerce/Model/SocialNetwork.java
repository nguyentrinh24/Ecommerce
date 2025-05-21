package com.project.Ecommerce.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_network")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SocialNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String provider;
    private String provider_id;
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;

}
