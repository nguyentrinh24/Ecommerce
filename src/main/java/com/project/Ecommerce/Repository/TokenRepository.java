package com.project.Ecommerce.Repository;

import com.project.Ecommerce.Model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import org.springframework.stereotype.Repository;


@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    List<Token> findAllByUser(User user);

    Token findByToken(String token);

    Token findByRefreshToken(String token);

    List<Token> token(String token);
}
