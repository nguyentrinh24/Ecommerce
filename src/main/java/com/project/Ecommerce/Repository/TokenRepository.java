package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token,Long> {
    List<Token> findByUser(User user);

    Token findByToken(String token);

    Token findByRefreshToken(String token);

}
